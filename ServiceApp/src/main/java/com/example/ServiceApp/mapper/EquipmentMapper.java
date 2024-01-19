package com.example.ServiceApp.mapper;

import com.example.ServiceApp.dto.EquipmentDto;
import com.example.ServiceApp.entity.Equipment;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class EquipmentMapper {

    public Equipment toEntity(EquipmentDto equipmentDto) {
        return Equipment.builder()
                .model(equipmentDto.getModel())
                .producerId(equipmentDto.getProducerId())
                .build();
    }

    public EquipmentDto toDto(Equipment equipment) {
        return EquipmentDto.builder()
                .id(equipment.getId())
                .model(equipment.getModel())
                .producerId(equipment.getProducerId())
                .build();
    }

    public List<EquipmentDto> toDtoList(List<Equipment> equipmentList) {
        List<EquipmentDto> equipmentDtoList = new ArrayList<>();
        for (Equipment equipment : equipmentList) {
            EquipmentDto equipmentDto = toDto(equipment);
            equipmentDtoList.add(equipmentDto);
        }
        return equipmentDtoList;
    }
}
