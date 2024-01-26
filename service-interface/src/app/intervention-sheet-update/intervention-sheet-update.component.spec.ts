import { ComponentFixture, TestBed } from '@angular/core/testing';

import { InterventionSheetUpdateComponent } from './intervention-sheet-update.component';

describe('InterventionSheetUpdateComponent', () => {
  let component: InterventionSheetUpdateComponent;
  let fixture: ComponentFixture<InterventionSheetUpdateComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [InterventionSheetUpdateComponent]
    });
    fixture = TestBed.createComponent(InterventionSheetUpdateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
