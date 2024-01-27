import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {UserRegisterComponent} from "./user-register/user-register.component";
import {UserLoginComponent} from "./user-login/user-login.component";
import {HomeComponent} from "./home/home.component";
import {InterventionSheetFormComponent} from "./intervention-sheet-form/intervention-sheet-form.component";
import {InterventionSheetListComponent} from "./intervention-sheet-list/intervention-sheet-list.component";
import {CustomerFormComponent} from "./customer-form/customer-form.component";
import {CustomerListComponent} from "./customer-list/customer-list.component";
import {ProductFormComponent} from "./product-form/product-form.component";
import {ProductListComponent} from "./product-list/product-list.component";


const routes: Routes = [
  {path: "register", component: UserRegisterComponent},
  {path: "login", component: UserLoginComponent},
  {path: "home", component: HomeComponent},
  {path: "intervention-sheet/:id", component: InterventionSheetFormComponent},
  {path: "intervention-sheet", component: InterventionSheetFormComponent},
  {path: "intervention-sheet-list", component: InterventionSheetListComponent},
  {path: "customer-form", component: CustomerFormComponent},
  {path: "customer-form/:id", component: CustomerFormComponent},
  {path: "customer-list", component: CustomerListComponent},
  {path: "product-form", component: ProductFormComponent},
  {path: "product-form/:id", component: ProductFormComponent},
  {path: "product-list", component: ProductListComponent},

];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {


}
