import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormComponent } from './form.component';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { SessionService } from 'src/app/services/session.service';
import { TeacherService } from 'src/app/services/teacher.service';
import { SessionApiService } from '../../services/session-api.service';
import { Router, ActivatedRoute } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import { of } from 'rxjs';
import { expect } from '@jest/globals';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';

describe('FormComponent', () => {
  let component: FormComponent;
  let fixture: ComponentFixture<FormComponent>;
  let sessionService: jest.Mocked<SessionService>;
  let teacherService: jest.Mocked<TeacherService>;
  let sessionApiService: jest.Mocked<SessionApiService>;
  let router: jest.Mocked<Router>;
  let matSnackBar: jest.Mocked<MatSnackBar>;

  const mockSession = {
    name: 'Yoga Session',
    date: '2023-01-01',
    teacher_id: 1,
    description: 'Test description'
  };

  beforeEach(async () => {
    const sessionServiceMock = {
      sessionInformation: {
        token: 'fake-token',
        type: 'Bearer',
        id: 1,
        username: 'test',
        firstName: 'Test',
        lastName: 'User',
        admin: true
      }
    };

    const teacherServiceMock = {
      all: jest.fn().mockReturnValue(of([]))
    };

    const sessionApiServiceMock = {
      create: jest.fn().mockReturnValue(of(mockSession)),
      update: jest.fn().mockReturnValue(of(mockSession)),
      detail: jest.fn().mockReturnValue(of(mockSession))
    };

    const routerMock = {
      navigate: jest.fn()
    };

    Object.defineProperty(routerMock, 'url', {
      get: jest.fn().mockReturnValue('/sessions/create')
    });

    const matSnackBarMock = {
      open: jest.fn()
    };

    const activatedRouteMock = {
      snapshot: {
        paramMap: {
          get: () => '1'
        }
      }
    };

    await TestBed.configureTestingModule({
      declarations: [FormComponent],
      imports: [ReactiveFormsModule, RouterTestingModule],
      providers: [
        { provide: SessionService, useValue: sessionServiceMock },
        { provide: TeacherService, useValue: teacherServiceMock },
        { provide: SessionApiService, useValue: sessionApiServiceMock },
        { provide: Router, useValue: routerMock },
        { provide: ActivatedRoute, useValue: activatedRouteMock },
        { provide: MatSnackBar, useValue: matSnackBarMock }
      ],
      schemas: [CUSTOM_ELEMENTS_SCHEMA]
    }).compileComponents();

    fixture = TestBed.createComponent(FormComponent);
    component = fixture.componentInstance;
    sessionService = TestBed.inject(SessionService) as jest.Mocked<SessionService>;
    teacherService = TestBed.inject(TeacherService) as jest.Mocked<TeacherService>;
    sessionApiService = TestBed.inject(SessionApiService) as jest.Mocked<SessionApiService>;
    router = TestBed.inject(Router) as jest.Mocked<Router>;
    matSnackBar = TestBed.inject(MatSnackBar) as jest.Mocked<MatSnackBar>;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should redirect if user is not admin', () => {
    sessionService.sessionInformation = {
      token: 'fake-token',
      type: 'Bearer',
      id: 1,
      username: 'test',
      firstName: 'Test',
      lastName: 'User',
      admin: false
    };
    component.ngOnInit();
    expect(router.navigate).toHaveBeenCalledWith(['/sessions']);
  });

  it('should initialize form for create mode', () => {
    component.ngOnInit();
    expect(component.onUpdate).toBeFalsy();
    expect(component.sessionForm?.get('name')?.value).toBe('');
    expect(component.sessionForm?.get('date')?.value).toBe('');
    expect(component.sessionForm?.get('teacher_id')?.value).toBe('');
    expect(component.sessionForm?.get('description')?.value).toBe('');
  });

  it('should initialize form for update mode', () => {
    jest.spyOn(router, 'url', 'get').mockReturnValue('/sessions/update/1');
    component.ngOnInit();
    expect(component.onUpdate).toBeTruthy();
    expect(sessionApiService.detail).toHaveBeenCalledWith('1');
  });

  it('should create new session', () => {
    component.ngOnInit();
    component.sessionForm?.setValue(mockSession);
    component.submit();
    expect(sessionApiService.create).toHaveBeenCalledWith(mockSession);
    expect(matSnackBar.open).toHaveBeenCalledWith('Session created !', 'Close', { duration: 3000 });
    expect(router.navigate).toHaveBeenCalledWith(['sessions']);
  });

  it('should update existing session', () => {
    jest.spyOn(router, 'url', 'get').mockReturnValue('/sessions/update/1');
    component.ngOnInit();
    component.sessionForm?.setValue(mockSession);
    component.submit();
    expect(sessionApiService.update).toHaveBeenCalledWith('1', mockSession);
    expect(matSnackBar.open).toHaveBeenCalledWith('Session updated !', 'Close', { duration: 3000 });
    expect(router.navigate).toHaveBeenCalledWith(['sessions']);
  });

  it('should validate required fields', () => {
    jest.spyOn(router, 'url', 'get').mockReturnValue('/sessions/create');
    component.ngOnInit();
    
    const form = component.sessionForm;
    form?.get('name')?.setValue('');
    form?.get('date')?.setValue('');
    form?.get('teacher_id')?.setValue('');
    form?.get('description')?.setValue('');
  
    form?.get('name')?.markAsTouched();
    form?.get('date')?.markAsTouched();
    form?.get('teacher_id')?.markAsTouched();
    form?.get('description')?.markAsTouched();
  
    expect(form?.get('name')?.errors?.['required']).toBeTruthy();
    expect(form?.get('date')?.errors?.['required']).toBeTruthy();
    expect(form?.get('teacher_id')?.errors?.['required']).toBeTruthy();
    expect(form?.get('description')?.errors?.['required']).toBeTruthy();
  });
  
  

  it('should validate description max length', () => {
    component.ngOnInit();
    const descriptionControl = component.sessionForm?.get('description');
    descriptionControl?.setValue('2001');
    expect(descriptionControl?.errors?.['max']).toBeTruthy();
  });
});
