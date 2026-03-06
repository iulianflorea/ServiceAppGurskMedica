import {Component, Input, OnInit, ViewChild} from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { MatSnackBar } from '@angular/material/snack-bar';
import {MatTableDataSource} from "@angular/material/table";
import {DocumentDataDto} from "../dtos/documentDataDto";
import {Observable} from "rxjs";
import {MatPaginator} from "@angular/material/paginator";
import {MatSort} from "@angular/material/sort";
import {environment} from "../../environments/environment";
import {Router} from "@angular/router";

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
  selectedDate: Date | null = null;
  isMobile: boolean = false;

  constructor(
    private http: HttpClient,
    private snackBar: MatSnackBar,
    private router: Router
  ) {}

  navigateToForm(id: number): void {
    this.router.navigate(['/documents-form', id]);
  }

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
  ngAfterViewInit() {
    this.documents2.paginator = this.paginator;
    this.documents2.sort = this.sort;
  }

  onDateChange(event: any) {
    if (event.value) {
      this.selectedDate = event.value;
    }
  }

  loadDocuments(): void {
    this.loading = true;
    this.http.get<DocumentDataDto[]>(`${environment.apiUrl}/documents/get-all`).subscribe({
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
      this.http.delete(`${environment.apiUrl}/documents/` + id).subscribe((response) => {
        console.log(response);
        alert(" The intervention was deleted");
        this.ngOnInit();
      })
    }
  }

  getById(documentDataDto: DocumentDataDto) {
    const id = documentDataDto.id;
    this.http.get(`${environment.apiUrl}/documents/` + id).subscribe((response) => {
      console.log(response);
    })
  }

  update(documentDataDto: DocumentDataDto) {
    this.http.put(`${environment.apiUrl}/documents/update`, documentDataDto).subscribe((response) => {
      console.log(response);
    })
  }

  searchCocuments(keyword: string): Observable<DocumentDataDto[]> {
    return this.http.get<DocumentDataDto[]>(`${environment.apiUrl}/documents/search?keyword=${keyword}`);
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
