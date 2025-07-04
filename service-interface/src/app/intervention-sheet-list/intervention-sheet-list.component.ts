import {AfterViewInit, Component, EventEmitter, Injectable, Input, Output, ViewChild} from '@angular/core';
import {MatPaginator, MatPaginatorModule} from "@angular/material/paginator";
import {MatTableDataSource, MatTableModule} from "@angular/material/table";
import {InterventionSheetDto} from "../dtos/interventionSheetDto";
import {HttpClient} from "@angular/common/http";
import {MatIconModule} from "@angular/material/icon";
import {MatButtonModule} from "@angular/material/button";
import {Router, RouterLink} from "@angular/router";
import {Observable} from "rxjs";
import {FormGroup, FormsModule, ReactiveFormsModule} from "@angular/forms";
import {NgForOf, NgIf} from "@angular/common";
import {MatInputModule} from "@angular/material/input";
import {MatSort} from "@angular/material/sort";
import {MatDatepickerModule} from "@angular/material/datepicker";
import {PopUpTaskComponent} from "../pop-up-task/pop-up-task.component";
import {SignaturePadComponent} from "../signature-pad/signature-pad.component";
import {MatCardModule} from "@angular/material/card";


@Component({
  selector: 'app-intervention-sheet-list',
  templateUrl: './intervention-sheet-list.component.html',
  styleUrls: ['./intervention-sheet-list.component.css'],
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
    PopUpTaskComponent,
    ReactiveFormsModule,
    MatCardModule
  ],
  standalone: true
})
@Injectable({
  providedIn: 'root'
})
export class InterventionSheetListComponent implements AfterViewInit {

  displayedColumns: string[] = ['typeOfIntervention', 'equipmentName', 'serialNumber', 'dateOfIntervention', 'dataOfExpireWarranty', 'yearsOfWarranty', 'customerName', 'employeeName', 'noticed', 'fixed', 'engineerNote', 'view'];
  dataSource: InterventionSheetDto[] = [];
  dataSource2 = new MatTableDataSource<InterventionSheetDto>(this.dataSource);
  keyword: string = '';
  searchResult: InterventionSheetDto[] = [];




  @Input() item: any;
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  ngAfterViewInit() {
    this.dataSource2.paginator = this.paginator;
    this.dataSource2.sort = this.sort;
  }

  constructor(private httpClient: HttpClient,
              private router: Router) {
  }

  selectedItem: any = null;
  isModalOpen = false;
  isMobile: boolean = false;

  openModal(item: any) {
    this.selectedItem = item;
    this.isModalOpen = true;
  }

  closeModal() {
    this.isModalOpen = false;
  }


  selectedDate: Date = new Date();

  saveDate() {
    this.selectedDate.setMinutes(this.selectedDate.getMinutes() - this.selectedDate.getTimezoneOffset());
    const savedDate = this.selectedDate.toISOString().substring(0, 10) // Formatare ca string YYYY-MM-DD
    console.log('Data salvată:', savedDate);

    if (this.keyword === "") {
      this.keyword = this.selectedDate.toISOString().slice(0, 10);
    }

  }

  ngOnInit() {
    this.isMobile = window.innerWidth < 768;
    window.addEventListener('resize', () => {
      this.isMobile = window.innerWidth < 768;
    });
    this.httpClient.get("/api/intervention-sheet/find-all").subscribe((response) => {
      console.log(response);
      this.dataSource = response as InterventionSheetDto[];
      this.dataSource2.data = this.dataSource;
    })

  }


  delete(interventionSheet: InterventionSheetDto) {
    const id = interventionSheet.id;
    if (confirm("Sure you want to delete it?")) {
      this.httpClient.delete("/api/intervention-sheet/" + id).subscribe((response) => {
        console.log(response);
        alert(" The intervention was deleted");
        this.ngOnInit();
      })
    }
  }

  getById(interventionSheet: InterventionSheetDto) {
    const id = interventionSheet.id;
    this.httpClient.get("/api/intervention-sheet/" + id).subscribe((response) => {
      console.log(response);

    })
  }

  update(interventionSheet: InterventionSheetDto) {

    this.httpClient.put("/api/intervention-sheet/update", interventionSheet).subscribe((response) => {
      console.log(response);
    })
  }


  // @ts-ignore
  searchInterventionSheet(keyword: string): Observable<InterventionSheetDto[]> {
    return this.httpClient.get<InterventionSheetDto[]>(`/api/intervention-sheet/search?keyword=${keyword}`);
  }

  search() {
    this.searchResult = [];
    if (this.keyword) {
      this.httpClient.get(`/api/intervention-sheet/search?keyword=${this.keyword}`).subscribe((data: any) => {
        console.log(data);
        this.dataSource2 = data;
      })
    }
    this.searchInterventionSheet(this.keyword).subscribe(data => this.searchResult = data);
  }





}






