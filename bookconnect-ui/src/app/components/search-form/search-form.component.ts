import { CommonModule } from '@angular/common';
import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import {FormsModule, FormBuilder, FormGroup } from '@angular/forms';
import { ReactiveFormsModule   } from '@angular/forms';

@Component({
  selector: 'app-search-form',
  standalone: true,
  imports: [FormsModule, ReactiveFormsModule, CommonModule],
  templateUrl: './search-form.component.html',
  styleUrls: ['./search-form.component.css']
})
export class SearchFormComponent implements OnInit{
  searchForm: FormGroup;

  @Output() search = new EventEmitter<string>();
  @Output() clear = new EventEmitter<void>();

  constructor(private fb: FormBuilder) {
    this.searchForm = this.fb.group({
      searchQuery: ['']
    });
  }

  ngOnInit(): void {
    this.searchForm.get('searchQuery')?.valueChanges.subscribe(value => {
      if (!value) {
        this.clear.emit();
      }
    });
  }

  onSubmit() {
    const searchText = this.searchForm.get('searchQuery')?.value;
    if (searchText) {
      this.search.emit(searchText);
    }
  }
}
