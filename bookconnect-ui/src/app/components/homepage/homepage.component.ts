import { Component } from '@angular/core';
import { BooklistComponent } from "../booklist/booklist.component";

@Component({
  selector: 'app-homepage',
  standalone: true,
  imports: [BooklistComponent],
  templateUrl: './homepage.component.html',
  styleUrl: './homepage.component.css'
})
export class HomepageComponent {

}
