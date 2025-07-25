import {NgModule, isDevMode} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';
import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {HeaderComponent} from './header/header.component';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {MatIconModule} from "@angular/material/icon";
import {MatButtonModule} from "@angular/material/button";
import {MatInputModule} from "@angular/material/input";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {MatCardModule} from "@angular/material/card";
import {HTTP_INTERCEPTORS, HttpClientModule} from "@angular/common/http";
import {InterventionSheetFormComponent} from './intervention-sheet-form/intervention-sheet-form.component';
import {MatSelectModule} from "@angular/material/select";
import {MatDatepickerModule} from "@angular/material/datepicker";
import {DatePickerComponent} from './date-picker/date-picker.component';
import {MatNativeDateModule} from "@angular/material/core";
import {InterventionSheetListComponent} from './intervention-sheet-list/intervention-sheet-list.component';
import {MatPaginatorModule} from "@angular/material/paginator";
import {MatProgressSpinnerModule} from "@angular/material/progress-spinner";
import {MatTableModule} from "@angular/material/table";
import {MatSortModule} from "@angular/material/sort";
import {MatDividerModule} from "@angular/material/divider";
import {MatFormFieldModule} from "@angular/material/form-field";
import {CustomerFormComponent} from './customer-form/customer-form.component';
import {CustomerListComponent} from './customer-list/customer-list.component';
import {ProductFormComponent} from './product-form/product-form.component';
import {ProductListComponent} from './product-list/product-list.component';
import {EquipmentFormComponent} from './equipment-form/equipment-form.component';
import {EquipmentListComponent} from './equipment-list/equipment-list.component';
import {EmployeeFormComponent} from './employee-form/employee-form.component';
import {EmployeeListComponent} from './employee-list/employee-list.component';
import {LoginComponent} from "./login/login.component";
import {AuthGuardComponent} from "./auth-guard/auth-guard.component";
import {AuthInterceptor} from "./header/auth-interceptor";
import { SignaturePadComponent } from './signature-pad/signature-pad.component';
import {MatAutocompleteModule} from "@angular/material/autocomplete";
import {MatMenuModule} from "@angular/material/menu";
import {MatSlideToggleModule} from "@angular/material/slide-toggle";
import {PopUpTaskComponent} from "./pop-up-task/pop-up-task.component";
import { DocumentDialogComponent } from './document-dialog/document-dialog.component';
import {MatDialogModule} from "@angular/material/dialog";
import { SafePipe } from './safe.pipe';
import { ServiceWorkerModule } from '@angular/service-worker';
import {RouterModule} from "@angular/router";
import {CommonModule} from "@angular/common";






@NgModule({
  declarations: [
    AppComponent,
    HeaderComponent,
    InterventionSheetFormComponent,
    CustomerFormComponent,
    CustomerListComponent,
    ProductFormComponent,
    ProductListComponent,
    EquipmentFormComponent,
    EquipmentListComponent,
    EmployeeFormComponent,
    EmployeeListComponent,
    LoginComponent,
    AuthGuardComponent,
    SignaturePadComponent,
    DocumentDialogComponent,
    SafePipe,
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
    MatFormFieldModule,
    MatAutocompleteModule,
    MatMenuModule,
    MatSlideToggleModule,
    PopUpTaskComponent,
    MatDialogModule,
    ServiceWorkerModule.register('ngsw-worker.js', {
      enabled: !isDevMode(),
      // Register the ServiceWorker as soon as the application is stable
      // or after 30 seconds (whichever comes first).
      registrationStrategy: 'registerWhenStable:30000'
    }),
    CommonModule,
    RouterModule,
  ],
  providers: [
    {
      provide: HTTP_INTERCEPTORS,
      useClass: AuthInterceptor,
      multi: true
    }
  ],
  exports: [],
  bootstrap: [AppComponent]
})
export class AppModule {
}
