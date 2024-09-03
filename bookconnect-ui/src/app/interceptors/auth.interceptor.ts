// auth.interceptor.ts
import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { AuthService } from '../services/auth.service';
import { inject } from '@angular/core';
import { catchError, switchMap, throwError } from 'rxjs';
import { environment } from '../../environment/environment';
export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const baseURL = environment.apiBaseURL;
  const authService = inject(AuthService);
  const isAuthOrRefreshRequest = [
    authService.refreshURL,
    authService.authURL,
  ].includes(req.url);

  const cloneRequest = req.clone({
    url: getFullURL(req.url),
    setHeaders: isAuthOrRefreshRequest
      ? {}
      : { Authorization: `Bearer ${authService.getAccessToken()}` },
  });

  if (req.url === authService.refreshURL || req.url === authService.authURL) {
    return next(cloneRequest);
  }

  return next(cloneRequest).pipe(
    catchError((error: HttpErrorResponse) => {
      if (error.status === 403) {
        return authService.requestAccessToken().pipe(
          switchMap(() => {
            return next(
              req.clone({
                url: getFullURL(req.url),
                setHeaders: {
                  Authorization: `Bearer ${authService.getAccessToken()}`,
                },
              })
            );
          }),
          catchError((innerError) => {
            return throwError(() => innerError);
          })
        );
      }
      return throwError(() => error);
    })
  );

  function getFullURL(SubURL: string): string {
    return baseURL + SubURL;
  }
};
