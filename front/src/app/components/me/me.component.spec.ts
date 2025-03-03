import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import { expect } from '@jest/globals';
import { of } from 'rxjs';
import { MeComponent } from './me.component';
import { SessionService } from '../../services/session.service';
import { UserService } from '../../services/user.service';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';

describe('MeComponent', () => {
  let component: MeComponent;
  let fixture: ComponentFixture<MeComponent>;
  let sessionService: jest.Mocked<SessionService>;
  let userService: jest.Mocked<UserService>;
  let router: jest.Mocked<Router>;
  let matSnackBar: jest.Mocked<MatSnackBar>;

  const mockUser = {
    id: 1,
    email: 'test@test.com',
    lastName: 'Test',
    firstName: 'User',
    admin: false,
    password: 'password',
    createdAt: new Date(),
    updatedAt: new Date()
  };

  beforeEach(async () => {
    const sessionServiceMock = {
      sessionInformation: {
        id: 1
      },
      logOut: jest.fn()
    };

    const userServiceMock = {
      getById: jest.fn().mockReturnValue(of(mockUser)),
      delete: jest.fn().mockReturnValue(of({}))
    };

    const routerMock = {
      navigate: jest.fn()
    };

    const matSnackBarMock = {
      open: jest.fn()
    };

    await TestBed.configureTestingModule({
      declarations: [MeComponent],
      providers: [
        { provide: SessionService, useValue: sessionServiceMock },
        { provide: UserService, useValue: userServiceMock },
        { provide: Router, useValue: routerMock },
        { provide: MatSnackBar, useValue: matSnackBarMock }
      ],
      schemas: [CUSTOM_ELEMENTS_SCHEMA]
    }).compileComponents();

    fixture = TestBed.createComponent(MeComponent);
    component = fixture.componentInstance;
    sessionService = TestBed.inject(SessionService) as jest.Mocked<SessionService>;
    userService = TestBed.inject(UserService) as jest.Mocked<UserService>;
    router = TestBed.inject(Router) as jest.Mocked<Router>;
    matSnackBar = TestBed.inject(MatSnackBar) as jest.Mocked<MatSnackBar>;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load user data on init', () => {
    expect(userService.getById).toHaveBeenCalledWith('1');
    expect(component.user).toEqual(mockUser);
  });

  it('should navigate back when back() is called', () => {
    const windowSpy = jest.spyOn(window.history, 'back');
    component.back();
    expect(windowSpy).toHaveBeenCalled();
  });

  it('should delete user account and logout', () => {
    component.delete();
    expect(userService.delete).toHaveBeenCalledWith('1');
    expect(matSnackBar.open).toHaveBeenCalledWith('Your account has been deleted !', 'Close', { duration: 3000 });
    expect(sessionService.logOut).toHaveBeenCalled();
    expect(router.navigate).toHaveBeenCalledWith(['/']);
  });
});