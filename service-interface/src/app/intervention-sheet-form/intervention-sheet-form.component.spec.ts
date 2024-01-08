import { ComponentFixture, TestBed } from '@angular/core/testing';

import { InterventionSheetFormComponent } from './intervention-sheet-form.component';

describe('InterventionSheetFormComponent', () => {
  let component: InterventionSheetFormComponent;
  let fixture: ComponentFixture<InterventionSheetFormComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [InterventionSheetFormComponent]
    });
    fixture = TestBed.createComponent(InterventionSheetFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
