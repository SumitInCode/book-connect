import { Component, inject, OnInit } from '@angular/core';
import { BooklistComponent } from "../booklist/booklist.component";
import { BookService } from '../../services/book.service';

@Component({
  selector: 'app-homepage',
  standalone: true,
  imports: [BooklistComponent],
  templateUrl: './homepage.component.html',
  styleUrl: './homepage.component.css'
})
export class HomepageComponent implements OnInit{
  popularBooks: any[] = [];
  private page: number = 0;
  private size: number = 10;
  isLoading: boolean = false;
  private bookService = inject(BookService);

  ngOnInit(): void {

    this.loadPopularBooks();
  }

  loadPopularBooks() {
    if (this.isLoading) {
      return;
    }
    
    this.isLoading = true;
    this.bookService.getPopularBooks(this.page, this.size).subscribe({
      next: (response: any) => {
        if (response.content.length > 0) {
          this.popularBooks = [...this.popularBooks, ...response.content];
          console.log(response);
        }
        // this.hashMoreBooks = !response.last;
      },
      complete: () => {
        this.page++;
        this.isLoading = false;
      },
      error: (error) => {
        console.error(error)
        this.isLoading = false;
      },
    });
  }
}
