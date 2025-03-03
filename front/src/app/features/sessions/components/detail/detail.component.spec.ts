import { ComponentFixture, TestBed } from '@angular/core/testing';
import { DetailComponent } from './detail.component';
import { RouterTestingModule } from '@angular/router/testing';
import { SessionService } from 'src/app/services/session.service';
import { TeacherService } from 'src/app/services/teacher.service';
import { SessionApiService } from '../../services/session-api.service';
import { Router, ActivatedRoute } from '@angular/router';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { of } from 'rxjs';
import { expect } from '@jest/globals';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { HttpClientModule } from '@angular/common/http';
import { ReactiveFormsModule } from '@angular/forms';

describe('DetailComponent', () => {
  let component: DetailComponent;
  let fixture: ComponentFixture<DetailComponent>;
  let sessionService: jest.Mocked<SessionService>;
  let teacherService: jest.Mocked<TeacherService>;
  let sessionApiService: jest.Mocked<SessionApiService>;
  let router: jest.Mocked<Router>;
  let matSnackBar: jest.Mocked<MatSnackBar>;

  const mockSession = {
    id: 1,
    name: 'Yoga Session',
    date: new Date().toISOString(),
    teacher_id: 1,
    description: 'Test description',
    users: [1, 2, 3],
    createdAt: new Date().toISOString(),
    updatedAt: new Date().toISOString()
  };

  const mockTeacher = {
    id: 1,
    lastName: 'Doe',
    firstName: 'John',
    createdAt: new Date().toISOString(),
    updatedAt: new Date().toISOString()
  };

  beforeEach(async () => {
    const sessionServiceMock = {
      sessionInformation: {
        admin: true,
        id: 1
      }
    };

    const teacherServiceMock = {
      detail: jest.fn().mockReturnValue(of(mockTeacher))
    };

    const sessionApiServiceMock = {
      detail: jest.fn().mockReturnValue(of(mockSession)),
      delete: jest.fn().mockReturnValue(of({})),
      participate: jest.fn().mockReturnValue(of({})),
      unParticipate: jest.fn().mockReturnValue(of({}))
    };

    const routerMock = {
      navigate: jest.fn()
    };

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
      declarations: [DetailComponent],
      imports: [RouterTestingModule,
        HttpClientModule,
        MatSnackBarModule,
        ReactiveFormsModule],
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

    fixture = TestBed.createComponent(DetailComponent);
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

  it('should fetch session and teacher details on init', () => {
    expect(sessionApiService.detail).toHaveBeenCalledWith('1');
    expect(teacherService.detail).toHaveBeenCalledWith('1');
    expect(component.session).toEqual(mockSession);
    expect(component.teacher).toEqual(mockTeacher);
  });

  it('should delete session', () => {
    component.delete();
    expect(sessionApiService.delete).toHaveBeenCalledWith('1');
    expect(matSnackBar.open).toHaveBeenCalledWith('Session deleted !', 'Close', { duration: 3000 });
    expect(router.navigate).toHaveBeenCalledWith(['sessions']);
  });

  it('should handle participation', () => {
    component.participate();
    expect(sessionApiService.participate).toHaveBeenCalledWith('1', '1');
    expect(sessionApiService.detail).toHaveBeenCalled();
  });

  it('should handle unparticipation', () => {
    component.unParticipate();
    expect(sessionApiService.unParticipate).toHaveBeenCalledWith('1', '1');
    expect(sessionApiService.detail).toHaveBeenCalled();
  });

  it('should go back when back() is called', () => {
    const spyHistoryBack = jest.spyOn(window.history, 'back');
    component.back();
    expect(spyHistoryBack).toHaveBeenCalled();
  });
});