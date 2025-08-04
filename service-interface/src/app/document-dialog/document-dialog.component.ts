
import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA } from "@angular/material/dialog";
import { DomSanitizer, SafeResourceUrl } from "@angular/platform-browser";
import { DocumentService } from "../services/document.service";
import { InterventionSheetListComponent } from "../intervention-sheet-list/intervention-sheet-list.component";

@Component({
  selector: 'app-document-dialog',
  templateUrl: './document-dialog.component.html',
  styleUrls: ['./document-dialog.component.css']
})
export class DocumentDialogComponent implements OnInit {

  documents: any[] = [];
  selectedFile: File | null = null;
  previewUrl: SafeResourceUrl | null = null;

  constructor(
    private documentService: DocumentService,
    private sanitizer: DomSanitizer,
    @Inject(MAT_DIALOG_DATA) public data: { intervention: any }
  ) {}

  ngOnInit() {
    this.loadDocuments();
  }

  loadDocuments(): void {
    this.documentService.getDocuments(this.data.intervention.id).subscribe({
      next: (docs) => {
        this.documents = docs.map(doc => ({
          ...doc,
          url: `${this.documentService.apiUrl}/${this.data.intervention.id}/documents/${doc.name}`
        }));
      },
      error: (err) => console.error('Error loading documents', err)
    });
  }

  onFileSelected(event: any): void {
    const file: File = event.target.files[0];
    if (file) {
      this.documentService.uploadDocument(this.data.intervention.id, file).subscribe({
        next: () => this.loadDocuments(),
        error: (err) => console.error('Error uploading file', err)
      });
    }
  }

  deleteDocument(filename: string): void {
    if (confirm("Sigur vrei să ștergi acest fișier?")) {
      this.documentService.deleteDocument(this.data.intervention.id, filename).subscribe({
        next: () => this.loadDocuments(),
        error: (err) => console.error('Delete failed', err)
      });
    }
  }

  // previewDocument(doc: any): void {
  //   this.documentService.downloadDocument(this.data.intervention.id, doc.name).subscribe({
  //     next: (blob) => {
  //       const fileURL = URL.createObjectURL(blob);
  //
  //       // Compatibil cu mobil: simulează click
  //       const link = document.createElement('a');
  //       link.href = fileURL;
  //       link.target = '_blank';
  //       link.rel = 'noopener';
  //       document.body.appendChild(link);
  //       link.click();
  //       document.body.removeChild(link);
  //
  //       // Cleanup
  //       setTimeout(() => URL.revokeObjectURL(fileURL), 1000);
  //     },
  //     error: (err) => {
  //       console.error('Preview failed:', err);
  //       alert('Nu s-a putut deschide PDF-ul');
  //     }
  //   });
  // }
  previewDocument(doc: any): void {
    const encodedName = encodeURIComponent(doc.name);
    const url = `${this.documentService.apiUrl}/${this.data.intervention.id}/documents/${encodedName}`;

    // Deschide într-o filă nouă – compatibil cu toate platformele
    window.open(url, '_blank', 'noopener');
  }

  downloadDocument(doc: any): void {
    this.documentService.downloadDocument(this.data.intervention.id, doc.name).subscribe({
      next: (blob) => {
        const url = URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = doc.name;
        a.click();
        URL.revokeObjectURL(url);
      },
      error: (err) => {
        console.error('Error downloading file:', err);
        alert('Eroare la descărcarea fișierului!');
      }
    });
  }

  protected readonly InterventionSheetListComponent = InterventionSheetListComponent;
}

