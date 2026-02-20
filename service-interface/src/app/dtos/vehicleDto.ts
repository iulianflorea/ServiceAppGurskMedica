export class VehicleRevisionDto {
  id?: number;
  vehicleId?: number;
  date?: string;
  km?: number;
  cost?: number;
  description?: string;
}

export class VehicleItpDto {
  id?: number;
  vehicleId?: number;
  date?: string;
  validityMonths?: number;
  expiryDate?: string;
  cost?: number;
}

export class VehicleInsuranceDto {
  id?: number;
  vehicleId?: number;
  date?: string;
  validityMonths?: number;
  expiryDate?: string;
  insurer?: string;
  policyNumber?: string;
  cost?: number;
}

export class VehicleEventDto {
  id?: number;
  vehicleId?: number;
  date?: string;
  type?: string;
  description?: string;
  cost?: number;
}

export class VehicleDto {
  id?: number;
  licensePlate?: string;
  vin?: string;
  make?: string;
  model?: string;
  year?: number;
  color?: string;
  fuelType?: string;
  engineCapacity?: number;
  power?: number;
  currentKm?: number;
  notes?: string;
  photoName?: string;
  userId?: number;
  userName?: string;
  revisions?: VehicleRevisionDto[];
  itpList?: VehicleItpDto[];
  insuranceList?: VehicleInsuranceDto[];
  events?: VehicleEventDto[];
}
