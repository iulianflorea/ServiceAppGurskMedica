export interface CbctReferenceDto {
  id?: number;
  deviceId?: number;
  mode: string;
  gender: string;
  kvp?: number | null;
  current?: number | null;
  scanTime?: number | null;
  dap?: number | null;
}

export interface CbctDeviceDto {
  id?: number;
  brand: string;
  model: string;
  references?: CbctReferenceDto[];
}

export interface CbctMeasurementValueDto {
  id?: number;
  measurementId?: number;
  mode: string;
  gender: string;
  kvp?: number | null;
  scanTime?: number | null;
  mgy?: number | null;
  mmAiHvl?: number | null;
  uGyPerS?: number | null;
  pulses?: number | null;
  mmAiTf?: number | null;
}

export interface CbctDozimetrieDto {
  id?: number;
  measurementId?: number;
  punctMasurat?: string;
  valoareaMaximaMarsurata?: number | null;
  materialPerete?: string;
}

export interface CbctMeasurementDto {
  id?: number;
  customerId?: number | null;
  customerName?: string;
  deviceId?: number | null;
  deviceBrand?: string;
  deviceModel?: string;
  serialNumber?: string;
  measurementDate?: string | null;
  values?: CbctMeasurementValueDto[];
  dozimetrie?: CbctDozimetrieDto[];
}
