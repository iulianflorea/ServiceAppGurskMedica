import {Component, OnInit} from '@angular/core';
import {ProducerDto} from "../dtos/producerDto";
import {FormControl, FormGroup} from "@angular/forms";
import {HttpClient} from "@angular/common/http";
import {ActivatedRoute, Router} from "@angular/router";
import {ProductDto} from "../dtos/productDto";
import {environment} from "../../environments/environment";

@Component({
  selector: 'app-product-form',
  templateUrl: './product-form.component.html',
  styleUrls: ['./product-form.component.css']
})
export class ProductFormComponent implements OnInit {

  id: any;
  name: any;
  cod: any;
  quantity: any;
  producerList: ProducerDto[] = [];
  producerSelected: any;

  selectedFile: File | null = null;
  previewUrl: string | null = null;
  imageName: string | null = null;
  lightboxOpen: boolean = false;

  price: any;

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
    } else {
      return 'assets/no-image.png';
    }
  }

  productForm: FormGroup = new FormGroup({
    name: new FormControl,
    cod: new FormControl,
    quantity: new FormControl,
    price: new FormControl(0)
  })

  constructor(private httpClient: HttpClient,
              private router: Router,
              private route: ActivatedRoute) {
  }

  ngOnInit(): void {
    this.getProducerList();
    if (this.route.snapshot.params['id'] !== undefined) {
      // @ts-ignore
      this.httpClient.get(`${environment.apiUrl}/product/getById/` + this.route.snapshot.params['id']).subscribe((response: ProductDto) => {
        this.id = response.id;
        this.name = response.name;
        this.cod = response.cod;
        this.producerSelected = response.producer;
        this.quantity = response.quantity;
        this.price = response.price;
        this.imageName = response.imageName || null;
      })
    }
  }

  getProducerList() {
    this.httpClient.get(`${environment.apiUrl}/producer/getAll`).subscribe((response) => {
      this.producerList = response as ProducerDto[];
    })
  }

  saveProduct() {
    const formData = new FormData();

    if (this.id !== undefined && this.id !== null) {
      formData.append('id', this.id.toString());
    }
    formData.append("name", this.name);
    formData.append("cod", this.cod);
    formData.append("quantity", this.quantity.toString());
    formData.append('producerName', this.producerSelected.toString());
    formData.append("price", this.price.toString());

    if (this.selectedFile) {
      formData.append("image", this.selectedFile);
    }

    this.httpClient.post(`${environment.apiUrl}/product`, formData).subscribe((response) => {
      alert("Product was saved");
      this.router.navigate(["/product-list"]);
    });
  }
}
