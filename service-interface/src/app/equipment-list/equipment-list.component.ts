import {AfterViewInit, Component, ViewChild} from '@angular/core';
import {EquipmentDto} from "../dtos/equipmentDto";
import {MatTab} from "@angular/material/tabs";
import {MatTableDataSource} from "@angular/material/table";
import {MatPaginator} from "@angular/material/paginator";
import {HttpClient} from "@angular/common/http";
import {ProductDto} from "../dtos/productDto";
import {Observable} from "rxjs";
import {InterventionSheetDto} from "../dtos/interventionSheetDto";
import {environment} from "../../environments/environment";
import {Router} from "@angular/router";

@Component({
  selector: 'app-equipment-list',
  templateUrl: './equipment-list.component.html',
  styleUrls: ['./equipment-list.component.css']
})
export class EquipmentListComponent implements AfterViewInit {

  displayedColumns: string[] = ['id', 'image', 'model', 'productCode', 'producerId', 'delete'];
  dataSource: EquipmentDto[] = [];
  dataSource2 = new MatTableDataSource<EquipmentDto>(this.dataSource);
  keyword: string = '';

  @ViewChild(MatPaginator) paginator!: MatPaginator;

  ngAfterViewInit() {
    this.dataSource2.paginator = this.paginator;
  }

  constructor(private httpClient: HttpClient, private router: Router) {
  }

  navigateToForm(id: number): void {
    this.router.navigate(['/equipment-form', id]);
  }

  ngOnInit() {
    this.httpClient.get(`${environment.apiUrl}/equipment/find-all`).subscribe((response) =>{
      console.log(response);
      this.dataSource = response as EquipmentDto[];
      this.dataSource2.data = this.dataSource;
    })
  }

  delete(equipment: EquipmentDto) {
    const id = equipment.id;
    if(confirm("Sure you want delete it?")) {
      this.httpClient.delete(`${environment.apiUrl}/equipment/delete/` + id).subscribe((response) => {
        console.log(response);
        alert("The equipment was deleted");
        this.ngOnInit();
      })
    }
  }

  searchEquipments(keyword: string): Observable<EquipmentDto[]> {
    return this.httpClient.get<EquipmentDto[]>(`${environment.apiUrl}/equipment/search?keyword=${keyword}`);
  }

  getImage(imageName: string | null): string {
    if (imageName) {
      return `${environment.apiUrl}/uploads/` + imageName;
    }
    return 'assets/no-image.png';
  }

  search() {
    let finalKeyword = this.keyword;
    if (finalKeyword) {
      this.searchEquipments(finalKeyword).subscribe(data => {
        console.log(data);
        this.dataSource2.data = data;
      });
    }
  }

}
