export interface DocumentEquipmentDto {
  id?: number;
  equipmentId?: number;
  equipmentName?: string;
  productCode?: string;
  serialNumber?: string;
  sortOrder?: number;
}

export class DocumentDataDto {
  id?: number;
  customerName?: string;
  cui?: string;
  monthOfWarranty?: number;
  monthOfWarrantyHandPieces?: number;
  numberOfContract?: string;
  trainedPerson?: string;
  jobFunction?: string;
  phone?: string;
  contactPerson?: string;
  customerId?: number;
  contractDate?: string;
  signatureDate?: string;
  email?: string;
  signatureBase64?: string;
  equipments?: DocumentEquipmentDto[];
}
