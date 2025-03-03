import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { FormComponent } from './form.component';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { SessionService } from 'src/app/services/session.service';
import { TeacherService } from 'src/app/services/teacher.service';
import { SessionApiService } from '../../services/session-api.service';
import { Router, ActivatedRoute, convertToParamMap } from '@angular/router';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { of } from 'rxjs';
import { expect } from '@jest/globals';
import { CUSTOM_ELEMENTS_SCHEMA, Component, NO_ERRORS_SCHEMA } from '@angular/core';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { Teacher } from 'src/app/interfaces/teacher.interface';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { Session } from '../../interfaces/session.interface';

// Mock components for routing tests
@Component({ template: '' })
class MockSessionsListComponent {}

@Component({ template: '' })
class MockLoginComponent {}

describe('FormComponent Integration', () => {
  let component: FormComponent;
  let fixture: ComponentFixture<FormComponent>;
  let sessionService: SessionService;
  let teacherService: TeacherService;
  let sessionApiService: SessionApiService;
  let router: Router;
  let httpMock: HttpTestingController;
  let matSnackBar: MatSnackBar;

  const mockTeachers: Teacher[] = [
    {
      id: 1,
      lastName: 'Smith',
      firstName: 'John',
      createdAt: new Date('2023-01-01'),
      updatedAt: new Date('2023-01-02')
    },
    {
      id: 2,
      lastName: 'Johnson',
      firstName: 'Mary',
      createdAt: new Date('2023-02-01'),
      updatedAt: new Date('2023-02-02')
    }
  ];

  const mockSessionData: Session = {
    id: 1,
    name: 'Yoga Session',
    date: new Date('2023-01-01'),
    teacher_id: 1,
    description: 'Test description',
    createdAt: new Date('2023-01-01'),
    updatedAt: new Date('2023-01-01'),
    users: []
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [
        FormComponent,
        MockSessionsListComponent,
        MockLoginComponent
      ],
      imports: [
        ReactiveFormsModule,
        RouterTestingModule.withRoutes([
          { path: 'sessions', component: MockSessionsListComponent },
          { path: 'login', component: MockLoginComponent }
        ]),
        HttpClientTestingModule,
        MatSnackBarModule,
        NoopAnimationsModule
      ],
      providers: [
        SessionService,
        TeacherService,
        SessionApiService,
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              paramMap: convertToParamMap({ id: '1' })
            }
          }
        },
        {
          provide: MatSnackBar,
          useValue: {
            open: jest.fn()
          }
        }
      ],
      schemas: [CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA]
    });

    fixture = TestBed.createComponent(FormComponent);
    component = fixture.componentInstance;
    sessionService = TestBed.inject(SessionService);
    teacherService = TestBed.inject(TeacherService);
    sessionApiService = TestBed.inject(SessionApiService);
    router = TestBed.inject(Router);
    httpMock = TestBed.inject(HttpTestingController);
    matSnackBar = TestBed.inject(MatSnackBar);

    // Spy on router navigate
    jest.spyOn(router, 'navigate');
  });

  afterEach(() => {
    // Verify and flush any pending requests after each test
    httpMock.verify();
  });

  describe('Access Control', () => {
    it('should redirect non-admin users to sessions list', fakeAsync(() => {
        // Setup with non-admin user
        sessionService.sessionInformation = {
          token: 'fake-token',
          type: 'Bearer',
          id: 1,
          username: 'test',
          firstName: 'Test',
          lastName: 'User',
          admin: false
        };
        
        // Initialize component
        fixture.detectChanges();
        tick();
        
        // Should redirect to sessions list
        expect(router.navigate).toHaveBeenCalledWith(['/sessions']);
        
        // No HTTP requests should happen for non-admin users
        // No need to test for teacher requests
      }));

    it('should allow admin users to access the form', fakeAsync(() => {
      // Setup with admin user
      sessionService.sessionInformation = {
        token: 'fake-token',
        type: 'Bearer',
        id: 1,
        username: 'test',
        firstName: 'Test',
        lastName: 'User',
        admin: true
      };
      
      // Mock URL to be create mode
      jest.spyOn(router, 'url', 'get').mockReturnValue('/sessions/create');
      
      // Initialize component
      fixture.detectChanges();
      tick();
      
      // Handle HTTP request for teachers
      const teacherReq = httpMock.expectOne('api/teacher');
      teacherReq.flush(mockTeachers);
      
      // Form should be accessible
      expect(component.sessionForm).toBeTruthy();
      
      // Should not redirect
      expect(router.navigate).not.toHaveBeenCalled();
    }));
  });

  describe('Form Initialization', () => {
    beforeEach(() => {
      // Set admin user for these tests
      sessionService.sessionInformation = {
        token: 'fake-token',
        type: 'Bearer',
        id: 1,
        username: 'test',
        firstName: 'Test',
        lastName: 'User',
        admin: true
      };
    });

    it('should initialize form in create mode with empty values', fakeAsync(() => {
      // Mock URL to be create mode
      jest.spyOn(router, 'url', 'get').mockReturnValue('/sessions/create');
      
      // Initialize component
      fixture.detectChanges();
      tick();
      
      // Handle HTTP request for teachers
      const teacherReq = httpMock.expectOne('api/teacher');
      teacherReq.flush(mockTeachers);
      
      // Should be in create mode
      expect(component.onUpdate).toBeFalsy();
      
      // Form should have default empty values
      expect(component.sessionForm?.get('name')?.value).toBe('');
      expect(component.sessionForm?.get('date')?.value).toBe('');
      expect(component.sessionForm?.get('teacher_id')?.value).toBe('');
      expect(component.sessionForm?.get('description')?.value).toBe('');
    }));

    it('should initialize form in update mode with existing values', fakeAsync(() => {
      // Mock URL to be update mode
      jest.spyOn(router, 'url', 'get').mockReturnValue('/sessions/update/1');
      
      // Initialize component
      fixture.detectChanges();
      tick();
      
      // In update mode, first the component makes a request to get session details
      const sessionReq = httpMock.expectOne('api/session/1');
      expect(sessionReq.request.method).toBe('GET');
      sessionReq.flush(mockSessionData);
      
      // THEN it requests teachers (order matters here)
      const teacherReq = httpMock.expectOne('api/teacher');
      expect(teacherReq.request.method).toBe('GET');
      teacherReq.flush(mockTeachers);
      
      // Call detectChanges after HTTP requests are handled
      fixture.detectChanges();
      
      // Should be in update mode
      expect(component.onUpdate).toBeTruthy();
      
      // Date formatting happens in the component
      const formattedDate = new Date(mockSessionData.date).toISOString().split('T')[0];
      
      // Form should be populated with session data
      expect(component.sessionForm?.get('name')?.value).toBe(mockSessionData.name);
      expect(component.sessionForm?.get('date')?.value).toBe(formattedDate);
      expect(component.sessionForm?.get('teacher_id')?.value).toBe(mockSessionData.teacher_id);
      expect(component.sessionForm?.get('description')?.value).toBe(mockSessionData.description);
    }));
  });

  describe('Teacher List Integration', () => {
    beforeEach(() => {
      // Set admin user for these tests
      sessionService.sessionInformation = {
        token: 'fake-token',
        type: 'Bearer',
        id: 1,
        username: 'test',
        firstName: 'Test',
        lastName: 'User',
        admin: true
      };
    });

    it('should load teachers from the teacher service', fakeAsync(() => {
        // Mock URL to be create mode
        jest.spyOn(router, 'url', 'get').mockReturnValue('/sessions/create');
        
        // Initialize component
        fixture.detectChanges();
        
        // Handle HTTP request for teachers
        const teacherReq = httpMock.expectOne('api/teacher');
        expect(teacherReq.request.method).toBe('GET');
        teacherReq.flush(mockTeachers);
        
        // Process all pending promises and timers
        tick();
        
        // Since teachers$ is a direct reference to the service method return value,
        // We need to verify it contains the expected data
        component.teachers$.subscribe(teachers => {
          expect(teachers).toEqual(mockTeachers);
          expect(teachers.length).toBe(2);
        });
        
        // Make sure subscription runs
        tick();
      }));
  });

  describe('Form Submission', () => {
    beforeEach(() => {
      // Set admin user for these tests
      sessionService.sessionInformation = {
        token: 'fake-token',
        type: 'Bearer',
        id: 1,
        username: 'test',
        firstName: 'Test',
        lastName: 'User',
        admin: true
      };
    });

    it('should create a new session when submitted in create mode', fakeAsync(() => {
      // Mock URL to be create mode
      jest.spyOn(router, 'url', 'get').mockReturnValue('/sessions/create');
      
      // Initialize component
      fixture.detectChanges();
      tick();
      
      // Handle HTTP request for teachers
      const teacherReq = httpMock.expectOne('api/teacher');
      teacherReq.flush(mockTeachers);
      
      // Set form values
      component.sessionForm?.setValue({
        name: 'New Session',
        date: '2023-05-01',
        teacher_id: 1,
        description: 'New session description'
      });
      
      // Submit form
      component.submit();
      
      // Handle HTTP request for session creation
      const createReq = httpMock.expectOne('api/session');
      expect(createReq.request.method).toBe('POST');
      expect(createReq.request.body).toEqual({
        name: 'New Session',
        date: '2023-05-01',
        teacher_id: 1,
        description: 'New session description'
      });
      createReq.flush(mockSessionData);
      
      // Flush any pending promises
      tick();
      
      // Should show success message
      expect(matSnackBar.open).toHaveBeenCalledWith('Session created !', 'Close', { duration: 3000 });
      
      // Should navigate back to sessions list
      expect(router.navigate).toHaveBeenCalledWith(['sessions']);
    }));

    it('should update an existing session when submitted in update mode', fakeAsync(() => {
        // Mock URL to be update mode
        jest.spyOn(router, 'url', 'get').mockReturnValue('/sessions/update/1');
        
        // Initialize component
        fixture.detectChanges();
        tick();
        
        // First it gets session details
        const sessionReq = httpMock.expectOne('api/session/1');
        sessionReq.flush(mockSessionData);
        
        // Need tick() after receiving session data
        tick();
        fixture.detectChanges();
        
        // Update form values
        component.sessionForm?.setValue({
          name: 'Updated Session',
          date: '2023-05-15',
          teacher_id: 2,
          description: 'Updated description'
        });
        
        // Submit the form
        component.submit();
        
        // Handle HTTP request for session update
        const updateReq = httpMock.expectOne('api/session/1');
        expect(updateReq.request.method).toBe('PUT');
        expect(updateReq.request.body).toEqual({
          name: 'Updated Session',
          date: '2023-05-15',
          teacher_id: 2,
          description: 'Updated description'
        });
        updateReq.flush({...mockSessionData, 
          name: 'Updated Session',
          date: '2023-05-15',
          teacher_id: 2,
          description: 'Updated description'
        });
        
        // Flush any pending promises
        tick();
        
        // Should show success message
        expect(matSnackBar.open).toHaveBeenCalledWith('Session updated !', 'Close', { duration: 3000 });
        
        // Should navigate back to sessions list
        expect(router.navigate).toHaveBeenCalledWith(['sessions']);
      }));
      

    it('should handle API errors during form submission', fakeAsync(() => {
        // Mock URL to be create mode
        jest.spyOn(router, 'url', 'get').mockReturnValue('/sessions/create');
        
        // Initialize component
        fixture.detectChanges();
        tick();
        
        // Handle HTTP request for teachers
        const teacherReq = httpMock.expectOne('api/teacher');
        teacherReq.flush(mockTeachers);
        
        // Set form values
        component.sessionForm?.setValue({
          name: 'New Session',
          date: '2023-05-01',
          teacher_id: 1,
          description: 'New session description'
        });
        
        // Submit form
        component.submit();
        
        // Handle HTTP request for session creation but respond with error
        const createReq = httpMock.expectOne('api/session');
        expect(createReq.request.method).toBe('POST');
        createReq.flush('Error creating session', { 
          status: 500, 
          statusText: 'Internal Server Error' 
        });
        
        // Expect the error to be caught and not crash the test
        try {
          tick(); 
        } catch (error) {
          // Expected error
        }
        
        // Should not show success message or navigate
        expect(matSnackBar.open).not.toHaveBeenCalledWith('Session created !', 'Close', { duration: 3000 });
        expect(router.navigate).not.toHaveBeenCalledWith(['sessions']);
      }));
    });
  
    describe('Form Validation', () => {
      beforeEach(() => {
        // Set admin user for these tests
        sessionService.sessionInformation = {
          token: 'fake-token',
          type: 'Bearer',
          id: 1,
          username: 'test',
          firstName: 'Test',
          lastName: 'User',
          admin: true
        };
        
        // Mock URL to be create mode
        jest.spyOn(router, 'url', 'get').mockReturnValue('/sessions/create');
      });
  
      it('should validate required fields', fakeAsync(() => {
        // Initialize component
        fixture.detectChanges();
        tick();
        
        // Handle HTTP request for teachers
        const teacherReq = httpMock.expectOne('api/teacher');
        teacherReq.flush(mockTeachers);
        
        // Form should be invalid when empty
        expect(component.sessionForm?.valid).toBeFalsy();
        
        // Mark all fields as touched to trigger validation
        Object.keys(component.sessionForm?.controls || {}).forEach(key => {
          const control = component.sessionForm?.get(key);
          control?.markAsTouched();
        });
        
        // Check each required field shows errors
        expect(component.sessionForm?.get('name')?.errors?.['required']).toBeTruthy();
        expect(component.sessionForm?.get('date')?.errors?.['required']).toBeTruthy();
        expect(component.sessionForm?.get('teacher_id')?.errors?.['required']).toBeTruthy();
        expect(component.sessionForm?.get('description')?.errors?.['required']).toBeTruthy();
        
        // Set values for all fields
        component.sessionForm?.setValue({
          name: 'Test Session',
          date: '2023-05-01',
          teacher_id: 1,
          description: 'Valid description'
        });
        
        // Form should now be valid
        expect(component.sessionForm?.valid).toBeTruthy();
      }));
  
      it('should validate description max length', fakeAsync(() => {
        // Initialize component
        fixture.detectChanges();
        tick();
        
        // Handle HTTP request for teachers
        const teacherReq = httpMock.expectOne('api/teacher');
        teacherReq.flush(mockTeachers);
        
        // Fill out the form with valid values first
        component.sessionForm?.setValue({
          name: 'Test Session',
          date: '2023-05-01',
          teacher_id: 1,
          description: 'Valid description'
        });
        
        // Form should be valid
        expect(component.sessionForm?.valid).toBeTruthy();
        
        // We test with max validator 2000 as that's what the component uses
        component.sessionForm?.get('description')?.setValue(2001);
        
        // Description should now be invalid with a "max" error
        expect(component.sessionForm?.get('description')?.errors?.['max']).toBeTruthy();
        
        // Fix the description
        component.sessionForm?.get('description')?.setValue(1999);
        
        // Should now be valid again
        expect(component.sessionForm?.get('description')?.valid).toBeTruthy();
      }));
    });
  
    describe('Error Handling', () => {
      beforeEach(() => {
        // Set admin user
        sessionService.sessionInformation = {
          token: 'fake-token',
          type: 'Bearer',
          id: 1,
          username: 'test',
          firstName: 'Test',
          lastName: 'User',
          admin: true
        };
      });
  
      it('should handle session detail API errors gracefully', fakeAsync(() => {
        // Mock URL to be update mode
        jest.spyOn(router, 'url', 'get').mockReturnValue('/sessions/update/1');
        
        // Initialize component
        fixture.detectChanges();
        tick();
        
        // Handle HTTP request for session details but return error
        const sessionReq = httpMock.expectOne('api/session/1');
        sessionReq.flush('Session not found', { 
          status: 404, 
          statusText: 'Not Found' 
        });
        
        // Process the error response
        tick();
        
        // Component should handle the error without crashing
        expect(component).toBeTruthy();
        
        // Component should still be in update mode
        expect(component.onUpdate).toBeTruthy();
      }));
    });
  
    describe('Session State Integration', () => {
      it('should maintain form state across login status changes', fakeAsync(() => {
        // Start with admin user
        sessionService.sessionInformation = {
          token: 'fake-token',
          type: 'Bearer',
          id: 1,
          username: 'test',
          firstName: 'Test',
          lastName: 'User',
          admin: true
        };
        
        // Mock URL for create mode
        jest.spyOn(router, 'url', 'get').mockReturnValue('/sessions/create');
        
        // Initialize component
        fixture.detectChanges();
        tick();
        
        // Handle HTTP request for teachers
        const teacherReq = httpMock.expectOne('api/teacher');
        teacherReq.flush(mockTeachers);
        
        // Fill form with data
        component.sessionForm?.setValue({
          name: 'New Session',
          date: '2023-05-01',
          teacher_id: 1,
          description: 'Test description'
        });
        
        // Simulate admin status change (logout/login as non-admin)
        sessionService.sessionInformation = {
          token: 'fake-token',
          type: 'Bearer',
          id: 1,
          username: 'test',
          firstName: 'Test',
          lastName: 'User',
          admin: false
        };
        
        // Trigger ngOnInit again
        component.ngOnInit();
        tick();
        
        // Should redirect to sessions list for non-admin
        expect(router.navigate).toHaveBeenCalledWith(['/sessions']);
      }));
    });
  
    describe('Integration with multiple components', () => {
      it('should properly interact with RouterModule for navigation', fakeAsync(() => {
        // Setup admin user
        sessionService.sessionInformation = {
          token: 'fake-token',
          type: 'Bearer',
          id: 1,
          username: 'test',
          firstName: 'Test',
          lastName: 'User',
          admin: true
        };
        
        // Mock URL for create mode
        jest.spyOn(router, 'url', 'get').mockReturnValue('/sessions/create');
        
        // Initialize component
        fixture.detectChanges();
        tick();
        
        // Handle HTTP request for teachers
        const teacherReq = httpMock.expectOne('api/teacher');
        teacherReq.flush(mockTeachers);
        
        // Fill and submit form
        component.sessionForm?.setValue({
          name: 'New Session',
          date: '2023-05-01',
          teacher_id: 1,
          description: 'Test description'
        });
        
        component.submit();
        
        // Handle HTTP request for session creation
        const createReq = httpMock.expectOne('api/session');
        createReq.flush(mockSessionData);
        
        // Flush router navigation
        tick();
        
        // Verify navigation was attempted
        expect(router.navigate).toHaveBeenCalledWith(['sessions']);
      }));
    });
  });
  
