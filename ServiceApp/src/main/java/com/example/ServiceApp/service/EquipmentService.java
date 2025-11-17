package com.example.ServiceApp.service;

import com.example.ServiceApp.dto.EquipmentDto;
import com.example.ServiceApp.dto.InterventionSheetDto;
import com.example.ServiceApp.entity.Equipment;
import com.example.ServiceApp.entity.InterventionSheet;
import com.example.ServiceApp.mapper.EquipmentMapper;
import com.example.ServiceApp.repository.EquipmentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EquipmentService {

    private final EquipmentRepository equipmentRepository;
    private final EquipmentMapper equipmentMapper;

    public EquipmentService(EquipmentRepository equipmentRepository, EquipmentMapper equipmentMapper) {
        this.equipmentRepository = equipmentRepository;
        this.equipmentMapper = equipmentMapper;
    }

    public EquipmentDto create(EquipmentDto equipmentDto) {
        Equipment equipmentToBeSaved = equipmentMapper.toEntity(equipmentDto);
        if (equipmentDto.getId() == null) {
            Equipment equipmentSaved = equipmentRepository.save(equipmentToBeSaved);
            return equipmentMapper.toDto(equipmentSaved);
        } else {
            update(equipmentDto);
        }
        return equipmentMapper.toDto(equipmentToBeSaved);
    }

    public EquipmentDto findById(Long id) {
        Equipment equipment = equipmentRepository.findById(id).orElseThrow();
        return equipmentMapper.toDto(equipment);
    }

    public List<EquipmentDto> findAllLast50() {
        Pageable topFifty = PageRequest.of(0, 50, Sort.by(Sort.Direction.DESC, "id"));
        Page<Equipment> equipmentPage = equipmentRepository.findAllByOrderByIdDesc(topFifty);
        return equipmentMapper.toDtoList(equipmentPage.getContent());
    }

    public List<EquipmentDto> findAll(){
        List<Equipment> equipmentList = equipmentRepository.findAll();
        return equipmentMapper.toDtoList(equipmentList);
    }

    public EquipmentDto update(EquipmentDto equipmentDto) {
        Equipment equipmentToBeUpdate = equipmentRepository.findById(equipmentDto.getId()).orElseThrow();
        equipmentToBeUpdate.setModel(equipmentDto.getModel());
        equipmentToBeUpdate.setProductCode(equipmentDto.getProductCode());
        equipmentToBeUpdate.setProducerId(equipmentDto.getProducerId());
        Equipment equipmentUpdated = equipmentRepository.save(equipmentToBeUpdate);
        return equipmentMapper.toDto(equipmentUpdated);
    }

    public void delete(Long id) {
        equipmentRepository.deleteById(id);
    }

    public List<EquipmentDto> search(String keyword) {
        List<Equipment> equipmentList = equipmentRepository.seearchEquipments(keyword);
        return equipmentMapper.toDtoList(equipmentList);
    }


}
