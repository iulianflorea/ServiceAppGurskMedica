import {AfterViewInit, Component, ElementRef, EventEmitter, Injectable, Input, Output, ViewChild} from '@angular/core';
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
import {PopUpTaskComponent} from "../pop-up-task/pop-up-task.component";
import {MatCardModule} from "@angular/material/card";
import {MatDialog} from "@angular/material/dialog";
import {DocumentDialogComponent} from "../document-dialog/document-dialog.component";
import {BreakpointObserver} from "@angular/cdk/layout";
// @ts-ignore
import { Datepicker } from 'vanillajs-datepicker';



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
    MatCardModule,
    CommonModule,
    RouterModule,
  ],
  standalone: true
})
@Injectable({
  providedIn: 'root'
})
export class InterventionSheetListComponent implements AfterViewInit {

  displayedColumns: string[] = ['id', 'typeOfIntervention', 'equipmentName', 'serialNumber', 'dateOfIntervention', 'dataOfExpireWarranty', 'yearsOfWarranty', 'customerName', 'employeeName', 'noticed', 'fixed', 'engineerNote', 'view', 'documents'];
  dataSource: InterventionSheetDto[] = [];
  dataSource2 = new MatTableDataSource<InterventionSheetDto>(this.dataSource);
  keyword: string = '';
  // searchResult: InterventionSheetDto[] = [];
  selectedDate: Date = new Date();




  @Input() item: any;
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;
  @ViewChild('dateInput') dateInput!: ElementRef<HTMLInputElement>;
  datepicker!: Datepicker;

  ngAfterViewInit() {
    this.dataSource2.paginator = this.paginator;
    this.dataSource2.sort = this.sort;

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

  constructor(private httpClient: HttpClient,
              private dialog: MatDialog,
              private breakpointObserver: BreakpointObserver) {
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

  saveDate() {
    this.selectedDate.setMinutes(this.selectedDate.getMinutes() - this.selectedDate.getTimezoneOffset());
    const savedDate = this.selectedDate.toISOString().substring(0, 10);
    console.log('Data selectată:', savedDate);
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
    // this.searchResult = [];

    let finalKeyword = this.keyword;

    // Dacă nu s-a scris nimic manual, dar s-a ales o dată, folosim data
    if (!finalKeyword && this.selectedDate) {
      const dateKeyword = new Date(this.selectedDate);
      dateKeyword.setMinutes(dateKeyword.getMinutes() - dateKeyword.getTimezoneOffset());
      finalKeyword = dateKeyword.toISOString().substring(0, 10); // YYYY-MM-DD
    }

    if (finalKeyword) {
      this.searchInterventionSheet(finalKeyword).subscribe(data => {
        console.log(data);
        this.dataSource2.data = data;
        // this.searchResult = data;
      });
    }
  }

  openDocumentDialog(intervention: any): void {
    const isMobile = this.breakpointObserver.isMatched('(max-width: 600px)');

    this.dialog.open(DocumentDialogComponent, {
      width: isMobile ? '95vw' : '60vw',   // pe mobil aproape full screen, pe desktop 80%
      height: isMobile ? '90vh' : '80vh',
      maxWidth: '100vw',
      maxHeight: '100vh',
      data: { intervention },
      autoFocus: false,
      restoreFocus: false,
    });
  }





}






