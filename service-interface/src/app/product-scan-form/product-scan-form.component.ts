import { Component } from '@angular/core';
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

  constructor(private fb: FormBuilder, private http: HttpClient) {
    this.scanForm = this.fb.group({
      productCode: ['', Validators.required],
      quantity: [1, [Validators.required, Validators.min(1)]]
    });
  }
  onSubmit(): void {
    if (this.scanForm.valid) {
      // const formData = this.scanForm.value;
      const formData = {
        cod: this.scanForm.value.productCode,
        quantity: this.scanForm.value.quantity
      };
      console.log(formData);
      this.http.post("/api/product/scan", formData).subscribe({
        next: (res: any) => {
          this.responseMessage = 'Produs actualizat cu succes!';
          this.scanForm.reset({ quantity: 1 });
        },
        error: err => {
          this.responseMessage = 'Eroare: produsul nu a fost găsit.';
        }
      });
    }
  }

}
