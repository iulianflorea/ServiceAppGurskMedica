import {AfterViewInit, Component, Injectable, Input, ViewChild} from '@angular/core';
import {MatPaginator, MatPaginatorModule} from "@angular/material/paginator";
import {MatTableDataSource, MatTableModule} from "@angular/material/table";
import {InterventionSheetDto} from "../dtos/interventionSheetDto";
import {HttpClient} from "@angular/common/http";
import {MatIconModule} from "@angular/material/icon";
import {MatButtonModule} from "@angular/material/button";
import {Router, RouterLink, RouterModule} from "@angular/router";
import {Observable} from "rxjs";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {CommonModule, NgForOf, NgIf} from "@angular/common";
import {MatInputModule} from "@angular/material/input";
import {MatSort} from "@angular/material/sort";
import {MatDatepickerModule} from "@angular/material/datepicker";
import {MatNativeDateModule} from "@angular/material/core";
import {environment} from "../../environments/environment.prod";


@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css'],
  imports: [
    MatTableModule,
    MatPaginatorModule,
    MatIconModule,
    MatButtonModule,
    RouterLink,
    FormsModule,
    NgForOf,
    NgIf,
    MatInputModule,
    MatDatepickerModule,
    MatNativeDateModule,
    ReactiveFormsModule,
    CommonModule,
    RouterModule,
  ],
  standalone: true
})
@Injectable({
  providedIn: 'root'
})
export class DashboardComponent implements AfterViewInit {

  displayedColumns: string[] = ['id', 'typeOfIntervention', 'equipmentName', 'serialNumber', 'dateOfIntervention', 'dataOfExpireWarranty', 'yearsOfWarranty', 'customerName', 'employeeName', 'noticed', 'fixed', 'engineerNote', 'delete'];
  dataSource: InterventionSheetDto[] = [];
  dataSource2 = new MatTableDataSource<InterventionSheetDto>(this.dataSource);
  keyword: string = '';
  selectedDate: Date | null = null;
  isMobile: boolean = false;

  @Input() item: any;
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  ngAfterViewInit() {
    this.dataSource2.paginator = this.paginator;
    this.dataSource2.sort = this.sort;
  }

  onDateChange(event: any) {
    if (event.value) {
      this.selectedDate = event.value;
    }
  }

  constructor(private httpClient: HttpClient, private router: Router) {}

  ngOnInit() {
    this.isMobile = window.innerWidth < 768;
    window.addEventListener('resize', () => {
      this.isMobile = window.innerWidth < 768;
    });
    this.httpClient.get(`${environment.apiUrl}/intervention-sheet/employeeIntervention/` + localStorage.getItem('token')).subscribe((response) => {
      this.dataSource = response as InterventionSheetDto[];
      this.dataSource2.data = this.dataSource;
    });
  }

  delete(interventionSheet: InterventionSheetDto) {
    const id = interventionSheet.id;
    if (confirm('Sure you want to delete it?')) {
      this.httpClient.delete(`${environment.apiUrl}/intervention-sheet/` + id).subscribe(() => {
        alert('The intervention was deleted');
        this.ngOnInit();
      });
    }
  }

  navigateToEdit(intervention: InterventionSheetDto) {
    this.router.navigate(['/intervention-sheet', intervention.id]);
  }

  searchInterventionSheet(keyword: string): Observable<InterventionSheetDto[]> {
    return this.httpClient.get<InterventionSheetDto[]>(`/api/intervention-sheet/search?keyword=${keyword}`);
  }

  search() {
    let finalKeyword = this.keyword;

    if (!finalKeyword && this.selectedDate) {
      const dateKeyword = new Date(this.selectedDate);
      dateKeyword.setMinutes(dateKeyword.getMinutes() - dateKeyword.getTimezoneOffset());
      finalKeyword = dateKeyword.toISOString().substring(0, 10);
    }

    if (finalKeyword) {
      this.searchInterventionSheet(finalKeyword).subscribe(data => {
        this.dataSource2.data = data;
      });
    }
  }
}
