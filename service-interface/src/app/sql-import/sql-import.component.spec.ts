import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SqlImportComponent } from './sql-import.component';

describe('SqlImportComponent', () => {
  let component: SqlImportComponent;
  let fixture: ComponentFixture<SqlImportComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [SqlImportComponent]
    });
    fixture = TestBed.createComponent(SqlImportComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
