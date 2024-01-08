import {ProducerDto} from "./producerDto";

export class EquipmentDto {
  id?: number;
  model?: string;
  producerId?: ProducerDto;
}
