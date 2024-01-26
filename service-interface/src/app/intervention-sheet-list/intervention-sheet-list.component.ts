import {AfterViewInit, Component, ViewChild} from '@angular/core';
import {MatPaginator, MatPaginatorModule} from "@angular/material/paginator";
import {MatTableDataSource, MatTableModule} from "@angular/material/table";
import {InterventionSheetDto} from "../dtos/interventionSheetDto";
import {HttpClient} from "@angular/common/http";
import {MatIconModule} from "@angular/material/icon";
import {MatButtonModule} from "@angular/material/button";
import {RouterLink} from "@angular/router";


@Component({
  selector: 'app-intervention-sheet-list',
  templateUrl: './intervention-sheet-list.component.html',
  styleUrls: ['./intervention-sheet-list.component.css'],
  imports: [
    MatTableModule,
    MatPaginatorModule,
    MatIconModule,
    MatButtonModule,
    RouterLink
  ],
  standalone: true
})
export class InterventionSheetListComponent implements AfterViewInit {

  displayedColumns: string[] = ['id', 'typeOfIntervention', 'equipmentName', 'serialNumber', 'dateOfIntervention', 'customerName', 'employeeName', 'noticed', 'fixed', 'engineerNote', 'delete', 'update'];
  dataSource: InterventionSheetDto[] = [];
  dataSource2 = new MatTableDataSource<InterventionSheetDto>(this.dataSource);
  interventionSheet: InterventionSheetDto = new InterventionSheetDto();

  @ViewChild(MatPaginator) paginator!: MatPaginator;

  ngAfterViewInit() {
    this.dataSource2.paginator = this.paginator;
  }

  constructor(private httpClient: HttpClient) {
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
        alert("Intervention was deleted");
        this.ngOnInit();
      })
    }
  }

  getById(interventionSheet: InterventionSheetDto) {
    const id = interventionSheet.id;
    this.httpClient.get("/api/intervention-sheet/" + id).subscribe((response) => {
      console.log(response);
      this.interventionSheet = response as InterventionSheetDto;
    })
  }

  update(interventionSheet: InterventionSheetDto) {
    this.httpClient.put("/api/intervention-sheet/update", interventionSheet).subscribe((response) =>{
      console.log(response);
    })
  }


}






