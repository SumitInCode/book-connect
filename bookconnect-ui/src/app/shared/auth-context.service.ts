import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthContextService {
  private isAuthenticated = new BehaviorSubject<boolean>(false);
  constructor() { }

  public getAuthenticationStatus(): Observable<boolean> {
    return this.isAuthenticated.asObservable();
  }

  public setAuthenticationStatus(status: boolean) {
    this.isAuthenticated.next(status);
  }
}
