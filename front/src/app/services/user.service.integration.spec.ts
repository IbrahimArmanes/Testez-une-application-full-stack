import { TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpClientModule } from '@angular/common/http';
import { UserService } from './user.service';
import { SessionService } from './session.service';
import { RouterTestingModule } from '@angular/router/testing';
import { Router } from '@angular/router';
import { AuthGuard } from '../guards/auth.guard';
import { User } from '../interfaces/user.interface';
import { SessionInformation } from '../interfaces/sessionInformation.interface';
import { Component } from '@angular/core';
import { HttpTestingController, HttpClientTestingModule } from '@angular/common/http/testing';
import { expect } from '@jest/globals';

// Mock components for routing
@Component({ template: '' })
class MockHomeComponent {}

@Component({ template: '' })
class MockLoginComponent {}

describe('User Service Integration', () => {
  let userService: UserService;
  let sessionService: SessionService;
  let authGuard: AuthGuard;
  let router: Router;
  let httpMock: HttpTestingController;

  const mockUser: User = {
    id: 1,
    email: 'user@example.com',
    lastName: 'Doe',
    firstName: 'John',
    admin: false,
    password: 'password123',
    createdAt: new Date(),
    updatedAt: new Date()
  };

  const mockSessionInfo: SessionInformation = {
    token: 'fake-jwt-token',
    type: 'Bearer',
    id: 1,
    username: 'johndoe',  
    firstName: 'John',
    lastName: 'Doe',
    admin: false
  };
  

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        RouterTestingModule.withRoutes([
          { path: 'login', component: MockLoginComponent },
          { path: '', component: MockHomeComponent }
        ])
      ],
      providers: [
        UserService,
        SessionService,
        AuthGuard
      ],
      declarations: [
        MockHomeComponent,
        MockLoginComponent
      ]
    });

    userService = TestBed.inject(UserService);
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

  describe('User Service with Session', () => {
    it('should fetch user details when logged in', fakeAsync(() => {
      // Setup
      sessionService.logIn(mockSessionInfo);
      let result: User | undefined;
      
      // Execute
      userService.getById('1').subscribe(user => {
        result = user;
      });
      
      // Simulate backend response
      const req = httpMock.expectOne('api/user/1');
      expect(req.request.method).toBe('GET');
      req.flush(mockUser);
      tick();
      
      // Verify
      expect(result).toEqual(mockUser);
      expect(sessionService.isLogged).toBeTruthy();
    }));

    it('should delete user account', fakeAsync(() => {
      // Setup
      sessionService.logIn(mockSessionInfo);
      let completed = false;
      
      // Execute
      userService.delete('1').subscribe(() => {
        completed = true;
      });
      
      // Simulate backend response
      const req = httpMock.expectOne('api/user/1');
      expect(req.request.method).toBe('DELETE');
      req.flush({});
      tick();
      
      // Verify
      expect(completed).toBeTruthy();
    }));

    it('should properly handle API errors', fakeAsync(() => {
      // Setup
      let errorResponse: any;
      
      // Execute
      userService.getById('999').subscribe(
        () => fail('Expected an error response'),
        error => errorResponse = error
      );
      
      // Simulate backend error
      const req = httpMock.expectOne('api/user/999');
      req.flush('Not found', { status: 404, statusText: 'Not Found' });
      tick();
      
      // Verify
      expect(errorResponse.status).toBe(404);
    }));
  });

  describe('Session State Management', () => {
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
});
