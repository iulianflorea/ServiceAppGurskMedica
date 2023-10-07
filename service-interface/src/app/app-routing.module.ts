import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {UserRegisterComponent} from "./user-register/user-register.component";
import {UserLoginComponent} from "./user-login/user-login.component";
import {HomeComponent} from "./home/home.component";

const routes: Routes = [
  {path: "register", component: UserRegisterComponent},
  {path: "login", component: UserLoginComponent},
  {path: "home", component: HomeComponent}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {


}
