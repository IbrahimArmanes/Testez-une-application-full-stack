import { TestBed } from '@angular/core/testing';
import { expect } from '@jest/globals';
import { SessionService } from './session.service';

describe('SessionService', () => {
  let service: SessionService;
  
  const mockSessionInfo = {
    token: 'fake-token',
    type: 'Bearer',
    id: 1,
    username: 'test',
    firstName: 'Test',
    lastName: 'User',
    admin: false
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [SessionService]
    });
    service = TestBed.inject(SessionService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should initialize with logged out state', () => {
    expect(service.isLogged).toBeFalsy();
    expect(service.sessionInformation).toBeUndefined();
  });

  it('should handle login correctly', () => {
    service.logIn(mockSessionInfo);
    expect(service.isLogged).toBeTruthy();
    expect(service.sessionInformation).toEqual(mockSessionInfo);
  });

  it('should handle logout correctly', () => {
    service.logIn(mockSessionInfo);
    service.logOut();
    expect(service.isLogged).toBeFalsy();
    expect(service.sessionInformation).toBeUndefined();
  });

  it('should emit login status changes', (done) => {
    service.$isLogged().subscribe(isLogged => {
      expect(isLogged).toBeTruthy();
      done();
    });
    service.logIn(mockSessionInfo);
  });

  it('should emit logout status changes', (done) => {
    service.logIn(mockSessionInfo);
    service.$isLogged().subscribe(isLogged => {
      expect(isLogged).toBeFalsy();
      done();
    });
    service.logOut();
  });
});