import {AfterViewInit, Component, ViewChild} from '@angular/core';
import {DatePipe} from "@angular/common";
import {MatPaginator, MatPaginatorModule} from "@angular/material/paginator";
import {MatSort, MatSortModule, SortDirection} from "@angular/material/sort";
import {MatTableModule} from "@angular/material/table";
import {MatProgressSpinnerModule} from "@angular/material/progress-spinner";
import {InterventionSheetDto} from "../dtos/interventionSheetDto";
import {HttpClient} from "@angular/common/http";
import {InterventionSheetFormComponent} from "../intervention-sheet-form/intervention-sheet-form.component";
import {CustomerDto} from "../dtos/customerDto";


@Component({
    selector: 'app-intervention-sheet-list',
    templateUrl: './intervention-sheet-list.component.html',
    styleUrls: ['./intervention-sheet-list.component.css'],
    standalone: true,
    imports: [MatProgressSpinnerModule, MatTableModule, MatSortModule, MatPaginatorModule, DatePipe]
})
export class InterventionSheetListComponent {
    displayedColumns: string[] = ['id', 'typeOfIntervention', 'equipmentId', 'serialNumber', 'dateOfIntervention', 'customerId', 'employeeId', 'noticed', 'fixed', 'engineerNote'];
    dataSource: InterventionSheetDto[] = [];
    customerSource: any;

    constructor(private httpClient: HttpClient) {
    }

    ngOnInit() {
        this.httpClient.get("/api/intervention-sheet/find-all").subscribe((response) => {
            console.log(response);
            this.dataSource = response as InterventionSheetDto[];
        })

    }

    getCustomerById(customer: number) {
        const id = customer;
        this.httpClient.get("/api/customer/" + id).subscribe((response) => {
            console.log(response);
            this.customerSource = response;
        })
    }


}






