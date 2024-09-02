import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { AuthService } from '../services/auth.service';
import { inject } from '@angular/core';
import { catchError, throwError } from 'rxjs';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const baseUrl = "http://localhost:8080/api/v1";
  const authService = inject(AuthService);  
  if (req.url === getFullUrl(authService.refreshUrl) || req.url === getFullUrl(authService.authUrl)) {
    return next(req); 
  }

  const cloneRequest = req.clone({
    url: getFullUrl(req.url),
    setHeaders: {
      Authorization: `Bearer ${authService.accesToken}`
    }
  });
  
  return next(cloneRequest).pipe(
    catchError((error: HttpErrorResponse) => {
      if (error.status === 401) {
        authService.getAccessTokenFromAPI();
      }
      return throwError(() => error);
    })
  );

  function getFullUrl(subUrl: string): string {
    return baseUrl.concat(subUrl);
  }
};
