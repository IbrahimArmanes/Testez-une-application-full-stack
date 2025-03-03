import { ComponentFixture, TestBed, fakeAsync, tick, flush } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { ActivatedRoute, Router, convertToParamMap } from '@angular/router';
import { By } from '@angular/platform-browser';
import { of } from 'rxjs';
import { expect, jest } from '@jest/globals';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';

import { DetailComponent } from './detail.component';
import { SessionService } from 'src/app/services/session.service';
import { TeacherService } from 'src/app/services/teacher.service';
import { SessionApiService } from '../../services/session-api.service';
import { SessionInformation } from 'src/app/interfaces/sessionInformation.interface';
import { Session } from '../../interfaces/session.interface';
import { Teacher } from 'src/app/interfaces/teacher.interface';

describe('DetailComponent Integration', () => {
  let component: DetailComponent;
  let fixture: ComponentFixture<DetailComponent>;
  let sessionService: SessionService;
  let teacherService: TeacherService;
  let sessionApiService: SessionApiService;
  let router: Router;

  // Mock data
  const mockSessionInfo: SessionInformation = {
    token: 'fake-jwt-token',
    type: 'Bearer',
    id: 1,
    username: 'testuser',
    firstName: 'Test',
    lastName: 'User',
    admin: false
  };

  const mockAdminSessionInfo: SessionInformation = {
    ...mockSessionInfo,
    admin: true
  };

  const mockSession: Session = {
    id: 1,
    name: 'Yoga Session',
    description: 'Test description',
    date: new Date('2023-06-01'),
    teacher_id: 1,
    users: [1, 2, 3],
    createdAt: new Date('2023-01-01'),
    updatedAt: new Date('2023-01-02')
  };

  const mockTeacher: Teacher = {
    id: 1,
    lastName: 'Doe',
    firstName: 'John',
    createdAt: new Date('2023-01-01'),
    updatedAt: new Date('2023-01-02')
  };

  beforeEach(async () => {
    // Create a session service with pre-initialized sessionInformation
    const sessionServiceMock = {
        sessionInformation: mockSessionInfo as SessionInformation | undefined,
        isLogged: true,
        // Use simpler implementation that doesn't conflict with Jest's typings
        logIn(user: any) {
          this.sessionInformation = user;
          this.isLogged = true;
        },
        logOut() {
          this.sessionInformation = undefined;
          this.isLogged = false;
        },
        $isLogged: () => of(true)
    };

    // Mock services
    const detailSpy = jest.fn().mockReturnValue(of(mockSession));
    const participateSpy = jest.fn().mockReturnValue(of({}));
    const unParticipateSpy = jest.fn().mockReturnValue(of({}));
    const deleteSpy = jest.fn().mockReturnValue(of({}));
    const teacherDetailSpy = jest.fn().mockReturnValue(of(mockTeacher));

    await TestBed.configureTestingModule({
      declarations: [DetailComponent],
      imports: [
        RouterTestingModule,
        MatSnackBarModule,
        ReactiveFormsModule,
        NoopAnimationsModule // Add NoopAnimationsModule to fix animation errors
      ],
      providers: [
        {
          provide: SessionService,
          useValue: sessionServiceMock
        },
        {
          provide: TeacherService,
          useValue: {
            detail: teacherDetailSpy
          }
        },
        {
          provide: SessionApiService,
          useValue: {
            detail: detailSpy,
            participate: participateSpy,
            unParticipate: unParticipateSpy,
            delete: deleteSpy
          }
        },
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              paramMap: {
                get: () => '1'
              }
            }
          }
        }
      ],
      schemas: [CUSTOM_ELEMENTS_SCHEMA]
    }).compileComponents();

    fixture = TestBed.createComponent(DetailComponent);
    component = fixture.componentInstance;
    sessionService = TestBed.inject(SessionService);
    teacherService = TestBed.inject(TeacherService);
    sessionApiService = TestBed.inject(SessionApiService);
    router = TestBed.inject(Router);

    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load session and teacher on init', fakeAsync(() => {
    // These should have been called during component initialization
    expect(sessionApiService.detail).toHaveBeenCalledWith('1');
    expect(teacherService.detail).toHaveBeenCalledWith('1');

    // Check that session and teacher data is set correctly
    expect(component.session).toEqual(mockSession);
    expect(component.teacher).toEqual(mockTeacher);
  }));

  it('should handle participation actions', fakeAsync(() => {
    // Test participate
    component.participate();
    tick();
    
    expect(sessionApiService.participate).toHaveBeenCalledWith('1', '1');
    expect(sessionApiService.detail).toHaveBeenCalledWith('1');
    
    // Test unParticipate
    component.unParticipate();
    tick();
    
    expect(sessionApiService.unParticipate).toHaveBeenCalledWith('1', '1');
    expect(sessionApiService.detail).toHaveBeenCalledWith('1');
  }));


  it('should check if user is admin', () => {
    // Initially not admin
    expect(sessionService.sessionInformation?.admin).toBeFalsy();
    
    // Change to admin user
    sessionService.logIn(mockAdminSessionInfo);
    fixture.detectChanges();
    
    expect(sessionService.sessionInformation?.admin).toBeTruthy();
  });

  it('should handle admin functionality', fakeAsync(() => {
    // Set user as admin for this test
    sessionService.logIn(mockAdminSessionInfo);
    fixture.detectChanges();
    
    // Test delete function
    jest.spyOn(router, 'navigate').mockImplementation(() => Promise.resolve(true));
    
    component.delete();
    tick();
    
    expect(sessionApiService.delete).toHaveBeenCalledWith('1');
    expect(router.navigate).toHaveBeenCalledWith(['sessions']);
    
    // Flush any remaining timers (important!)
    flush();
  }));

  it('should navigate back when back() is called', () => {
    const historyBackSpy = jest.spyOn(window.history, 'back').mockImplementation(() => {});
    
    component.back();
    
    expect(historyBackSpy).toHaveBeenCalled();
  });
});
