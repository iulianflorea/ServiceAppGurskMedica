import {ProducerDto} from "./producerDto";

export class EquipmentDto {
  id?: number;
  model?: string;
  producerId?: number;
  producerName?: string;

  constructor(id: number, model: string, producerId: number, producerName: string) {
    this.id = id;
    this.model = model;
    this.producerId = producerId;
    this.producerName = producerName;
  }
}
