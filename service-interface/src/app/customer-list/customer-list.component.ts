import {AfterViewInit, Component, ViewChild} from '@angular/core';
import {CustomerDto} from "../dtos/customerDto";
import {MatTableDataSource} from "@angular/material/table";
import {MatPaginator} from "@angular/material/paginator";
import {HttpClient} from "@angular/common/http";

@Component({
  selector: 'app-customer-list',
  templateUrl: './customer-list.component.html',
  styleUrls: ['./customer-list.component.css']
})
export class CustomerListComponent implements AfterViewInit {

  displayedColumns: string[] = ['id', 'name', 'cui', 'address', 'telephone', 'update', 'delete'];
  dataSource: CustomerDto[] = [];
  dataSource2 = new MatTableDataSource<CustomerDto>(this.dataSource);

  @ViewChild(MatPaginator) paginator!: MatPaginator;

  ngAfterViewInit(): void {
    this.dataSource2.paginator = this.paginator;
  }

  constructor(private httpClient: HttpClient) {
  }

  ngOnInit() {
    this.httpClient.get("/api/customer/customer-list").subscribe((response) => {
      console.log(response);
      this.dataSource = response as CustomerDto[];
      this.dataSource2.data = this.dataSource;
    })
  }

  delete(customer: CustomerDto) {
    const id = customer.id;
    if (confirm("sure you want to delete it?")) {
      this.httpClient.delete("/api/customer/" + id).subscribe((response) => {
        console.log(response);
        alert("Customer was deleted");
        this.ngOnInit();
      })
    }
  }
}
