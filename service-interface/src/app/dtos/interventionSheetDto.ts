export class InterventionSheetDto {
  id?: number;
  typeOfIntervention?: string;
  equipmentId?: number;
  serialNumber?: string;
  dateOfIntervention?: string;
  dateOfExpireWarranty?: string
  yearsOfWarranty?: number;
  customerId?: number;
  employeeId?: number;
  noticed?: string;
  fixed?: string;
  engineerNote?: string;
  customerName?: string;
  employeeName?: string;
  equipmentName?: string;


  constructor(id: number, typeOfIntervention: string, equipmentId: number, serialNumber: string, dateOfIntervention: string, customerId: number, employeeId: number, noticed: string, fixed: string, engineerNote: string, customerName: string, employeeName: string, equipmentName: string) {
    this.id = id;
    this.typeOfIntervention = typeOfIntervention;
    this.equipmentId = equipmentId;
    this.serialNumber = serialNumber;
    this.dateOfIntervention = dateOfIntervention;
    this.customerId = customerId;
    this.employeeId = employeeId;
    this.noticed = noticed;
    this.fixed = fixed;
    this.engineerNote = engineerNote;
    this.customerName = customerName;
    this.employeeName = employeeName;
    this.equipmentName = equipmentName;
  }

}
