import {Component, ElementRef, ViewChild} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {HttpClient} from '@angular/common/http';
import {Location} from '@angular/common';
import {environment} from "../../environments/environment";

@Component({
  selector: 'app-product-scan-form',
  templateUrl: './product-scan-form.component.html',
  styleUrls: ['./product-scan-form.component.css']
})
export class ProductScanFormComponent {
  scanForm: FormGroup;
  responseMessage: string | null = null;
  responseSuccess = false;

  scanMode = false;
  scannerEnabled = false;

  // Toate formatele comune de coduri de bare
  formatsToScan: any[] = [
    'QR_CODE', 'CODE_128', 'CODE_39', 'CODE_93',
    'EAN_13', 'EAN_8', 'UPC_A', 'UPC_E',
    'DATA_MATRIX', 'PDF_417', 'CODABAR', 'ITF'
  ];

  @ViewChild('quantityInput') quantityInput!: ElementRef;
  @ViewChild('codeInput') codeInput!: ElementRef;

  constructor(private fb: FormBuilder, private http: HttpClient, private location: Location) {
    this.scanForm = this.fb.group({
      quantity: [null, [Validators.required]],
      productCode: ['', Validators.required]
    });
  }

  goBack(): void {
    this.location.back();
  }

  enableScanMode() {
    this.scanMode = true;
    this.scannerEnabled = true;
    this.scanForm.get('productCode')?.setValue('');
    this.responseMessage = null;
  }

  disableScanMode() {
    this.scanMode = false;
    this.scannerEnabled = false;
    this.responseMessage = null;
    setTimeout(() => this.codeInput?.nativeElement.focus(), 0);
  }

  onCodeScanned(result: string): void {
    if (!result) return;
    this.scannerEnabled = false;
    this.scanForm.get('productCode')?.setValue(result);
    this.onSubmit();
  }

  onSubmit(): void {
    if (this.scanForm.valid) {
      const formData = {
        cod: this.scanForm.value.productCode,
        quantity: this.scanForm.value.quantity
      };

      this.http.post(`${environment.apiUrl}/product/scan`, formData).subscribe({
        next: () => {
          this.responseMessage = `✔ Produs "${formData.cod}" actualizat cu succes!`;
          this.responseSuccess = true;
          this.scanForm.get('productCode')?.setValue('');
          if (this.scanMode) {
            // Reactivează scanerul pentru următorul cod
            setTimeout(() => this.scannerEnabled = true, 1000);
          } else {
            setTimeout(() => this.codeInput?.nativeElement.focus(), 0);
          }
        },
        error: () => {
          this.responseMessage = `✘ Produsul "${formData.cod}" nu a fost găsit.`;
          this.responseSuccess = false;
          this.scanForm.get('productCode')?.setValue('');
          if (this.scanMode) {
            setTimeout(() => this.scannerEnabled = true, 1000);
          } else {
            setTimeout(() => this.codeInput?.nativeElement.focus(), 0);
          }
        }
      });
    }
  }
}
