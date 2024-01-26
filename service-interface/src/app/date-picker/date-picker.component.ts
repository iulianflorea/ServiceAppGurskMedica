import { Component } from '@angular/core';
import {MatDatepickerModule} from "@angular/material/datepicker";
import {MatInputModule} from "@angular/material/input";
import {NativeDateAdapter} from '@angular/material/core';
@Component({
  selector: 'app-date-picker',
  templateUrl: './date-picker.component.html',
  styleUrls: ['./date-picker.component.css'],
  standalone: true,
  providers: [NativeDateAdapter],
  imports: [MatInputModule, MatDatepickerModule, MatDatepickerModule, MatInputModule],
})
export class DatePickerComponent {

}
