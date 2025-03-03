import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { AuthService } from './auth.service';
import { LoginRequest } from '../interfaces/loginRequest.interface';
import { RegisterRequest } from '../interfaces/registerRequest.interface';
import { SessionInformation } from 'src/app/interfaces/sessionInformation.interface';
import { expect } from '@jest/globals';

describe('AuthService', () => {
  let service: AuthService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [AuthService]
    });
    service = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should send POST request for registration', () => {
    const mockRegisterRequest: RegisterRequest = {
      email: 'test@test.com',
      firstName: 'Test',
      lastName: 'User',
      password: 'password123'
    };

    service.register(mockRegisterRequest).subscribe();

    const req = httpMock.expectOne('api/auth/register');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(mockRegisterRequest);
    req.flush(null);
  });

  it('should send POST request for login and return session information', () => {
    const mockLoginRequest: LoginRequest = {
      email: 'test@test.com',
      password: 'password123'
    };

    const mockSessionInfo: SessionInformation = {
      token: 'fake-token',
      type: 'Bearer',
      id: 1,
      username: 'test',
      firstName: 'Test',
      lastName: 'User',
      admin: false
    };

    service.login(mockLoginRequest).subscribe(response => {
      expect(response).toEqual(mockSessionInfo);
    });

    const req = httpMock.expectOne('api/auth/login');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(mockLoginRequest);
    req.flush(mockSessionInfo);
  });
});
