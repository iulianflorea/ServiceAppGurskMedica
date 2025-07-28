import { Component, ElementRef, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-product-scan-form',
  templateUrl: './product-scan-form.component.html',
  styleUrls: ['./product-scan-form.component.css']
})
export class ProductScanFormComponent {
  scanForm: FormGroup;
  responseMessage: string | null = null;

  @ViewChild('quantityInput') quantityInput!: ElementRef;

  constructor(private fb: FormBuilder, private http: HttpClient) {
    this.scanForm = this.fb.group({
      quantity: [null, [Validators.required]], // fără valoare implicită
      productCode: ['', Validators.required]
    });
  }

  onSubmit(): void {
    if (this.scanForm.valid) {
      const formData = {
        cod: this.scanForm.value.productCode,
        quantity: this.scanForm.value.quantity
      };

      console.log('Trimis:', formData);

      this.http.post("/api/product/scan", formData).subscribe({
        next: (res: any) => {
          this.responseMessage = 'Produs actualizat cu succes!';
          this.scanForm.reset(); // resetează complet
          setTimeout(() => this.quantityInput?.nativeElement.focus(), 0);
        },
        error: err => {
          this.responseMessage = 'Eroare: produsul nu a fost găsit.';
          setTimeout(() => this.quantityInput?.nativeElement.focus(), 0);
        }
      });
    }
  }
}
