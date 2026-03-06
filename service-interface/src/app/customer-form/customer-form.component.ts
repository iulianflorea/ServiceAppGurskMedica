import {Component, OnInit} from '@angular/core';
import {FormControl, FormGroup} from "@angular/forms";
import {HttpClient} from "@angular/common/http";
import {ActivatedRoute, Router} from "@angular/router";
import {CustomerDto} from "../dtos/customerDto";
import {UserDto} from "../dtos/userDto";
import {environment} from "../../environments/environment";

@Component({
  selector: 'app-customer-form',
  templateUrl: './customer-form.component.html',
  styleUrls: ['./customer-form.component.css']
})
export class CustomerFormComponent implements OnInit {
  id: any;
  name: any;
  cui: any;
  address: any;
  telephone: any;
  email: any;
  contactPerson: any;

  latitude: number | null = null;
  longitude: number | null = null;
  mapVisible = false;
  mapCenter: google.maps.LatLngLiteral = {lat: 45.9, lng: 24.9};
  mapZoom = 7;
  markerPosition: google.maps.LatLngLiteral | null = null;

  customerForm: FormGroup = new FormGroup({
    name: new FormControl,
    cui: new FormControl,
    address: new FormControl,
    telephone: new FormControl,
    email: new FormControl
  })

  constructor(private httpClient: HttpClient,
              private route: ActivatedRoute,
              private router: Router) {
  }

  ngOnInit(): void {
    console.log("id", this.route.snapshot.params['id']);
    if (this.route.snapshot.params['id'] !== undefined) {
      this.httpClient.get(`${environment.apiUrl}/customer/` + this.route.snapshot.params['id']).subscribe((response: CustomerDto) => {
        console.log(response);
        this.id = this.route.snapshot.params['id'];
        this.name = response.name;
        this.cui = response.cui;
        this.address = response.address;
        this.telephone = response.telephone;
        this.email = response.email;
        this.contactPerson = response.contactPerson;
        if (response.latitude != null && response.longitude != null) {
          this.latitude = response.latitude;
          this.longitude = response.longitude;
        }
      })
    }
  }

  toggleMap() {
    this.mapVisible = true;
    if (this.latitude != null && this.longitude != null) {
      this.mapCenter = {lat: this.latitude, lng: this.longitude};
      this.markerPosition = {lat: this.latitude, lng: this.longitude};
      this.mapZoom = 14;
    } else {
      this.mapCenter = {lat: 45.9, lng: 24.9};
      this.markerPosition = null;
      this.mapZoom = 7;
    }
  }

  onMapClick(event: google.maps.MapMouseEvent) {
    if (event.latLng) {
      this.latitude = event.latLng.lat();
      this.longitude = event.latLng.lng();
      this.markerPosition = {lat: this.latitude, lng: this.longitude};
    }
  }

  getCurrentLocation() {
    if (!navigator.geolocation) {
      alert('Geolocalizarea nu este suportată de browser.');
      return;
    }
    navigator.geolocation.getCurrentPosition(
      (position) => {
        this.latitude = position.coords.latitude;
        this.longitude = position.coords.longitude;
        this.mapCenter = {lat: this.latitude, lng: this.longitude};
        this.markerPosition = {lat: this.latitude, lng: this.longitude};
        this.mapZoom = 16;
        this.mapVisible = true;
      },
      () => alert('Nu s-a putut obține locația. Verifică permisiunile browserului.')
    );
  }

  saveCustomer() {
    var customer = {
      id: this.id,
      name: this.name,
      cui: this.cui,
      address: this.address,
      telephone: this.telephone,
      email: this.email,
      contactPerson: this.contactPerson,
      latitude: this.latitude,
      longitude: this.longitude
    }
    this.httpClient.post(`${environment.apiUrl}/customer`, customer).subscribe((response) => {
      console.log(response);
      alert("Customer was saved");
      this.router.navigate(["/customer-list"]);
    })
  }



}
