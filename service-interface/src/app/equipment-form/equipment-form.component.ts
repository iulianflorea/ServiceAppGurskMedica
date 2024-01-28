import {Component, OnInit} from '@angular/core';
import {ProducerDto} from "../dtos/producerDto";
import {FormControl, FormGroup} from "@angular/forms";
import {HttpClient} from "@angular/common/http";
import {ActivatedRoute, Router} from "@angular/router";
import {ProductDto} from "../dtos/productDto";
import {EquipmentDto} from "../dtos/equipmentDto";

@Component({
  selector: 'app-equipment-form',
  templateUrl: './equipment-form.component.html',
  styleUrls: ['./equipment-form.component.css']
})
export class EquipmentFormComponent implements OnInit{

  id: any;
  model: any;
  producerList: ProducerDto[] = [];
  producerSelected: any;


   equipmentForm: FormGroup = new FormGroup( {
     model: new FormControl,
   })

  constructor(private httpClient: HttpClient,
              private router: Router,
              private route: ActivatedRoute) {
  }

  ngOnInit() {
     this.getProducerList();
    console.log("id", this.route.snapshot.params['id']);
    if (this.route.snapshot.params['id'] !== undefined) {
      // @ts-ignore
      this.httpClient.get("/api/equipment/find-by-id/" + this.route.snapshot.params['id']).subscribe((response: EquipmentDto) => {
        console.log(response);
        this.id = response.id;
        this.model = response.model;
        this.producerSelected = response.producerId;
      })
    }
  }

  getProducerList() {
    this.httpClient.get("/api/producer/getAll").subscribe((response) => {
      console.log(response);
      this.producerList = response as ProducerDto[];
    })
  }

  saveEquipment() {
     var equipment = {
       id: this.id,
       model: this.model,
       producerId: this.producerSelected
     }
     this.httpClient.post("/api/equipment", equipment).subscribe((response) =>{
       console.log(response);
       alert("Equipment was saved");
       this.router.navigate(["/equipment-list"])
     })
  }

}
