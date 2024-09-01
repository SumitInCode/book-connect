import { Component, inject, OnInit, ChangeDetectorRef } from '@angular/core';
import { BooklistComponent } from '../booklist/booklist.component';
import { MybookService } from '../../services/mybook.service';
import { AuthContextService } from '../../shared/auth-context.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-mybook-list',
  standalone: true,
  imports: [BooklistComponent, CommonModule],
  templateUrl: './mybook-list.component.html',
  styleUrls: ['./mybook-list.component.css']
})
export class MybookListComponent implements OnInit {
  myBooks: any = [];
  private page: number = 0;
  private size: number = 10;
  isLoading: boolean = false;
  hasMoreBooks: boolean = true;
  isAuthenticated: boolean = false;

  private mybookService = inject(MybookService);
  private authContextService = inject(AuthContextService);
  constructor() {
    this.authContextService.getAuthenticationStatus().subscribe(status => {
      this.isAuthenticated = status;
    });
  }

  ngOnInit() {
    if(this.isAuthenticated) {
      this.loadMyBooks();
    }
  }

  loadMyBooks() {
    if (this.isLoading) {
      return;
    }
    this.isLoading = true;
    this.mybookService.getMyBooks(this.page, this.size).subscribe({
      next: (response: any) => {
        if (response.content.length > 0) {
          this.myBooks = [...this.myBooks, ...response.content];
          console.log(this.myBooks);
        }
        this.hasMoreBooks = !response.last;
      },
      complete: () => {
        this.page++;
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Failed to load books', error);
        this.isLoading = false;
      },
    });
  }
}
