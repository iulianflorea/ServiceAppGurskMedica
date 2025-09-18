import {Component, inject, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, FormArray, Validators, FormControl} from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import {hide} from "@popperjs/core";

interface Client {
  id: number;
  name: string;
}

interface Product {
  id: number;
  name: string;
  quantity: number;
}

@Component({
  selector: 'app-order',
  templateUrl: './order.component.html',
  styleUrls: ['./order.component.css']
})
export class OrderComponent implements OnInit {
  orderForm!: FormGroup;
  clients: Client[] = [];
  products: Product[] = [];
  filteredClients: Client[] = [];
  filteredProducts: Product[][] = [];
  deliveryAddress!: string;
  sameAddress = false;


  constructor(private fb: FormBuilder, private http: HttpClient) {}

  ngOnInit(): void {
    this.orderForm = this.fb.group({
      clientId: [null, Validators.required],
      products: this.fb.array([]),
      deliveryAddress: this.deliveryAddress
    });

    // Adaugă 6 produse goale
    for (let i = 0; i < 6; i++) {
      this.productsFormArray.push(
        this.fb.group({
          productId: [null],
          quantity: [0]
        })
      );
      this.filteredProducts.push([]);
    }

    this.loadClients();
    this.loadProducts();
  }

  get productsFormArray(): FormArray {
    return this.orderForm.get('products') as FormArray;
  }

  // === CLIENT ===
  loadClients(): void {
    this.http.get<Client[]>('/api/customer/customer-list').subscribe(data => {
      this.clients = data;
      this.filteredClients = data;
    });
  }

  onClientInput(event: Event): void {
    const input = (event.target as HTMLInputElement).value.toLowerCase();
    this.filteredClients = this.clients.filter(c => c.name.toLowerCase().includes(input));
  }

  onClientSelected(clientId: number): void {
    this.orderForm.patchValue({ clientId });
  }

  getClientName(clientId: number): string {
    const client = this.clients.find(p => p.id === clientId);
    return client ? client.name : '';
  }

  // === PRODUSE ===
  loadProducts(): void {
    this.http.get<Product[]>('/api/product/getAll').subscribe(data => {
      this.products = data;

      this.productsFormArray.controls.forEach((group, index) => {
        this.filteredProducts[index] = this.products;
      });
    });
  }

  onProductInput(index: number, event: Event): void {
    const input = (event.target as HTMLInputElement).value.toLowerCase();
    this.filteredProducts[index] = this.products.filter(p => p.name.toLowerCase().includes(input));
  }

  onProductSelected(index: number, productId: number): void {
    this.productsFormArray.at(index).patchValue({ productId });
  }

  getProductName(productId: number): string {
    const product = this.products.find(p => p.id === productId);
    return product ? product.name : '';
  }

  submitOrder(): void {
    if (this.sameAddress) {
      this.orderForm.patchValue({
        deliveryAddress: null
      });
    }
    if (this.orderForm.valid) {
      console.log('Trimitem:', this.orderForm.value);
      this.http.post('/api/orders', this.orderForm.value).subscribe({
        next: () => alert('Comanda a fost trimisă cu succes!'),
        error: (err) => {
          console.error(err);
          // Dacă backendul returnează un ResponseStatusException, mesajul de eroare e de obicei în err.error.message
          if (err.status === 400 && err.error && err.error.message) {
            alert('Eroare: ' + err.error.message);
          } else {
            alert('A apărut o eroare necunoscută. Probabil stocul unui produs este insuficient');
          }
        }
      });
    }
  }

  protected readonly hide = hide;
}
