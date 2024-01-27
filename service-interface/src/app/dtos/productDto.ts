
export class ProductDto {
  id?: number;
  name?: string;
  cod?: string;
  quantity?: number;
  producer: number;
  producerName?: string;

  constructor(id: number, name: string, cod: string, quantity: number, producer: number, producerName: string) {
    this.id = id;
    this.name = name;
    this.cod = cod;
    this.quantity = quantity;
    this.producer = producer;
    this.producerName = producerName;
  }
}
