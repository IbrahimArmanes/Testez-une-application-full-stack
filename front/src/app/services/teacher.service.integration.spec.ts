import { TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpClientModule } from '@angular/common/http';
import { TeacherService } from './teacher.service';
import { SessionService } from './session.service';
import { RouterTestingModule } from '@angular/router/testing';
import { Router } from '@angular/router';
import { AuthGuard } from '../guards/auth.guard';
import { Teacher } from '../interfaces/teacher.interface';
import { SessionInformation } from '../interfaces/sessionInformation.interface';
import { Component } from '@angular/core';
import { HttpTestingController, HttpClientTestingModule } from '@angular/common/http/testing';
import { expect } from '@jest/globals';

// Mock components for routing
@Component({ template: '' })
class MockTeacherListComponent {}

@Component({ template: '' })
class MockTeacherDetailComponent {}

@Component({ template: '' })
class MockLoginComponent {}

describe('Teacher Service Integration', () => {
  let teacherService: TeacherService;
  let sessionService: SessionService;
  let authGuard: AuthGuard;
  let router: Router;
  let httpMock: HttpTestingController;

  const mockTeacher: Teacher = {
    id: 1,
    lastName: 'Smith',
    firstName: 'John',
    createdAt: new Date('2023-01-01'),
    updatedAt: new Date('2023-01-02')
  };

  const mockTeachers: Teacher[] = [
    mockTeacher,
    {
      id: 2,
      lastName: 'Johnson',
      firstName: 'Mary',
      createdAt: new Date('2023-02-01'),
      updatedAt: new Date('2023-02-02')
    }
  ];

  const mockSessionInfo: SessionInformation = {
    token: 'fake-jwt-token',
    type: 'Bearer',
    id: 1,
    username: 'testuser',
    firstName: 'Test',
    lastName: 'User',
    admin: false
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        RouterTestingModule.withRoutes([
          { path: 'teachers', component: MockTeacherListComponent },
          { path: 'teachers/:id', component: MockTeacherDetailComponent },
          { path: 'login', component: MockLoginComponent }
        ])
      ],
      providers: [
        TeacherService,
        SessionService,
        AuthGuard
      ],
      declarations: [
        MockTeacherListComponent,
        MockTeacherDetailComponent,
        MockLoginComponent
      ]
    });

    teacherService = TestBed.inject(TeacherService);
    sessionService = TestBed.inject(SessionService);
    authGuard = TestBed.inject(AuthGuard);
    router = TestBed.inject(Router);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  describe('Authentication Flow', () => {
    it('should redirect to login when accessing protected routes while logged out', fakeAsync(() => {
      // Setup
      sessionService.logOut();
      jest.spyOn(router, 'navigate');
      
      // Execute
      const canActivate = authGuard.canActivate();
      
      // Verify
      expect(canActivate).toBeFalsy();
      expect(router.navigate).toHaveBeenCalledWith(['login']);
    }));

    it('should allow access to protected routes when logged in', fakeAsync(() => {
      // Setup
      sessionService.logIn(mockSessionInfo);
      jest.spyOn(router, 'navigate');
      
      // Execute
      const canActivate = authGuard.canActivate();
      
      // Verify
      expect(canActivate).toBeTruthy();
      expect(router.navigate).not.toHaveBeenCalled();
    }));
  });

  describe('Teacher Service with Session', () => {
    it('should fetch all teachers when logged in', fakeAsync(() => {
      // Setup
      sessionService.logIn(mockSessionInfo);
      let result: Teacher[] | undefined;
      
      // Execute
      teacherService.all().subscribe(teachers => {
        result = teachers;
      });
      
      // Simulate backend response
      const req = httpMock.expectOne('api/teacher');
      expect(req.request.method).toBe('GET');
      req.flush(mockTeachers);
      tick();
      
      // Verify
      expect(result).toEqual(mockTeachers);
      expect(sessionService.isLogged).toBeTruthy();
    }));

    it('should fetch teacher details when logged in', fakeAsync(() => {
      // Setup
      sessionService.logIn(mockSessionInfo);
      let result: Teacher | undefined;
      
      // Execute
      teacherService.detail('1').subscribe(teacher => {
        result = teacher;
      });
      
      // Simulate backend response
      const req = httpMock.expectOne('api/teacher/1');
      expect(req.request.method).toBe('GET');
      req.flush(mockTeacher);
      tick();
      
      // Verify
      expect(result).toEqual(mockTeacher);
    }));

    it('should properly handle API errors', fakeAsync(() => {
      // Setup
      let errorResponse: any;
      
      // Execute
      teacherService.detail('999').subscribe(
        () => fail('Expected an error response'),
        error => errorResponse = error
      );
      
      // Simulate backend error
      const req = httpMock.expectOne('api/teacher/999');
      req.flush('Not found', { status: 404, statusText: 'Not Found' });
      tick();
      
      // Verify
      expect(errorResponse.status).toBe(404);
    }));
  });

  describe('Session State Management with Teacher Data', () => {
    it('should emit login state changes through observable', fakeAsync(() => {
      // Setup
      const loginStates: boolean[] = [];
      sessionService.$isLogged().subscribe(state => loginStates.push(state));
      
      // Execute initial state
      expect(loginStates[0]).toBeFalsy(); // Initial state should be false
      
      // Log in
      sessionService.logIn(mockSessionInfo);
      tick();
      
      // Log out
      sessionService.logOut();
      tick();
      
      // Verify state changes were emitted
      expect(loginStates).toEqual([false, true, false]);
    }));

    it('should maintain session information when logged in', () => {
      // Setup & Execute
      sessionService.logIn(mockSessionInfo);
      
      // Verify
      expect(sessionService.sessionInformation).toEqual(mockSessionInfo);
      expect(sessionService.isLogged).toBeTruthy();
    });

    it('should clear session information when logged out', () => {
      // Setup
      sessionService.logIn(mockSessionInfo);
      
      // Execute
      sessionService.logOut();
      
      // Verify
      expect(sessionService.sessionInformation).toBeUndefined();
      expect(sessionService.isLogged).toBeFalsy();
    });
  });

  describe('Teacher Data Access Scenarios', () => {
    it('should allow fetching teachers after login', fakeAsync(() => {
      // Setup - Initially not logged in
      sessionService.logOut();
      
      // Try fetching teachers
      let initialResult: Teacher[] | undefined;
      let initialError: any;
      teacherService.all().subscribe(
        teachers => initialResult = teachers,
        error => initialError = error
      );
      
      // Simulate response
      const req1 = httpMock.expectOne('api/teacher');
      req1.flush(mockTeachers);
      tick();
      
      // Now log in and try again
      sessionService.logIn(mockSessionInfo);
      let loggedInResult: Teacher[] | undefined;
      
      teacherService.all().subscribe(
        teachers => loggedInResult = teachers,
        error => fail(`Should not fail after login: ${error}`)
      );
      
      // Simulate response
      const req2 = httpMock.expectOne('api/teacher');
      req2.flush(mockTeachers);
      tick();
      
      // Verify both attempts (in real app, first might fail due to interceptors)
      expect(initialResult).toEqual(mockTeachers);
      expect(loggedInResult).toEqual(mockTeachers);
      expect(sessionService.isLogged).toBeTruthy();
    }));

    it('should handle concurrent teacher data requests', fakeAsync(() => {
      // Setup
      sessionService.logIn(mockSessionInfo);
      
      // Make multiple concurrent requests
      let allTeachersResult: Teacher[] | undefined;
      let teacherDetailsResult: Teacher | undefined;
      
      // Request 1: Get all teachers
      teacherService.all().subscribe(
        teachers => allTeachersResult = teachers
      );
      
      // Request 2: Get teacher details
      teacherService.detail('1').subscribe(
        teacher => teacherDetailsResult = teacher
      );
      
      // Handle both requests
      const allReq = httpMock.expectOne('api/teacher');
      const detailReq = httpMock.expectOne('api/teacher/1');
      
      allReq.flush(mockTeachers);
      detailReq.flush(mockTeacher);
      tick();
      
      // Verify both requests succeeded
      expect(allTeachersResult).toEqual(mockTeachers);
      expect(teacherDetailsResult).toEqual(mockTeacher);
    }));
  });
});
