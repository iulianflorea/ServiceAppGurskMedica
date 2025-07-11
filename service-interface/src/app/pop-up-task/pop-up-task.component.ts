import {Component, ElementRef, EventEmitter, Input, OnInit, Output, ViewChild} from '@angular/core';
import {ActivatedRoute, Router, RouterLink} from "@angular/router";
import {HttpClient} from "@angular/common/http";
import {UserDto} from "../dtos/userDto";
import {MatButtonModule} from "@angular/material/button";
import {MatTableModule} from "@angular/material/table";
import {MatIconModule} from "@angular/material/icon";
import {DashboardComponent} from "../dashboard/dashboard.component";
import {InterventionSheetDto} from "../dtos/interventionSheetDto";
import {InterventionSheetListComponent} from "../intervention-sheet-list/intervention-sheet-list.component";
import {CommonModule} from "@angular/common";
// @ts-ignore
import html2pdf from "html2pdf.js";




@Component({
  selector: 'app-pop-up-task',
  standalone: true,
  imports: [
    CommonModule,
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
  signatureBase64: any;
  userList: UserDto[] = [];

  @Input() item: any;
  @Output() close = new EventEmitter<void>(); // Evenimentul de închidere
  constructor(private router: Router, private route: ActivatedRoute, private httpClient: HttpClient) {
  }

  ngOnInit() {

  }


  @ViewChild('printSection') printSection!: ElementRef;

  print() {
    const printContents = this.printSection.nativeElement.innerHTML;
    const popupWindow = window.open('', '_blank', 'width=800,height=600');
    if (popupWindow) {
      popupWindow.document.open();
      popupWindow.document.write(`
      <html>
        <head>
          <title>Fișă intervenție</title>
          <style>

          @media print {
  .no-print {
    display: none !important;
  }
}

            body {
              font-family: Arial, sans-serif;
              padding: 20px;
              position: relative;
            }
            h2 {
              text-align: center;
            }
            img {
              max-width: 200px;
              height: auto;
            }
            .signature {
              margin-top: 100px;
              display: flex;
              justify-content: flex-end;
            }
            .signature p {
              margin-bottom: 5px;
              text-align: center;
            }
          </style>
        </head>
        <body onload="window.print(); window.close();">
          ${printContents}
        </body>
      </html>
    `);
      popupWindow.document.close();
    }
  }

  exportPdf() {
    const element = this.printSection.nativeElement;

    // Ascunde temporar butoanele
    const buttons = element.querySelectorAll('.no-print');
    buttons.forEach((btn: HTMLElement) => btn.style.display = 'none');

    const options = {
      margin: 0,
      filename: 'interventie.pdf',
      image: { type: 'jpeg', quality: 0.98 },
      html2canvas: {
        scale: 2,
        useCORS: true,
        scrollX: 0,
        scrollY: 0,
        windowWidth: document.documentElement.scrollWidth,  // 🟢 cheia: folosește întreaga lățime reală
      },
      jsPDF: {
        unit: 'mm',
        format: 'a4',
        orientation: 'portrait'
      }
    };

    html2pdf().from(element).set(options).save().then(() => {
      // Reafișează butoanele
      buttons.forEach((btn: HTMLElement) => btn.style.display = '');
    });
  }



  onBackdropClick(event: MouseEvent) {
    this.closeModal(); // deja o ai definită
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
