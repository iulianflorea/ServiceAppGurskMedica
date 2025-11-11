package com.example.ServiceApp.mapper;

import com.example.ServiceApp.dto.DocumentDataDto;
import com.example.ServiceApp.entity.DocumentData;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class DocumentDataMapper {

    public static DocumentDataDto toDto(DocumentData entity) {
        if (entity == null) {
            return null;
        }
        return DocumentDataDto.builder()
                .id(entity.getId())
                .customerId(entity.getCustomerId())
                .customerName(entity.getCustomer() != null ? entity.getCustomer().getName() : null)
                .cui(entity.getCui() != null ? entity.getCui() : null)
                .contractDate(entity.getContractDate())
                .monthOfWarranty(entity.getMonthOfWarranty())
                .numberOfContract(entity.getNumberOfContract())

                .equipmentId1(entity.getEquipmentId1())
                .equipmentId2(entity.getEquipmentId2())
                .equipmentId3(entity.getEquipmentId3())
                .equipmentId4(entity.getEquipmentId4())
                .equipmentId5(entity.getEquipmentId5())
                .equipmentId6(entity.getEquipmentId6())

                .equipmentName1(entity.getEquipment1() != null ? entity.getEquipment1().getModel() : null)
                .equipmentName2(entity.getEquipment2() != null ? entity.getEquipment2().getModel() : null)
                .equipmentName3(entity.getEquipment3() != null ? entity.getEquipment3().getModel() : null)
                .equipmentName4(entity.getEquipment4() != null ? entity.getEquipment4().getModel() : null)
                .equipmentName5(entity.getEquipment5() != null ? entity.getEquipment5().getModel() : null)
                .equipmentName6(entity.getEquipment6() != null ? entity.getEquipment6().getModel() : null)

                .productCode1(entity.getProductCode1())
                .productCode2(entity.getProductCode2())
                .productCode3(entity.getProductCode3())
                .productCode4(entity.getProductCode4())
                .productCode5(entity.getProductCode5())
                .productCode6(entity.getProductCode6())

                .serialNumber1(entity.getSerialNumber1())
                .serialNumber2(entity.getSerialNumber2())
                .serialNumber3(entity.getSerialNumber3())
                .serialNumber4(entity.getSerialNumber4())
                .serialNumber5(entity.getSerialNumber5())
                .serialNumber6(entity.getSerialNumber6())

                .signatureDate(entity.getSignatureDate())
                .trainedPerson(entity.getTrainedPerson())
                .jobFunction(entity.getJobFunction())
                .phone(entity.getPhone())
                .email(entity.getEmail())
                .contactPerson(entity.getContactPerson())
                .build();
    }

    public static DocumentData toEntity(DocumentDataDto dto) {
        if (dto == null) {
            return null;
        }
        return DocumentData.builder()
                .id(dto.getId())
                .customerId(dto.getCustomerId())
                .cui(dto.getCui())
                .contractDate(dto.getContractDate())
                .monthOfWarranty(dto.getMonthOfWarranty())
                .numberOfContract(dto.getNumberOfContract())

                .equipmentId1(dto.getEquipmentId1())
                .equipmentId2(dto.getEquipmentId2())
                .equipmentId3(dto.getEquipmentId3())
                .equipmentId4(dto.getEquipmentId4())
                .equipmentId5(dto.getEquipmentId5())
                .equipmentId6(dto.getEquipmentId6())

                .productCode1(dto.getProductCode1())
                .productCode2(dto.getProductCode2())
                .productCode3(dto.getProductCode3())
                .productCode4(dto.getProductCode4())
                .productCode5(dto.getProductCode5())
                .productCode6(dto.getProductCode6())

                .serialNumber1(dto.getSerialNumber1())
                .serialNumber2(dto.getSerialNumber2())
                .serialNumber3(dto.getSerialNumber3())
                .serialNumber4(dto.getSerialNumber4())
                .serialNumber5(dto.getSerialNumber5())
                .serialNumber6(dto.getSerialNumber6())

                .signatureDate(dto.getSignatureDate())
                .trainedPerson(dto.getTrainedPerson())
                .jobFunction(dto.getJobFunction())
                .phone(dto.getPhone())
                .email(dto.getEmail())
                .contactPerson(dto.getContactPerson())
                .build();
    }

    public static List<DocumentDataDto> toDtoList(List<DocumentData> entities) {
        if (entities == null) {
            return null;
        }
        return entities.stream()
                .map(DocumentDataMapper::toDto)
                .collect(Collectors.toList());
    }
}
