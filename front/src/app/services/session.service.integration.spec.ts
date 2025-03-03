import { TestBed, fakeAsync, tick } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { Router } from '@angular/router';
import { Component } from '@angular/core';
import { SessionService } from './session.service';
import { AuthGuard } from '../guards/auth.guard';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { SessionInformation } from '../interfaces/sessionInformation.interface';
import { expect } from '@jest/globals';
import { Location } from '@angular/common';

// Mock components for routing
@Component({ template: '<div>Home Component</div>' })
class MockHomeComponent {}

@Component({ template: '<div>Login Component</div>' })
class MockLoginComponent {}

@Component({ 
  template: '<div>Protected Component</div>',
  providers: [] 
})
class MockProtectedComponent {}

@Component({ template: '<div>Admin Component</div>' })
class MockAdminComponent {}

describe('Session Service Integration', () => {
  let sessionService: SessionService;
  let authGuard: AuthGuard;
  let router: Router;
  let location: Location;
  let httpMock: HttpTestingController;

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

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        RouterTestingModule.withRoutes([
          { path: '', component: MockHomeComponent },
          { path: 'login', component: MockLoginComponent },
          { 
            path: 'protected', 
            component: MockProtectedComponent,
            canActivate: [AuthGuard]
          },
          { 
            path: 'admin', 
            component: MockAdminComponent,
            canActivate: [AuthGuard]
          }
        ])
      ],
      providers: [
        SessionService,
        AuthGuard
      ],
      declarations: [
        MockHomeComponent,
        MockLoginComponent,
        MockProtectedComponent,
        MockAdminComponent
      ]
    });

    sessionService = TestBed.inject(SessionService);
    authGuard = TestBed.inject(AuthGuard);
    router = TestBed.inject(Router);
    location = TestBed.inject(Location);
    httpMock = TestBed.inject(HttpTestingController);

    // Start at home page
    router.initialNavigation();
  });

  afterEach(() => {
    httpMock.verify();
  });

  describe('Authentication Flow with Routing', () => {
    it('should block navigation to protected routes when not logged in', fakeAsync(() => {
      // Ensure logged out
      sessionService.logOut();
      
      // Try to navigate to protected route
      router.navigate(['/protected']);
      tick();
      
      // Should be redirected to login
      expect(location.path()).toBe('/login');
    }));

    it('should allow navigation to protected routes after login', fakeAsync(() => {
      // First login
      sessionService.logIn(mockSessionInfo);
      
      // Then navigate to protected route
      router.navigate(['/protected']);
      tick();
      
      // Should be on protected route
      expect(location.path()).toBe('/protected');
    }));

    it('should redirect to login when accessing protected routes after logout', fakeAsync(() => {
        // First login and navigate to protected route
        sessionService.logIn(mockSessionInfo);
        router.navigate(['/protected']);
        tick();
        expect(location.path()).toBe('/protected');
        
        // Then logout
        sessionService.logOut();
        
        // Need to explicitly trigger route refresh to activate guards
        router.navigate(['/']); // First navigate away
        tick();
        
        // Now try to access protected route
        router.navigate(['/protected']);
        tick();
        
        // Should be redirected to login
        expect(location.path()).toBe('/login');
      }));
  });

  describe('Session State Management', () => {
    it('should correctly track login state through observable', fakeAsync(() => {
      // Track login states
      const loginStates: boolean[] = [];
      const subscription = sessionService.$isLogged().subscribe(
        state => loginStates.push(state)
      );
      
      // Initial state should be logged out
      expect(loginStates[0]).toBeFalsy();
      
      // Login and verify state change
      sessionService.logIn(mockSessionInfo);
      tick();
      expect(loginStates[1]).toBeTruthy();
      
      // Logout and verify state change
      sessionService.logOut();
      tick();
      expect(loginStates[2]).toBeFalsy();
      
      // Verify sequence of states
      expect(loginStates).toEqual([false, true, false]);
      
      subscription.unsubscribe();
    }));

    it('should maintain correct session information', () => {
      // Start with no session
      expect(sessionService.sessionInformation).toBeUndefined();
      
      // Login and check session info
      sessionService.logIn(mockSessionInfo);
      expect(sessionService.sessionInformation).toEqual(mockSessionInfo);
      expect(sessionService.isLogged).toBeTruthy();
      
      // Logout and check session info cleared
      sessionService.logOut();
      expect(sessionService.sessionInformation).toBeUndefined();
      expect(sessionService.isLogged).toBeFalsy();
    });

    it('should allow updating session information', () => {
      // Login with regular user
      sessionService.logIn(mockSessionInfo);
      expect(sessionService.sessionInformation?.admin).toBeFalsy();
      
      // Update to admin user
      sessionService.logIn(mockAdminSessionInfo);
      expect(sessionService.sessionInformation?.admin).toBeTruthy();
    });
  });

  describe('Integration with AuthGuard', () => {
    it('should block access via AuthGuard when not logged in', () => {
      // Ensure logged out
      sessionService.logOut();
      
      // Check AuthGuard directly
      const canActivate = authGuard.canActivate();
      expect(canActivate).toBeFalsy();
    });

    it('should allow access via AuthGuard when logged in', () => {
      // Login first
      sessionService.logIn(mockSessionInfo);
      
      // Check AuthGuard directly
      const canActivate = authGuard.canActivate();
      expect(canActivate).toBeTruthy();
    });
  });

  describe('Multiple Navigation Scenarios', () => {
    it('should handle multiple route changes during session changes', fakeAsync(() => {
      // Login and go to protected
      sessionService.logIn(mockSessionInfo);
      router.navigate(['/protected']);
      tick();
      expect(location.path()).toBe('/protected');
      
      // Logout - should still be on protected until next navigation
      sessionService.logOut();
      tick();
      expect(location.path()).toBe('/protected');
      
      // Try to go to another protected route
      router.navigate(['/admin']);
      tick();
      expect(location.path()).toBe('/login');
      
      // Login again
      sessionService.logIn(mockSessionInfo);
      tick();
      
      // Should now be able to access protected routes
      router.navigate(['/protected']);
      tick();
      expect(location.path()).toBe('/protected');
    }));

    it('should persist login state across multiple navigations', fakeAsync(() => {
      // Login
      sessionService.logIn(mockSessionInfo);
      
      // Navigate through multiple routes
      router.navigate(['/']);
      tick();
      expect(location.path()).toBe('/');
      
      router.navigate(['/protected']);
      tick();
      expect(location.path()).toBe('/protected');
      
      router.navigate(['/']);
      tick();
      expect(location.path()).toBe('/');
      
      router.navigate(['/protected']);
      tick();
      expect(location.path()).toBe('/protected');
      
      // Should still be logged in
      expect(sessionService.isLogged).toBeTruthy();
    }));
  });

  describe('Observer Notifications', () => {
    it('should notify all subscribers when login state changes', fakeAsync(() => {
      // Create multiple subscribers
      const states1: boolean[] = [];
      const states2: boolean[] = [];
      
      const sub1 = sessionService.$isLogged().subscribe(state => states1.push(state));
      const sub2 = sessionService.$isLogged().subscribe(state => states2.push(state));
      
      // Login
      sessionService.logIn(mockSessionInfo);
      tick();
      
      // Logout
      sessionService.logOut();
      tick();
      
      // Both should have received the same notifications
      expect(states1).toEqual([false, true, false]);
      expect(states2).toEqual([false, true, false]);
      
      sub1.unsubscribe();
      sub2.unsubscribe();
    }));

    it('should provide correct initial state to new subscribers', fakeAsync(() => {
      // Login first
      sessionService.logIn(mockSessionInfo);
      tick();
      
      // New subscriber should get current state
      const states: boolean[] = [];
      const sub = sessionService.$isLogged().subscribe(state => states.push(state));
      tick();
      
      // Should immediately get logged in state
      expect(states).toEqual([true]);
      
      sub.unsubscribe();
    }));
  });
});
