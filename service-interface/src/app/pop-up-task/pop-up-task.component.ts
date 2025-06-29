import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {ActivatedRoute, Router, RouterLink} from "@angular/router";
import {HttpClient} from "@angular/common/http";
import {UserDto} from "../dtos/userDto";
import {MatButtonModule} from "@angular/material/button";
import {MatTableModule} from "@angular/material/table";
import {MatIconModule} from "@angular/material/icon";
import {DashboardComponent} from "../dashboard/dashboard.component";
import {InterventionSheetDto} from "../dtos/interventionSheetDto";



@Component({
  selector: 'app-pop-up-task',
  standalone: true,
  imports: [
    MatButtonModule,
    MatTableModule,
    RouterLink,
    MatIconModule
  ],
  templateUrl: './pop-up-task.component.html',
  styleUrls: ['./pop-up-task.component.css']
})
export class PopUpTaskComponent {
  id: any;
  typeOfIntervention: any;
  equipmentName: any;
  serialNumber: any;
  dateOfIntervention: any;
  dataOfExpireWarranty: any;
  yearsOfWarranty: any;
  customerName: any;
  employeeName: any;
  noticed: any;
  fixed: any;
  engineerNote: any;
  userList: UserDto[] = [];

  @Input() item: any;
  @Output() close = new EventEmitter<void>(); // Evenimentul de închidere
  constructor(private router: Router, private route: ActivatedRoute, private httpClient: HttpClient) {
  }

  ngOnInit() {

  }


  closeModal() {
    this.close.emit(); // Emite evenimentul când modalul trebuie închis
  }


  protected readonly interventionSheetDto = InterventionSheetDto;
  // protected readonly TaskListComponent = TaskListComponent;
  protected readonly DashboardComponent = DashboardComponent;

  delete(interventionSheetDto: InterventionSheetDto) {
    const id = interventionSheetDto.id;
    if (confirm("Sure you want to delete it?")) {
      this.httpClient.delete("/api/intervention-sheet/" + id).subscribe((response) => {
        console.log(response);
        alert(" The intervention was deleted");
        this.closeModal();
        this.router.navigateByUrl('/', { skipLocationChange: true }).then(() => {
          this.router.navigate(['/intervention-sheet-list']); // Navighează din nou la aceeași adresă
        });
      })
    }
  }
}
