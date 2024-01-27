import {Component, OnInit} from '@angular/core';
import {ProducerDto} from "../dtos/producerDto";
import {FormControl, FormGroup} from "@angular/forms";
import {HttpClient} from "@angular/common/http";
import {ActivatedRoute, Router} from "@angular/router";
import {ProductDto} from "../dtos/productDto";

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
  producerName: any;


  productForm: FormGroup = new FormGroup({
    name: new FormControl,
    cod: new FormControl,
    quantity: new FormControl,
  })

  constructor(private httpClient: HttpClient,
              private router: Router,
              private route: ActivatedRoute) {
  }

  ngOnInit(): void {
    this.getProducerList();
    console.log("id", this.route.snapshot.params['id']);
    if (this.route.snapshot.params['id'] !== undefined) {
      // @ts-ignore
      this.httpClient.get("/api/product/getById/" + this.route.snapshot.params['id']).subscribe((response: ProductDto) => {
        console.log(response);
        this.id = response.id;
        this.name = response.name;
        this.cod = response.cod;
        this.producerSelected = response.producer;
        this.quantity = response.quantity;
      })
    }
  }

  getProducerList() {
    this.httpClient.get("/api/producer/getAll").subscribe((response) => {
      console.log(response);
      this.producerList = response as ProducerDto[];
    })
  }

  saveProduct() {
    var product = {
      id: this.id,
      name: this.name,
      cod: this.cod,
      producer: this.producerSelected,
      quantity: this.quantity
    }
    this.httpClient.post("/api/product", product).subscribe((response) => {
      console.log(response);
      alert("Product was saved");
      this.router.navigate(["/product-list"]);
    })
  }


}
