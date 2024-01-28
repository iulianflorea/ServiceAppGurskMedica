import {NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';
import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {UserRegisterComponent} from './user-register/user-register.component';
import {HeaderComponent} from './header/header.component';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {MatIcon, MatIconModule} from "@angular/material/icon";
import {MatButtonModule} from "@angular/material/button";
import {MatInputModule} from "@angular/material/input";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {MatCardModule} from "@angular/material/card";
import {HttpClientModule} from "@angular/common/http";
import {UserLoginComponent} from './user-login/user-login.component';
import {HomeComponent} from './home/home.component';
import {InterventionSheetFormComponent} from './intervention-sheet-form/intervention-sheet-form.component';
import {MatSelectModule} from "@angular/material/select";
import {MatDatepickerModule} from "@angular/material/datepicker";
import {DatePickerComponent} from './date-picker/date-picker.component';
import {MatNativeDateModule} from "@angular/material/core";
import {InterventionSheetListComponent} from './intervention-sheet-list/intervention-sheet-list.component';
import {MatPaginator, MatPaginatorModule} from "@angular/material/paginator";
import {MatProgressSpinnerModule} from "@angular/material/progress-spinner";
import {MatTableModule} from "@angular/material/table";
import {MatSortModule} from "@angular/material/sort";
import {MatDividerModule} from "@angular/material/divider";
import {MatFormFieldModule} from "@angular/material/form-field";
import { CustomerFormComponent } from './customer-form/customer-form.component';
import { CustomerListComponent } from './customer-list/customer-list.component';
import { ProductFormComponent } from './product-form/product-form.component';
import { ProductListComponent } from './product-list/product-list.component';
import { EquipmentFormComponent } from './equipment-form/equipment-form.component';
import { EquipmentListComponent } from './equipment-list/equipment-list.component';
import { EmployeeFormComponent } from './employee-form/employee-form.component';
import { EmployeeListComponent } from './employee-list/employee-list.component';


@NgModule({
    declarations: [
        AppComponent,
        UserRegisterComponent,
        HeaderComponent,
        UserLoginComponent,
        HomeComponent,
        InterventionSheetFormComponent,
        CustomerFormComponent,
        CustomerListComponent,
        ProductFormComponent,
        ProductListComponent,
        EquipmentFormComponent,
        EquipmentListComponent,
        EmployeeFormComponent,
        EmployeeListComponent,
    ],
    imports: [
        BrowserModule,
        AppRoutingModule,
        BrowserAnimationsModule,
        MatIconModule,
        MatButtonModule,
        MatInputModule,
        ReactiveFormsModule,
        MatCardModule,
        HttpClientModule,
        FormsModule,
        MatSelectModule,
        MatDatepickerModule,
        MatNativeDateModule,
        DatePickerComponent,
        InterventionSheetListComponent,
        MatProgressSpinnerModule,
        MatTableModule,
        MatSortModule,
        MatPaginatorModule,
        MatDividerModule,
        MatFormFieldModule
    ],
    providers: [],
    exports: [],
    bootstrap: [AppComponent]
})
export class AppModule {
}
