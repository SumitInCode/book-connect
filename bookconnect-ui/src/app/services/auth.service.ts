  // auth.service.ts
  import { HttpClient, HttpHeaders } from '@angular/common/http';
  import { Injectable, inject } from '@angular/core';
  import { Observable, tap, switchMap, catchError, of } from 'rxjs';
  import { AuthContextService } from '../shared/auth-context.service';

  @Injectable({
    providedIn: 'root',
  })
  export class AuthService {
    private SEVEN_DAYS_IN_MS: number = 604800000;
    authURL = '/auth/authenticate';
    refreshURL = '/auth/refresh-token';
    private accessToken: string = '';
    private authContextService = inject(AuthContextService);

    constructor(private http: HttpClient) {}

    onLogin(loginData: any): Observable<any> {
      return this.http.post<any>(this.authURL, loginData).pipe(
        tap((response) => {
          console.log(response.accessToken, response.refreshToken);
          this.accessToken = response.accessToken;
          this.saveToken(
            response.refreshToken,
            response.accessToken,
            new Date(Date.now() + this.SEVEN_DAYS_IN_MS)
          );
          this.authContextService.setAuthenticationStatus(true);
        })
      );
    }

    onLogout(): void {
      this.deleteToken();
      this.authContextService.setAuthenticationStatus(false);
    }

    autoLogin(): void {
      const expirationTime = this.getExpiration();
      if (expirationTime && new Date() > new Date(expirationTime)) {
        return;
      }
      const accessToken = this.getAccessTokenFromLocalStorage();
      if (accessToken) {
        this.accessToken = accessToken;
        this.authContextService.setAuthenticationStatus(true);
        return;
      }
      const refreshToken = this.getRefreshTokenFromLocalStorage();
      if (refreshToken) {
        this.requestAccessToken().subscribe();
      }
    }

    saveToken(
      refreshToken: string | null,
      accessToken: string | null,
      expirationDate: Date | null
    ): void {
      if (refreshToken) localStorage.setItem('refreshToken', refreshToken);
      if (accessToken) localStorage.setItem('accessToken', accessToken);
      if (expirationDate)
        localStorage.setItem('expirationDate', JSON.stringify(expirationDate));
    }

    deleteToken(): void {
      localStorage.clear();
    }

    getExpiration(): string | null {
      return localStorage.getItem('expirationDate');
    }

    getRefreshTokenFromLocalStorage(): string | null {
      return localStorage.getItem('refreshToken');
    }

    getAccessTokenFromLocalStorage(): string | null {
      return localStorage.getItem('accessToken');
    }

    requestAccessToken(): Observable<any> {
      const headers = new HttpHeaders({
        Authorization: `Bearer ${this.getRefreshTokenFromLocalStorage()}`,
      });
      return this.http.post<any>(this.refreshURL, {}, { headers }).pipe(
        tap((response) => {
          this.accessToken = response.accessToken;
          this.saveToken(null, response.accessToken, null);
        }),
      );
    }

    getAccessToken(): string {
      return this.accessToken;
    }
  }
