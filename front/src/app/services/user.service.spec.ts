import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { expect } from '@jest/globals';
import { UserService } from './user.service';

describe('UserService', () => {
  let service: UserService;
  let httpMock: HttpTestingController;

  const mockUser = {
    id: '1',
    email: 'test@test.com',
    lastName: 'Test',
    firstName: 'User',
    admin: false,
    password: 'password123'
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [UserService]
    });
    service = TestBed.inject(UserService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should get user by id', () => {
    service.getById('1').subscribe(user => {
      expect(user).toEqual(mockUser);
    });

    const req = httpMock.expectOne('api/user/1');
    expect(req.request.method).toBe('GET');
    req.flush(mockUser);
  });

  it('should delete user', () => {
    service.delete('1').subscribe(response => {
      expect(response).toBeTruthy();
    });

    const req = httpMock.expectOne('api/user/1');
    expect(req.request.method).toBe('DELETE');
    req.flush({});
  });
});