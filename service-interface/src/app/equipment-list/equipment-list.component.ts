import {AfterViewInit, Component, ViewChild} from '@angular/core';
import {EquipmentDto} from "../dtos/equipmentDto";
import {MatTab} from "@angular/material/tabs";
import {MatTableDataSource} from "@angular/material/table";
import {MatPaginator} from "@angular/material/paginator";
import {HttpClient} from "@angular/common/http";
import {ProductDto} from "../dtos/productDto";

@Component({
  selector: 'app-equipment-list',
  templateUrl: './equipment-list.component.html',
  styleUrls: ['./equipment-list.component.css']
})
export class EquipmentListComponent implements AfterViewInit {

  displayedColumns: string[] = ['id', 'model', 'producerId', 'update', 'delete'];
  dataSource: EquipmentDto[] = [];
  dataSource2 = new MatTableDataSource<EquipmentDto>(this.dataSource);

  @ViewChild(MatPaginator) paginator!: MatPaginator;

  ngAfterViewInit() {
    this.dataSource2.paginator = this.paginator;
  }

  constructor(private httpClient: HttpClient) {
  }

  ngOnInit() {
    this.httpClient.get("/api/equipment/find-all").subscribe((response) =>{
      console.log(response);
      this.dataSource = response as EquipmentDto[];
      this.dataSource2.data = this.dataSource;
    })
  }

  delete(equipment: EquipmentDto) {
    const id = equipment.id;
    if(confirm("Sure you want delete it?")) {
      this.httpClient.delete("/api/equipment/delete/" + id).subscribe((response) => {
        console.log(response);
        alert("The equipment was deleted");
        this.ngOnInit();
      })
    }
  }

}
