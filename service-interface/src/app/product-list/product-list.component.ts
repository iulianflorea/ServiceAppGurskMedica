import {AfterViewInit, Component, computed, ViewChild} from '@angular/core';
import {ProductDto} from "../dtos/productDto";
import {MatTableDataSource} from "@angular/material/table";
import {MatPaginator} from "@angular/material/paginator";
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {environment} from "../../environments/environment.prod";
import {Router} from "@angular/router";


@Component({
  selector: 'app-product-list',
  templateUrl: './product-list.component.html',
  styleUrls: ['./product-list.component.css']
})
export class ProductListComponent implements AfterViewInit{

  displayedColumns: string[] = ['id', 'image', 'name', 'cod', 'producer', 'priceWithVAT', 'quantity', 'delete'];
  dataSource: ProductDto[] = [];
  dataSource2 = new MatTableDataSource<ProductDto>(this.dataSource);
  keyword: string = '';
  searchResult: ProductDto[] = [];

  @ViewChild(MatPaginator) paginator!: MatPaginator;

  ngAfterViewInit() {
    this.dataSource2.paginator = this.paginator;
  }

  constructor(private httpClient: HttpClient, private router: Router) {

  }

  ngOnInit() {
    this.httpClient.get(`${environment.apiUrl}/product/getAll`).subscribe((response) =>{
      console.log(response);
      this.dataSource = response as ProductDto[];
      this.dataSource2.data = this.dataSource;
    })
  }

  navigateToEdit(product: ProductDto) {
    this.router.navigate(['/product-form', product.id]);
  }

  delete(product: ProductDto) {
    const id = product.id;
    if(confirm("Sure you want delete it?")) {
      this.httpClient.delete(`${environment.apiUrl}/product/delete/` + id).subscribe((response) => {
        console.log(response);
        alert("The product was deleted");
        this.ngOnInit();
      })
    }
  }


  search() {
    let finalKeywork = this.keyword;
    if (finalKeywork) {
      this.searchProduct(finalKeywork).subscribe((data: any) => {
        console.log(data);
        this.dataSource2 = data;
      })
    }
  }


  searchProduct(keyword: string): Observable<ProductDto[]> {
    return this.httpClient.get<ProductDto[]>(`${environment.apiUrl}/product/search?keyword=${keyword}`);
  }

  getImage(imageName: any) {
    console.log("imageName", imageName)
    if(imageName != null) {
      console.log("link", `${environment.apiUrl}/uploads/` + imageName);
      return `${environment.apiUrl}/uploads/` + imageName;
    } else {
      return 'assets/no-image.png'
    }
  }

}
