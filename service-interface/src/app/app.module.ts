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
import {InterventionSheetUpdateComponent} from './intervention-sheet-update/intervention-sheet-update.component';
import {MatFormFieldModule} from "@angular/material/form-field";


@NgModule({
    declarations: [
        AppComponent,
        UserRegisterComponent,
        HeaderComponent,
        UserLoginComponent,
        HomeComponent,
        InterventionSheetFormComponent,
        InterventionSheetUpdateComponent,
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
    exports: [
        InterventionSheetUpdateComponent
    ],
    bootstrap: [AppComponent]
})
export class AppModule {
}
