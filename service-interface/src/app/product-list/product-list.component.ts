import {AfterViewInit, Component, computed, ViewChild} from '@angular/core';
import {ProductDto} from "../dtos/productDto";
import {MatTableDataSource} from "@angular/material/table";
import {MatPaginator} from "@angular/material/paginator";
import {HttpClient} from "@angular/common/http";


@Component({
  selector: 'app-product-list',
  templateUrl: './product-list.component.html',
  styleUrls: ['./product-list.component.css']
})
export class ProductListComponent implements AfterViewInit{

  displayedColumns: string[] = ['id', 'name', 'cod', 'producer', 'quantity', 'update', 'delete'];
  dataSource: ProductDto[] = [];
  dataSource2 = new MatTableDataSource<ProductDto>(this.dataSource);

  @ViewChild(MatPaginator) paginator!: MatPaginator;

  ngAfterViewInit() {
    this.dataSource2.paginator = this.paginator;
  }

  constructor(private httpClient: HttpClient) {

  }

  ngOnInit() {
    this.httpClient.get("/api/product/getAll").subscribe((response) =>{
      console.log(response);
      this.dataSource = response as ProductDto[];
      this.dataSource2.data = this.dataSource;
    })
  }

  delete(product: ProductDto) {
    const id = product.id;
    if(confirm("Sure you want delete it?")) {
      this.httpClient.delete("/api/product/delete/" + id).subscribe((response) => {
        console.log(response);
        alert("The product was deleted");
        this.ngOnInit();
      })
    }
  }

}
