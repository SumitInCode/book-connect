import { Routes } from '@angular/router';
import { HomepageComponent } from './components/homepage/homepage.component';
import { LoginComponent } from './components/login/login.component';
import { RegisterComponent } from './components/register/register.component';
import { BookShelfComponent } from './components/book-shelf/book-shelf.component';
import { BookDetailsPageComponent } from './components/book-details-page/book-details-page.component';
import { MybookListComponent } from './components/mybook-list/mybook-list.component';
import { BookCollectionComponent } from './components/book-library/book-library.component';
import { AddBookComponent } from './components/add-book/add-book.component';

export const routes: Routes = [
    {path: '', component: HomepageComponent  },
    {path: 'home', component: HomepageComponent},
    {path: 'login', component: LoginComponent},
    {path: 'get-started', component: RegisterComponent},
    {path: 'library', component: BookCollectionComponent},
    {path: 'book-shelf', component: BookShelfComponent},
    {path: 'my-books', component: MybookListComponent},
    {path: 'add-book', component: AddBookComponent},
    {path: 'book-details/:id', component: BookDetailsPageComponent}
];
