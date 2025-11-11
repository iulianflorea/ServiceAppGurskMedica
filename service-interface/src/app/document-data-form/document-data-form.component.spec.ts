import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DocumentDataFormComponent } from './document-data-form.component';

describe('DocumentDataFormComponent', () => {
  let component: DocumentDataFormComponent;
  let fixture: ComponentFixture<DocumentDataFormComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [DocumentDataFormComponent]
    });
    fixture = TestBed.createComponent(DocumentDataFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
