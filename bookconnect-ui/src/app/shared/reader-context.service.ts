import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ReaderContextService {
  private isReaderMode = new BehaviorSubject<boolean>(false);
  constructor() { }
  public getAuthenticationStatus(): Observable<boolean> {
    return this.isReaderMode.asObservable();
  }
  public setAuthenticationstatus(status: boolean) {
    this.isReaderMode.next(status);
  }
}
