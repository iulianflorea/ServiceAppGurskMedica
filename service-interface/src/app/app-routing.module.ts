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


const routes: Routes = [
  {path: "register", component: RegisterComponent},
  {path: "login", component: LoginComponent},
  {path: "intervention-sheet/:id", component: InterventionSheetFormComponent},
  {path: "intervention-sheet", component: InterventionSheetFormComponent},
  {path: "intervention-sheet-list", component: InterventionSheetListComponent},
  {path: "customer-form", component: CustomerFormComponent},
  {path: "customer-form/:id", component: CustomerFormComponent},
  {path: "customer-list", component: CustomerListComponent},
  {path: "product-form", component: ProductFormComponent},
  {path: "product-form/:id", component: ProductFormComponent},
  {path: "product-list", component: ProductListComponent},
  {path: "equipment-form", component: EquipmentFormComponent},
  {path: "equipment-form/:id", component: EquipmentFormComponent},
  {path: "equipment-list", component: EquipmentListComponent},
  {path: "employee-form", component: EmployeeFormComponent},
  {path: "employee-form/:id", component: EmployeeFormComponent},
  {path: "employee-list", component: EmployeeListComponent},
  { path: 'dashboard', component: DashboardComponent, canActivate: [AuthGuardComponent]}


];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {


}
