import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Subject, tap, Observable, race } from 'rxjs';
import { AuthContextService } from '../shared/auth-context.service';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  authUrl: string = '/auth/authenticate';
  refreshUrl: string = '/auth/refresh-token';
  accesToken: string = "";

  private authContextService = inject(AuthContextService);

  constructor(private http: HttpClient) {
  }

  onLogin(loginData: any): Observable<any> {
    return this.http.post<any>(this.authUrl, loginData).pipe( 
      tap((response) => {
        this.accesToken = response.accessToken;
        this.saveToken(response.refreshToken, response.accessToken, new Date(new Date().setDate(new Date().getDate() + 7)));
        this.authContextService.setAuthenticationstatus(true);
      })
    );
  }

  onLogout() {
    this.deleteToken();
    this.authContextService.setAuthenticationstatus(false);
  }

  autoLogin() {
    let expirationTime = this.getExpiration();
    if(expirationTime && (new Date() > new Date(expirationTime)))  {
      return;
    }
    console.log("here 1")
    let accessToken = this.getAccessTokenFromLocalStorage()
    if(accessToken) {
      this.accesToken = accessToken;
      this.authContextService.setAuthenticationstatus(true)
      return;
    }
    console.log("here 2")
    let refreshToken = this.getRefreshTokenLocalStorage();
    if(refreshToken) {
      this.requestAccessToken(refreshToken);
    }
  }

  saveToken(refreshToken: string | null, accessToken: string | null, expirationDate: Date | null) {
    if(refreshToken) {
      localStorage.setItem("refreshToken", refreshToken);
    }
    if(accessToken) {
      localStorage.setItem('accessToken', accessToken);
    }
    if(expirationDate) {
      localStorage.setItem('expirationDate', JSON.stringify(expirationDate));
    }
  }

  deleteToken() {
    localStorage.clear();
  }

  getExpiration(): string | null {
   return localStorage.getItem('expirationDate');
  }

  getRefreshTokenLocalStorage(): string | null{
    return localStorage.getItem('refreshToken');
  }

  getAccessTokenFromLocalStorage(): string | null{
    return localStorage.getItem('accessToken');
  }

  getAccessTokenFromAPI() {
    let refreshToken = localStorage.getItem('refreshToken');
    if(refreshToken) {
      this.requestAccessToken(refreshToken);
    }
    this.authContextService.setAuthenticationstatus(true);
  }

  requestAccessToken(refreshToken: string) {
    const headers = { Authorization: `Bearer ${refreshToken}` }
    this.http
      .post(`http://localhost:8080/api/v1${this.refreshUrl}`, {}, {headers})
      .subscribe({
        next: (response: any) => {
          this.accesToken = response.accessToken;
          this.saveToken(response.accessToken, null, null);
        },
        complete: () => {
        }
    });
  }
}
