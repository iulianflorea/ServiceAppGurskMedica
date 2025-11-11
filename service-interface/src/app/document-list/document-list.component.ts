import {Component, ElementRef, Input, OnInit, ViewChild} from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { MatSnackBar } from '@angular/material/snack-bar';
import {MatTableDataSource} from "@angular/material/table";
import {DocumentDataDto} from "../dtos/documentDataDto";
import {Observable} from "rxjs";
import {MatPaginator} from "@angular/material/paginator";
import {MatSort} from "@angular/material/sort";
// @ts-ignore
import { Datepicker } from 'vanillajs-datepicker';

export interface DocumentData {
  id: number;
  customerName: string;
  cui: string;
  date: string;
  monthOfWarranty: number;
  numberOfContract: string;
  nameOfEquipment: string;
  productCode: string;
  serialNumber: string;
  dateOfContract: string;
  trainedPerson: string;
  function: string;
  phone: string;
  contactPerson: string;
}

@Component({
  selector: 'app-document-list',
  templateUrl: './document-list.component.html',
  styleUrls: ['./document-list.component.css']
})
export class DocumentListComponent implements OnInit {

  documents: DocumentDataDto[] = [];
  displayedColumns: string[] = ['id', 'customerName', 'contractDate', 'numberOfContract', 'actions'];
  loading = false;
  documents2 = new MatTableDataSource<DocumentDataDto>(this.documents);
  keyword: string = '';
  selectedDate: Date = new Date();
  isMobile: boolean = false;

  constructor(
    private http: HttpClient,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.loadDocuments();
    this.isMobile = window.innerWidth < 768;
    window.addEventListener('resize', () => {
      this.isMobile = window.innerWidth < 768;
    });
  }

  @Input() item: any;
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;
  @ViewChild('dateInput') dateInput!: ElementRef<HTMLInputElement>;
  datepicker!: Datepicker;

  ngAfterViewInit() {
    this.documents2.paginator = this.paginator;
    this.documents2.sort = this.sort;

    this.datepicker = new Datepicker(this.dateInput.nativeElement, {
      format: 'dd/mm/yyyy',
      autohide: true
    });

    // Ascultă evenimentul de schimbare dată
    this.dateInput.nativeElement.addEventListener('changeDate', (event: any) => {
      const selected = this.datepicker.getDate();
      if (selected) {
        this.selectedDate = selected;
      }
    });
  }

  loadDocuments(): void {
    this.loading = true;
    this.http.get<DocumentData[]>('/api/documents/get-all').subscribe({
      next: (data) => {
        console.log(data);
        this.documents = data;
        this.documents2.data = this.documents
        this.loading = false;
      },
      error: (err) => {
        console.error('Eroare la încărcare:', err);
        this.loading = false;
        this.snackBar.open('Eroare la încărcarea documentelor.', 'Închide', { duration: 3000 });
      }
    });
  }

  delete(documentDataDto: DocumentDataDto) {
    const id = documentDataDto.id;
    if (confirm("Sure you want to delete it?")) {
      this.http.delete("/api/documents/" + id).subscribe((response) => {
        console.log(response);
        alert(" The intervention was deleted");
        this.ngOnInit();
      })
    }
  }

  getById(documentDataDto: DocumentDataDto) {
    const id = documentDataDto.id;
    this.http.get("/api/documents/" + id).subscribe((response) => {
      console.log(response);
    })
  }

  update(documentDataDto: DocumentDataDto) {
    this.http.put("/api/documents/update", documentDataDto).subscribe((response) => {
      console.log(response);
    })
  }

  downloadDocx(id: number, type: string): void {
    this.loading = true;
    this.http.get(`/api/documents/export/${id}/${type}`, { responseType: 'blob' })
      .subscribe({
        next: (blob) => {
          const fileName = `${type}_${id}.docx`;
          const url = window.URL.createObjectURL(blob);
          const a = document.createElement('a');
          a.href = url;
          a.download = fileName;
          document.body.appendChild(a);
          a.click();
          a.remove();
          window.URL.revokeObjectURL(url);
          this.loading = false;
          this.snackBar.open(`Documentul ${type} a fost generat cu succes.`, 'OK', { duration: 3000 });
        },
        error: (err) => {
          console.error('Eroare la generarea documentului DOCX:', err);
          this.loading = false;
          this.snackBar.open('Eroare la generarea fișierului DOCX.', 'Închide', { duration: 3000 });
        }
      });
  }

  searchCocuments(keyword: string): Observable<DocumentDataDto[]> {
    return this.http.get<DocumentDataDto[]>(`/api/documents/search?keyword=${keyword}`);
  }

  search() {
    let finalKeyword = this.keyword;
    // Dacă nu s-a scris nimic manual, dar s-a ales o dată, folosim data
    if (!finalKeyword && this.selectedDate) {
      const dateKeyword = new Date(this.selectedDate);
      dateKeyword.setMinutes(dateKeyword.getMinutes() - dateKeyword.getTimezoneOffset());
      finalKeyword = dateKeyword.toISOString().substring(0, 10); // YYYY-MM-DD
    }
    if (finalKeyword) {
      this.searchCocuments(finalKeyword).subscribe(data => {
        console.log(data);
        this.documents2.data = data;
      });
    }
  }
}
