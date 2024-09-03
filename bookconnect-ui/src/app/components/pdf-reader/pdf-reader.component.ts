  import { Component, ChangeDetectionStrategy, Output, inject} from '@angular/core';
  import { NgxExtendedPdfViewerModule,NgxExtendedPdfViewerService} from 'ngx-extended-pdf-viewer';
  import { ActivatedRoute } from '@angular/router';
  import { HttpClient } from '@angular/common/http';
  import { AuthContextService } from '../../shared/auth-context.service';
  import { CommonModule } from '@angular/common';
  import { ReaderContextService } from '../../shared/reader-context.service';

  @Component({
    selector: 'app-pdf-reader',
    standalone: true,
    imports: [NgxExtendedPdfViewerModule, CommonModule      ],
    providers: [NgxExtendedPdfViewerService],
    changeDetection: ChangeDetectionStrategy.OnPush,
    templateUrl: './pdf-reader.component.html',
    styleUrl: './pdf-reader.component.css'
  })
  export class PdfReaderComponent {
    private requestBookFile = "/books/book-file/"
    tempBookFileURL: string = '';
    isAuthenticated: boolean = false;
    private route = inject(ActivatedRoute);
    private PDFService = inject(NgxExtendedPdfViewerModule)
    private http = inject(HttpClient);

    constructor(private authContextService: AuthContextService,
      private readerContextService: ReaderContextService
    ) {
      this.authContextService.getAuthenticationStatus().subscribe(status => {
        this.isAuthenticated = status;
      })
    }

    ngOnInit() {
      this.route.paramMap.subscribe(params => {
        if(this.isAuthenticated) {
          this.readerContextService.setAuthenticationstatus(true);
          this.getBookFile(+params.get('id')!);
        }
      })
    }

    getBookFile(bookId: number) {
      this.http.get(this.requestBookFile + bookId, { responseType: 'blob' }).subscribe(
        blob => {
          this.tempBookFileURL = URL.createObjectURL(blob);         
        },
        error => {
          console.error('Error fetching book file:', error);
        }
      );
    }
    

    ngOnDestroy(): void {
      if (this.tempBookFileURL) {
        URL.revokeObjectURL(this.tempBookFileURL);
      }
      this.readerContextService.setAuthenticationstatus(false);
    }           
  }
