import {Component, Inject} from '@angular/core';
import {MAT_DIALOG_DATA} from "@angular/material/dialog";
import {DocumentService} from "../services/document.service";

@Component({
  selector: 'app-document-dialog',
  templateUrl: './document-dialog.component.html',
  styleUrls: ['./document-dialog.component.css']
})
export class DocumentDialogComponent {

  documents: any[] = [];
  selectedDocumentUrl: string | null = null;
  private apiUrl = 'http://localhost:8080/api/interventions';

  constructor(@Inject(MAT_DIALOG_DATA) public data: any, private documentService: DocumentService) {}

  ngOnInit() {
    this.loadDocuments();
  }

  loadDocuments() {
    this.documentService.getDocuments(this.data.intervention.id).subscribe({
      next: (docs) => {
        this.documents = docs.map((doc: any) => ({
          ...doc,
          url: `http://localhost:8080/api/interventions/${this.data.intervention.id}/documents/${doc.name}`
        }));
      },
      error: (err) => console.error('Error loading documents', err)
    });
  }

  onFileSelected(event: any) {
    const file: File = event.target.files[0];
    if (file) {
      this.documentService.uploadDocument(this.data.intervention.id, file).subscribe({
        next: () => {
          this.loadDocuments(); // reîncarcă documentele după upload
        },
        error: (err) => console.error('Error uploading file', err)
      });
    }
  }

  deleteDocument(filename: string) {
    this.documentService.deleteDocument(this.data.intervention.id, filename).subscribe({
      next: () => this.loadDocuments(),
      error: (err) => console.error('Delete failed', err)
    });
  }

  previewDocument(doc: { name: string, url: string }) {
    if (doc.name.toLowerCase().endsWith('.pdf')) {
      this.selectedDocumentUrl = doc.url;
    } else {
      this.selectedDocumentUrl = null;
      window.open(doc.url, '_blank');
    }
  }

}
