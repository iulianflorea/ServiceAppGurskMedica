import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ProductScanFormComponent } from './product-scan-form.component';

describe('ProductScanFormComponent', () => {
  let component: ProductScanFormComponent;
  let fixture: ComponentFixture<ProductScanFormComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ProductScanFormComponent]
    });
    fixture = TestBed.createComponent(ProductScanFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
