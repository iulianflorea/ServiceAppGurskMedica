import {AfterViewInit, Component, Injectable, ViewChild} from '@angular/core';
import {MatPaginator, MatPaginatorModule} from "@angular/material/paginator";
import {MatTableDataSource, MatTableModule} from "@angular/material/table";
import {InterventionSheetDto} from "../dtos/interventionSheetDto";
import {HttpClient} from "@angular/common/http";
import {MatIconModule} from "@angular/material/icon";
import {MatButtonModule} from "@angular/material/button";
import {Router, RouterLink} from "@angular/router";
import {Observable} from "rxjs";
import {FormGroup, FormsModule} from "@angular/forms";
import {NgForOf, NgIf} from "@angular/common";
import {MatInputModule} from "@angular/material/input";


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
    MatInputModule
  ],
  standalone: true
})
@Injectable({
  providedIn: 'root'
})
export class InterventionSheetListComponent implements AfterViewInit {

  displayedColumns: string[] = ['id', 'typeOfIntervention', 'equipmentName', 'serialNumber', 'dateOfIntervention', 'dataOfExpireWarranty', 'yearsOfWarranty', 'customerName', 'employeeName', 'noticed', 'fixed', 'engineerNote', 'update', 'delete'];
  dataSource: InterventionSheetDto[] = [];
  dataSource2 = new MatTableDataSource<InterventionSheetDto>(this.dataSource);
  keyword: string = '';
  searchResult: InterventionSheetDto[] = [];


  @ViewChild(MatPaginator) paginator!: MatPaginator;

  ngAfterViewInit() {
    this.dataSource2.paginator = this.paginator;
  }

  constructor(private httpClient: HttpClient,
              private router: Router) {
  }

  ngOnInit() {
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

    this.httpClient.put("/api/intervention-sheet/update", interventionSheet).subscribe((response) =>{
      console.log(response);
    })
  }

  // @ts-ignore
  searchInterventionSheet(keyword:string): Observable<InterventionSheetDto[]> {
    return this.httpClient.get<InterventionSheetDto[]>(`/api/intervention-sheet/search?keyword=${keyword}`);
  }

  search() {
    this.searchInterventionSheet(this.keyword).subscribe(data =>this.searchResult = data);
  }






}






