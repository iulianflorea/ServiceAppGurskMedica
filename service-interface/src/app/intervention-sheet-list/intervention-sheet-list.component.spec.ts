import { ComponentFixture, TestBed } from '@angular/core/testing';

import { InterventionSheetListComponent } from './intervention-sheet-list.component';

describe('InterventionSheetListComponent', () => {
  let component: InterventionSheetListComponent;
  let fixture: ComponentFixture<InterventionSheetListComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [InterventionSheetListComponent]
    });
    fixture = TestBed.createComponent(InterventionSheetListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
