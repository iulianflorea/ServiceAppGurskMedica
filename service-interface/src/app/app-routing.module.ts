import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {InterventionSheetFormComponent} from "./intervention-sheet-form/intervention-sheet-form.component";
import {InterventionSheetListComponent} from "./intervention-sheet-list/intervention-sheet-list.component";
import {CustomerFormComponent} from "./customer-form/customer-form.component";
import {CustomerListComponent} from "./customer-list/customer-list.component";
import {ProductFormComponent} from "./product-form/product-form.component";
import {ProductListComponent} from "./product-list/product-list.component";
import {EquipmentFormComponent} from "./equipment-form/equipment-form.component";
import {EquipmentListComponent} from "./equipment-list/equipment-list.component";
import {EmployeeFormComponent} from "./employee-form/employee-form.component";
import {EmployeeListComponent} from "./employee-list/employee-list.component";
import {RegisterComponent} from "./register/register.component";
import {LoginComponent} from "./login/login.component";
import {AuthGuardComponent} from "./auth-guard/auth-guard.component";
import {DashboardComponent} from "./dashboard/dashboard.component";
import {ProductScanFormComponent} from "./product-scan-form/product-scan-form.component";
import {SqlImportComponent} from "./sql-import/sql-import.component";
import {OrderComponent} from "./order/order.component";
import {DocumentListComponent} from "./document-list/document-list.component";
import {DocumentDataFormComponent} from "./document-data-form/document-data-form.component";
import {AttendanceListComponent} from "./attendance-list/attendance-list.component";
import {WorkScheduleComponent} from "./work-schedule/work-schedule.component";
import {VehicleListComponent} from "./vehicle-list/vehicle-list.component";
import {VehicleFormComponent} from "./vehicle-form/vehicle-form.component";
import {TicketFormComponent} from "./ticket-form/ticket-form.component";
import {TicketListComponent} from "./ticket-list/ticket-list.component";
import {CbctListComponent} from "./cbct-list/cbct-list.component";
import {CbctDeviceListComponent} from "./cbct-device-list/cbct-device-list.component";
import {CbctMeasurementFormComponent} from "./cbct-measurement-form/cbct-measurement-form.component";
import {CbctDeviceFormComponent} from "./cbct-device-form/cbct-device-form.component";


const routes: Routes = [
  {path: "register", component: RegisterComponent},
  {path: "login", component: LoginComponent},
  {path: "intervention-sheet/:id", component: InterventionSheetFormComponent},
  {path: "intervention-sheet", component: InterventionSheetFormComponent},
  {path: "intervention-sheet-list", component: InterventionSheetListComponent, data: {reuse: true}},
  {path: "customer-form", component: CustomerFormComponent},
  {path: "customer-form/:id", component: CustomerFormComponent},
  {path: "customer-list", component: CustomerListComponent},
  {path: "product-form", component: ProductFormComponent},
  {path: "product-form/:id", component: ProductFormComponent},
  {path: "product-list", component: ProductListComponent, data: {reuse: true}},
  {path: "equipment-form", component: EquipmentFormComponent},
  {path: "equipment-form/:id", component: EquipmentFormComponent},
  {path: "equipment-list", component: EquipmentListComponent, data: {reuse: true}},
  {path: "employee-form", component: EmployeeFormComponent},
  {path: "employee-form/:id", component: EmployeeFormComponent},
  {path: "employee-list", component: EmployeeListComponent, data: {reuse: true}},
  {path: "scan", component: ProductScanFormComponent},
  {path: "import-database", component: SqlImportComponent},
  {path: "order", component: OrderComponent},
  {path: "documents", component: DocumentListComponent, canActivate: [AuthGuardComponent], data: {reuse: true}},
  {path: "documents-form", component: DocumentDataFormComponent, canActivate: [AuthGuardComponent]},
  {path: "documents-form/:id", component: DocumentDataFormComponent, canActivate: [AuthGuardComponent]},
  {path: 'dashboard', component: DashboardComponent, canActivate: [AuthGuardComponent]},
  {path: 'attendance-list', component: AttendanceListComponent, canActivate: [AuthGuardComponent], data: {reuse: true}},
  {path: 'work-schedule', component: WorkScheduleComponent, canActivate: [AuthGuardComponent]},
  {path: 'vehicle-list', component: VehicleListComponent, data: {reuse: true}},
  {path: 'vehicle-form', component: VehicleFormComponent},
  {path: 'vehicle-form/:id', component: VehicleFormComponent},
  {path: 'ticket-form', component: TicketFormComponent},
  {path: 'ticket-list', component: TicketListComponent, canActivate: [AuthGuardComponent], data: {reuse: true}},
  {path: 'cbct-list', component: CbctListComponent, canActivate: [AuthGuardComponent], data: {reuse: true}},
  {path: 'cbct-devices', component: CbctDeviceListComponent, canActivate: [AuthGuardComponent]},
  {path: 'cbct-form', component: CbctMeasurementFormComponent, canActivate: [AuthGuardComponent]},
  {path: 'cbct-form/:id', component: CbctMeasurementFormComponent, canActivate: [AuthGuardComponent]},
  {path: 'cbct-device-form', component: CbctDeviceFormComponent, canActivate: [AuthGuardComponent]},
  {path: 'cbct-device-form/:id', component: CbctDeviceFormComponent, canActivate: [AuthGuardComponent]},
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {


}
