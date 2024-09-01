  import { Component, inject, Input} from '@angular/core';
  import { CommonModule } from '@angular/common';
  import { SearchFormComponent } from "../search-form/search-form.component";
  import { Router } from '@angular/router';

  @Component({
    selector: 'app-booklist',
    standalone: true,
    imports: [CommonModule, SearchFormComponent],
    templateUrl: './booklist.component.html',
    styleUrls: ['./booklist.component.css']
  })
  export class BooklistComponent{
    coverPhotoNotFound: string = "/coverNotAvailable.jpg";
    private router = inject(Router);
    @Input() books: any = [];
    
    truncateTitle(title: string): string {
      return title.length > 22 ? `${title.slice(0, 19)}...` : title;
    }

    navigateToBookDetails(id: number) {
      this.router.navigate(['/book-details', id]);
    }
  }
