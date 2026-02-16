package com.example.ServiceApp.mapper;

import com.example.ServiceApp.dto.DocumentDataDto;
import com.example.ServiceApp.dto.DocumentEquipmentDto;
import com.example.ServiceApp.dto.DocumentTrainedPersonDto;
import com.example.ServiceApp.entity.DocumentData;
import com.example.ServiceApp.entity.DocumentEquipment;
import com.example.ServiceApp.entity.DocumentTrainedPerson;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class DocumentDataMapper {

    public static DocumentDataDto toDto(DocumentData entity) {
        if (entity == null) {
            return null;
        }

        List<DocumentEquipmentDto> equipmentDtos = new ArrayList<>();
        if (entity.getEquipments() != null) {
            equipmentDtos = entity.getEquipments().stream()
                    .map(DocumentDataMapper::toEquipmentDto)
                    .collect(Collectors.toList());
        }

        List<DocumentTrainedPersonDto> trainedPersonDtos = new ArrayList<>();
        if (entity.getTrainedPersons() != null) {
            trainedPersonDtos = entity.getTrainedPersons().stream()
                    .map(DocumentDataMapper::toTrainedPersonDto)
                    .collect(Collectors.toList());
        }

        return DocumentDataDto.builder()
                .id(entity.getId())
                .customerId(entity.getCustomerId())
                .customerName(entity.getCustomer() != null ? entity.getCustomer().getName() : null)
                .cui(entity.getCui() != null ? entity.getCui() : null)
                .contractDate(entity.getContractDate())
                .monthOfWarranty(entity.getMonthOfWarranty())
                .monthOfWarrantyHandPieces(entity.getMonthOfWarrantyHandPieces())
                .numberOfContract(entity.getNumberOfContract())
                .equipments(equipmentDtos)
                .trainedPersons(trainedPersonDtos)
                .signatureDate(entity.getSignatureDate())
                .contactPerson(entity.getContactPerson())
                .build();
    }

    public static DocumentEquipmentDto toEquipmentDto(DocumentEquipment entity) {
        if (entity == null) {
            return null;
        }
        String equipmentName = entity.getEquipmentName();
        if (equipmentName == null && entity.getEquipment() != null) {
            equipmentName = entity.getEquipment().getModel();
        }
        return DocumentEquipmentDto.builder()
                .id(entity.getId())
                .equipmentId(entity.getEquipmentId())
                .equipmentName(equipmentName)
                .productCode(entity.getProductCode())
                .serialNumber(entity.getSerialNumber())
                .sortOrder(entity.getSortOrder())
                .build();
    }

    public static DocumentTrainedPersonDto toTrainedPersonDto(DocumentTrainedPerson entity) {
        if (entity == null) {
            return null;
        }
        return DocumentTrainedPersonDto.builder()
                .id(entity.getId())
                .trainedPersonName(entity.getTrainedPersonName())
                .jobFunction(entity.getJobFunction())
                .phone(entity.getPhone())
                .email(entity.getEmail())
                .signatureBase64(entity.getSignatureBase64())
                .sortOrder(entity.getSortOrder())
                .build();
    }

    public static DocumentData toEntity(DocumentDataDto dto) {
        if (dto == null) {
            return null;
        }

        List<DocumentEquipment> equipments = new ArrayList<>();
        if (dto.getEquipments() != null) {
            equipments = dto.getEquipments().stream()
                    .map(DocumentDataMapper::toEquipmentEntity)
                    .collect(Collectors.toList());
        }

        List<DocumentTrainedPerson> trainedPersons = new ArrayList<>();
        if (dto.getTrainedPersons() != null) {
            trainedPersons = dto.getTrainedPersons().stream()
                    .map(DocumentDataMapper::toTrainedPersonEntity)
                    .collect(Collectors.toList());
        }

        return DocumentData.builder()
                .id(dto.getId())
                .customerId(dto.getCustomerId())
                .cui(dto.getCui())
                .contractDate(dto.getContractDate())
                .monthOfWarranty(dto.getMonthOfWarranty())
                .monthOfWarrantyHandPieces(dto.getMonthOfWarrantyHandPieces())
                .numberOfContract(dto.getNumberOfContract())
                .equipments(equipments)
                .trainedPersons(trainedPersons)
                .signatureDate(dto.getSignatureDate())
                .contactPerson(dto.getContactPerson())
                .build();
    }

    public static DocumentEquipment toEquipmentEntity(DocumentEquipmentDto dto) {
        if (dto == null) {
            return null;
        }
        return DocumentEquipment.builder()
                .id(dto.getId())
                .equipmentId(dto.getEquipmentId())
                .equipmentName(dto.getEquipmentName())
                .productCode(dto.getProductCode())
                .serialNumber(dto.getSerialNumber())
                .sortOrder(dto.getSortOrder())
                .build();
    }

    public static DocumentTrainedPerson toTrainedPersonEntity(DocumentTrainedPersonDto dto) {
        if (dto == null) {
            return null;
        }
        return DocumentTrainedPerson.builder()
                .id(dto.getId())
                .trainedPersonName(dto.getTrainedPersonName())
                .jobFunction(dto.getJobFunction())
                .phone(dto.getPhone())
                .email(dto.getEmail())
                .signatureBase64(dto.getSignatureBase64())
                .sortOrder(dto.getSortOrder())
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
