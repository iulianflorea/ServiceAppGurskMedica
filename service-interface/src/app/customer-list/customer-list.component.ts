import {AfterViewInit, Component, ViewChild} from '@angular/core';
import {CustomerDto} from "../dtos/customerDto";
import {MatTableDataSource} from "@angular/material/table";
import {MatPaginator} from "@angular/material/paginator";
import {HttpClient} from "@angular/common/http";
import {InterventionSheetDto} from "../dtos/interventionSheetDto";
import {Observable} from "rxjs";
import {environment} from "../../environments/environment.prod";
import {Router} from "@angular/router";

@Component({
  selector: 'app-customer-list',
  templateUrl: './customer-list.component.html',
  styleUrls: ['./customer-list.component.css']
})
export class CustomerListComponent implements AfterViewInit {

  displayedColumns: string[] = ['id', 'name', 'cui', 'address', 'telephone', 'email', 'contactPerson', 'delete'];
  dataSource: CustomerDto[] = [];
  dataSource2 = new MatTableDataSource<CustomerDto>(this.dataSource);
  keyword: string = '';
  searchResult: CustomerDto[] = [];

  @ViewChild(MatPaginator) paginator!: MatPaginator;

  ngAfterViewInit(): void {
    this.dataSource2.paginator = this.paginator;
  }

  constructor(private httpClient: HttpClient, private router: Router) {
  }

  navigateToForm(id: number): void {
    this.router.navigate(['/customer-form', id]);
  }

  ngOnInit() {
    this.httpClient.get(`${environment.apiUrl}/customer/customer-list`).subscribe((response) => {
      console.log(response);
      this.dataSource = response as CustomerDto[];
      this.dataSource2.data = this.dataSource;
    })
  }

  delete(customer: CustomerDto) {
    const id = customer.id;
    if (confirm("sure you want to delete it?")) {
      this.httpClient.delete(`${environment.apiUrl}/customer/` + id).subscribe((response) => {
        console.log(response);
        alert("Customer was deleted");
        this.ngOnInit();
      })
    }
  }


  searchCustomer(keyword: string): Observable<CustomerDto[]> {
    return this.httpClient.get<CustomerDto[]>(`${environment.apiUrl}/customer/search?keyword=${keyword}`);
  }

  search() {
    let finalKeyword = this.keyword;
    if (finalKeyword) {
      this.searchCustomer(finalKeyword).subscribe((data: any) => {
        console.log(data);
        this.dataSource2 = data;
      })
    }
  }



}
