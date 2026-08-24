import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, throwError } from 'rxjs';
import { AuthService } from '../services/auth.service';

export const errorInterceptor: HttpInterceptorFn = (request, next) => {

  const authService = inject(AuthService);

  return next(request).pipe(
    catchError((error: HttpErrorResponse) => {

      const isLoginRequest = request.url.includes('/auth/login');

      if (error.status === 401 && !isLoginRequest) {
        authService.clearSession();
      }

      return throwError(() => error);
    })
  );
};