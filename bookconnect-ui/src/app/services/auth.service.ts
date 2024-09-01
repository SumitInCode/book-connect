import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { CookieService } from 'ngx-cookie-service';
import { Subject, tap, Observable } from 'rxjs';
import { AuthContextService } from '../shared/auth-context.service';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
            authUrl: string = '/auth/authenticate';
  refreshUrl: string = '/auth/refresh-token';
  accesToken: string = "";

  private authContextService = inject(AuthContextService);

  private cookieService = inject(CookieService);
  public newAcessToken$ = new Subject<boolean>();

  constructor(private http: HttpClient) {
    this.newAcessToken$.subscribe((response: any) => {
      this.getAccessTokenUsingRefreshToken();
    });
  }

  onLogin(loginData: any): Observable<any> {
    return this.http.post<any>(this.authUrl, loginData).pipe( 
      tap((response) => {
        if (response && response.refreshToken) {
          if (this.checkRefreshTokenInCookie()) {
            this.deleteToken();
          }
          this.accesToken = response.accessToken;
          this.saveToken(response.refreshToken);
        }
      })
    );
  }
  
  onStartUpInit() {
    if(this.checkRefreshTokenInCookie() && !this.accesToken) {
      this.getAccessTokenUsingRefreshToken();
    }
  }

  onLogout() {
    if(this.cookieService.check('refreshToken')) {
      this.deleteToken(); 
    }
    this.authContextService.setAuthenticationstatus(false);
  }

  saveToken(token: string) {
    this.cookieService.set('refreshToken', token);
  }

  getToken(): string {
    return this.cookieService.get('refreshToken');
  }

  deleteToken() {
    this.cookieService.delete('refreshToken');
  }

  checkRefreshTokenInCookie(): boolean {
    return this.cookieService.check('refreshToken');
  }

  getAccessToken(): string {
    return this.accesToken;
  }

  getRefreshToken(): string {
    return this.getToken();
  }

  getAccessTokenUsingRefreshToken() {
    const headers = { Authorization: `Bearer ${this.getRefreshToken()}` }
    this.http
      .post(`http://localhost:8080/api/v1${this.refreshUrl}`, {}, {headers})
      .subscribe({
        next: (response: any) => {
          this.accesToken = response.accessToken;
        },
        complete: () => {
          if(this.accesToken) {
            this.authContextService.setAuthenticationstatus(true);
          }
        },
        error: (error) => {
          console.log(error.error)
          if(error.status == 400) {
            this.onLogout();
            if(this.checkRefreshTokenInCookie()) {
              this.deleteToken();
            }
            alert("Please authenticate yourself!")
          }
        }
    });
  }
}
