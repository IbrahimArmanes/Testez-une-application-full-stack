import { ComponentFixture, TestBed } from '@angular/core/testing';
import { LoginComponent } from './login.component';
import { AuthService } from '../../services/auth.service';
import { SessionService } from 'src/app/services/session.service';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import { expect, test } from '@jest/globals';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { HttpClientModule } from '@angular/common/http';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';

describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;
  let authService: jest.Mocked<AuthService>;
  let sessionService: jest.Mocked<SessionService>;
  let router: jest.Mocked<Router>;

  beforeEach(async () => {
    const authServiceMock = {
      login: jest.fn()
    };
    const sessionServiceMock = {
      logIn: jest.fn()
    };
    const routerMock = {
      navigate: jest.fn()
    };

    await TestBed.configureTestingModule({
      declarations: [LoginComponent],
      imports: [
        RouterTestingModule,
        BrowserAnimationsModule,
        HttpClientModule,
        MatCardModule,
        MatIconModule,
        MatFormFieldModule,
        MatInputModule,
        ReactiveFormsModule
      ],
      providers: [
        { provide: AuthService, useValue: authServiceMock },
        { provide: SessionService, useValue: sessionServiceMock },
        { provide: Router, useValue: routerMock }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    authService = TestBed.inject(AuthService) as jest.Mocked<AuthService>;
    sessionService = TestBed.inject(SessionService) as jest.Mocked<SessionService>;
    router = TestBed.inject(Router) as jest.Mocked<Router>;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize with empty form and hidden password', () => {
    expect(component.hide).toBeTruthy();
    expect(component.form.get('email')?.value).toBe('');
    expect(component.form.get('password')?.value).toBe('');
  });

  it('should validate required email field', () => {
    const emailControl = component.form.get('email');
    emailControl?.setValue('');
    expect(emailControl?.errors?.['required']).toBeTruthy();
  });

  it('should validate email format', () => {
    const emailControl = component.form.get('email');
    emailControl?.setValue('invalid-email');
    expect(emailControl?.errors?.['email']).toBeTruthy();
  });

  it('should validate required password field', () => {
    const passwordControl = component.form.get('password');
    passwordControl?.setValue('');
    expect(passwordControl?.errors?.['required']).toBeTruthy();
  });

  it('should submit valid form and navigate to sessions', () => {
    const mockSessionInfo = {
      token: 'fake-token',
      type: 'Bearer',
      id: 1,
      username: 'test',
      firstName: 'Test',
      lastName: 'User',
      admin: false
    };

    authService.login.mockReturnValue(of(mockSessionInfo));

    component.form.setValue({
      email: 'test@test.com',
      password: 'password123'
    });

    component.submit();

    expect(authService.login).toHaveBeenCalledWith({
      email: 'test@test.com',
      password: 'password123'
    });
    expect(sessionService.logIn).toHaveBeenCalledWith(mockSessionInfo);
    expect(router.navigate).toHaveBeenCalledWith(['/sessions']);
    expect(component.onError).toBeFalsy();
  });

  it('should handle login error', () => {
    authService.login.mockReturnValue(throwError(() => new Error('Login failed')));

    component.form.setValue({
      email: 'test@test.com',
      password: 'wrong-password'
    });

    component.submit();

    expect(component.onError).toBeTruthy();
    expect(sessionService.logIn).not.toHaveBeenCalled();
    expect(router.navigate).not.toHaveBeenCalled();
  });
});