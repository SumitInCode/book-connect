  import { Component, inject, OnInit } from '@angular/core';
  import { BookService } from '../../services/book.service';
  import { SearchFormComponent } from '../search-form/search-form.component';
  import { BooklistComponent } from '../booklist/booklist.component';
  import { CommonModule } from '@angular/common';
  @Component({
    selector: 'app-book-library',
    standalone: true,
    imports: [SearchFormComponent, BooklistComponent, CommonModule],
    templateUrl: './book-library.component.html',
    styleUrl: './book-library.component.css',
  })
  export class BookCollectionComponent implements OnInit {
    books: any = [];
    private bookService = inject(BookService);
    private page: number = 0;
    private size: number = 10;
    isLoading: boolean = false;
    hashMoreBooks: boolean = true;
    searchQuery: string = '';

    ngOnInit() {
      this.AdjustPageLimit();
      this.loadBooks();
    }

    loadBooks() {
      if (this.isLoading) {
        return;
      }
      this.isLoading = true;
      this.bookService.getAllBooks(this.page, this.size).subscribe({
        next: (response: any) => {
          if (response.content.length > 0) {
            this.books = [...this.books, ...response.content];
          }
          this.hashMoreBooks = !response.last;
        },
        complete: () => {
          this.page++;
          this.isLoading = false;
        },
        error: (error) => {
          this.isLoading = false;
        },
      });
    }

    private AdjustPageLimit(): void {
      if (window.innerWidth >= 1024) {
        this.size = 20;
      }
    }

    handleSearch(searchQuery: string) {
      this.searchQuery = searchQuery;
      this.loadSearchBooks()
    }

    loadSearchBooks() {
      if(this.books) {
        this.books = [];
      }
      if (this.isLoading) {
        return;
      }
      this.isLoading = true;
      this.bookService.getSearchedBooks(this.searchQuery, this.page, this.size).subscribe({
        next: (response: any) => {
          if (response.content.length > 0) {
            this.books = [...this.books, ...response.content];
            
          }
          this.hashMoreBooks = !response.last;
        },
        complete: () => {
          this.page++;
          this.isLoading = false;
        },
        error: () => {
          this.isLoading = false;
        },
      });
    }

    onClear() {
      this.searchQuery = '';
      this.books = []
      this.page = 0; 
      this.loadBooks();
    }
    
    handleLoadMore() {
      if(this.searchQuery) {
        this.loadSearchBooks();
      }
      else {
        this.loadBooks()
      }
    }
  }
