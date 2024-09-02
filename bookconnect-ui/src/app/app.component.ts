import { Component, inject, OnInit } from '@angular/core';
import { ActivatedRoute, Router, RouterOutlet } from '@angular/router';
import { NavbarComponent } from './components/navbar/navbar.component';
import { HomepageComponent } from './components/homepage/homepage.component';
import { AuthContextService } from './shared/auth-context.service';
import { AuthService } from './services/auth.service';
import { CommonModule } from '@angular/common';
import { ReaderContextService } from './shared/reader-context.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, HomepageComponent, NavbarComponent, CommonModule],
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {  
  title = 'bookconnect-ui';
  isAuthenticated: boolean = false;
  isReaderMode: boolean = false;
  private authService = inject(AuthService);

  constructor(private readerContextService: ReaderContextService) {
    this.readerContextService.getAuthenticationStatus().subscribe(status => {
      this.isReaderMode = status;
    })
  }

  ngOnInit() {
   this.authService.autoLogin();
  }
}
