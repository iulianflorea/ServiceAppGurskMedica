import {Component, OnInit} from '@angular/core';
import {ProducerDto} from "../dtos/producerDto";
import {FormControl, FormGroup} from "@angular/forms";
import {HttpClient} from "@angular/common/http";
import {ActivatedRoute, Router} from "@angular/router";
import {EquipmentDto} from "../dtos/equipmentDto";
import {environment} from "../../environments/environment";

@Component({
  selector: 'app-equipment-form',
  templateUrl: './equipment-form.component.html',
  styleUrls: ['./equipment-form.component.css']
})
export class EquipmentFormComponent implements OnInit {

  id: any;
  model: any;
  productCode: any;
  producerList: ProducerDto[] = [];
  producerSelected: any;

  selectedFile: File | null = null;
  previewUrl: string | null = null;
  imageName: string | null = null;
  lightboxOpen = false;

  equipmentForm: FormGroup = new FormGroup({
    model: new FormControl,
  })

  constructor(private httpClient: HttpClient,
              private router: Router,
              private route: ActivatedRoute) {
  }

  ngOnInit() {
    this.getProducerList();
    if (this.route.snapshot.params['id'] !== undefined) {
      this.httpClient.get(`${environment.apiUrl}/equipment/find-by-id/` + this.route.snapshot.params['id']).subscribe((response: EquipmentDto) => {
        this.id = response.id;
        this.model = response.model;
        this.productCode = response.productCode;
        this.producerSelected = response.producerId;
        this.imageName = response.imageName || null;
      })
    }
  }

  getProducerList() {
    this.httpClient.get(`${environment.apiUrl}/producer/getAll`).subscribe((response) => {
      this.producerList = response as ProducerDto[];
    })
  }

  onFileSelected(event: Event) {
    const fileInput = event.target as HTMLInputElement;
    if (fileInput.files && fileInput.files[0]) {
      this.selectedFile = fileInput.files[0];
      const reader = new FileReader();
      reader.onload = e => this.previewUrl = reader.result as string;
      reader.readAsDataURL(this.selectedFile);
    }
  }

  openLightbox() {
    this.lightboxOpen = true;
  }

  closeLightbox() {
    this.lightboxOpen = false;
  }

  getImage(imageName: string | null): string {
    if (imageName != null) {
      return `${environment.apiUrl}/uploads/` + imageName;
    }
    return 'assets/no-image.png';
  }

  saveEquipment() {
    const formData = new FormData();
    if (this.id !== undefined && this.id !== null) {
      formData.append('id', this.id.toString());
    }
    formData.append('model', this.model);
    formData.append('productCode', this.productCode);
    formData.append('producerId', this.producerSelected.toString());
    if (this.selectedFile) {
      formData.append('image', this.selectedFile);
    }

    this.httpClient.post(`${environment.apiUrl}/equipment`, formData).subscribe(() => {
      alert("Equipment was saved");
      this.router.navigate(["/equipment-list"]);
    });
  }

}
