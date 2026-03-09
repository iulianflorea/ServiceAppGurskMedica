import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';

@Component({
  selector: 'app-ticket-form',
  templateUrl: './ticket-form.component.html',
  styleUrls: ['./ticket-form.component.css']
})
export class TicketFormComponent implements OnInit {

  submitted = false;
  loading = false;

  ticketForm = new FormGroup({
    clinicName:     new FormControl('', Validators.required),
    equipmentBrand: new FormControl('', Validators.required),
    equipmentModel: new FormControl('', Validators.required),
    serialNumber:   new FormControl('', Validators.required),
    phone:          new FormControl('', Validators.required),
    email:          new FormControl('', [Validators.required, Validators.email]),
    city:           new FormControl('', Validators.required),
    address:        new FormControl('', Validators.required),
    problem:        new FormControl('', Validators.required),
  });

  constructor(private http: HttpClient) {}

  ngOnInit(): void {}

  submit() {
    if (this.ticketForm.invalid) {
      this.ticketForm.markAllAsTouched();
      return;
    }
    this.loading = true;
    this.http.post(`${environment.apiUrl}/api/tickets`, this.ticketForm.value).subscribe({
      next: () => {
        this.submitted = true;
        this.loading = false;
      },
      error: () => {
        alert('A apărut o eroare. Vă rugăm încercați din nou.');
        this.loading = false;
      }
    });
  }

  newTicket() {
    this.ticketForm.reset();
    this.submitted = false;
  }
}
