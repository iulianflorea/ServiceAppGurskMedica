import {AfterViewInit, Component, ViewChild} from '@angular/core';
import {MatTableDataSource} from "@angular/material/table";
import {MatPaginator} from "@angular/material/paginator";
import {EmployeeDto} from "../dtos/employeeDto";
import {HttpClient} from "@angular/common/http";

@Component({
  selector: 'app-employee-list',
  templateUrl: './employee-list.component.html',
  styleUrls: ['./employee-list.component.css']
})
export class EmployeeListComponent implements AfterViewInit{

  displayedColumns: string[] = ['id', 'name', 'cnp', 'update', 'delete'];
  dataSource: EmployeeDto[] = [];
  dataSource2 = new MatTableDataSource<EmployeeDto>(this.dataSource);

  @ViewChild(MatPaginator) paginator!: MatPaginator;

  ngAfterViewInit(): void {
    this.dataSource2.paginator = this.paginator;
  }

  constructor(private httpClient: HttpClient) {
  }

  ngOnInit() {
    this.httpClient.get("/api/employee/find-all").subscribe((response) => {
      console.log(response);
      this.dataSource = response as EmployeeDto[];
      this.dataSource2.data = this.dataSource;
    })
  }

  delete(employee: EmployeeDto) {
    const id = employee.id;
    if (confirm("Sure you want to delete it?")) {
      this.httpClient.delete("/api/employee/delete/" + id).subscribe((response) => {
        console.log(response);
        alert("Employee was deleted");
        this.ngOnInit();
      })
    }
  }
}
