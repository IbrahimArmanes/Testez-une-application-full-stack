import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RegisterComponent } from './register.component';
import { AuthService } from '../../services/auth.service';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import { expect } from '@jest/globals';
import { HttpClientModule } from '@angular/common/http';

describe('RegisterComponent', () => {
  let component: RegisterComponent;
  let fixture: ComponentFixture<RegisterComponent>;
  let authService: jest.Mocked<AuthService>;
  let router: jest.Mocked<Router>;

  beforeEach(async () => {
    const authServiceMock = {
      register: jest.fn()
    };
    const routerMock = {
      navigate: jest.fn()
    };

    await TestBed.configureTestingModule({
      declarations: [RegisterComponent],
      imports: [
        BrowserAnimationsModule,
        HttpClientModule,
        ReactiveFormsModule,  
        MatCardModule,
        MatFormFieldModule,
        MatIconModule,
        MatInputModule,
        RouterTestingModule],
      providers: [
        { provide: AuthService, useValue: authServiceMock },
        { provide: Router, useValue: routerMock }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(RegisterComponent);
    component = fixture.componentInstance;
    authService = TestBed.inject(AuthService) as jest.Mocked<AuthService>;
    router = TestBed.inject(Router) as jest.Mocked<Router>;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize with empty form', () => {
    expect(component.form.get('email')?.value).toBe('');
    expect(component.form.get('firstName')?.value).toBe('');
    expect(component.form.get('lastName')?.value).toBe('');
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

  it('should validate required firstName field', () => {
    const firstNameControl = component.form.get('firstName');
    firstNameControl?.setValue('');
    expect(firstNameControl?.errors?.['required']).toBeTruthy();
  });

  it('should validate required lastName field', () => {
    const lastNameControl = component.form.get('lastName');
    lastNameControl?.setValue('');
    expect(lastNameControl?.errors?.['required']).toBeTruthy();
  });

  it('should validate firstName length', () => {
    const firstNameControl = component.form.get('firstName');
    firstNameControl?.setValue('2'); // Below min value of 3
    expect(firstNameControl?.errors?.['min']).toBeTruthy();
    
    firstNameControl?.setValue('21'); // Above max value of 20
    expect(firstNameControl?.errors?.['max']).toBeTruthy();
  });

  it('should validate lastName length', () => {
    const lastNameControl = component.form.get('lastName');
    lastNameControl?.setValue('2'); // Below min value of 3
    expect(lastNameControl?.errors?.['min']).toBeTruthy();
    
    lastNameControl?.setValue('21'); // Above max value of 20
    expect(lastNameControl?.errors?.['max']).toBeTruthy();
  });

  it('should validate required password field', () => {
    const passwordControl = component.form.get('password');
    passwordControl?.setValue('');
    expect(passwordControl?.errors?.['required']).toBeTruthy();
  });

  it('should validate password length', () => {
    const passwordControl = component.form.get('password');
    passwordControl?.setValue('2'); // Below min value of 3
    expect(passwordControl?.errors?.['min']).toBeTruthy();
    
    passwordControl?.setValue('41'); // Above max value of 40
    expect(passwordControl?.errors?.['max']).toBeTruthy();
  });

  it('should submit valid form and navigate to login', () => {
    authService.register.mockReturnValue(of(void 0));

    component.form.setValue({
      email: 'test@test.com',
      firstName: 'John',
      lastName: 'Doe',
      password: 'password123'
    });

    component.submit();

    expect(authService.register).toHaveBeenCalledWith({
      email: 'test@test.com',
      firstName: 'John',
      lastName: 'Doe',
      password: 'password123'
    });
    expect(router.navigate).toHaveBeenCalledWith(['/login']);
    expect(component.onError).toBeFalsy();
  });

  it('should handle registration error', () => {
    authService.register.mockReturnValue(throwError(() => new Error('Registration failed')));

    component.form.setValue({
      email: 'test@test.com',
      firstName: 'John',
      lastName: 'Doe',
      password: 'password123'
    });

    component.submit();

    expect(component.onError).toBeTruthy();
    expect(router.navigate).not.toHaveBeenCalled();
  });
});
