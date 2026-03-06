export interface DocumentProductDto {
  id?: number;
  productId?: number;
  productName?: string;
  productCod?: string;
  quantity?: number;
  sortOrder?: number;
}

export interface DocumentEquipmentDto {
  id?: number;
  equipmentId?: number;
  equipmentName?: string;
  productCode?: string;
  serialNumber?: string;
  sortOrder?: number;
}

export interface DocumentTrainedPersonDto {
  id?: number;
  trainedPersonName?: string;
  jobFunction?: string;
  phone?: string;
  email?: string;
  signatureBase64?: string;
  sortOrder?: number;
}

export class DocumentDataDto {
  id?: number;
  customerName?: string;
  cui?: string;
  monthOfWarranty?: number;
  monthOfWarrantyHandPieces?: number;
  numberOfContract?: string;
  contactPerson?: string;
  customerId?: number;
  contractDate?: string;
  signatureDate?: string;
  signatureBase64?: string;
  equipments?: DocumentEquipmentDto[];
  products?: DocumentProductDto[];
  trainedPersons?: DocumentTrainedPersonDto[];
}
