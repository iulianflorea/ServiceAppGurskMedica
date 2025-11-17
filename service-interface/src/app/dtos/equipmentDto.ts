import {ProducerDto} from "./producerDto";

export class EquipmentDto {
  id?: number;
  model?: string;
  productCode?: string;
  producerId?: number;
  producerName?: string;

  constructor(id: number, model: string, productCode: string, producerId: number, producerName: string) {
    this.id = id;
    this.model = model;
    this.productCode = productCode;
    this.producerId = producerId;
    this.producerName = producerName;
  }
}
