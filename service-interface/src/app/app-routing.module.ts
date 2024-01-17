import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {UserRegisterComponent} from "./user-register/user-register.component";
import {UserLoginComponent} from "./user-login/user-login.component";
import {HomeComponent} from "./home/home.component";
import {InterventionSheetFormComponent} from "./intervention-sheet-form/intervention-sheet-form.component";

const routes: Routes = [
  {path: "register", component: UserRegisterComponent},
  {path: "login", component: UserLoginComponent},
  {path: "home", component: HomeComponent},
  {path: "intervention-sheet", component: InterventionSheetFormComponent}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {


}
