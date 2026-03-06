package com.example.ServiceApp.service;

import com.example.ServiceApp.dto.EquipmentDto;
import com.example.ServiceApp.entity.Equipment;
import com.example.ServiceApp.mapper.EquipmentMapper;
import com.example.ServiceApp.repository.EquipmentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class EquipmentService {

    @Value("${upload.path}")
    private String uploadPath;

    private final EquipmentRepository equipmentRepository;
    private final EquipmentMapper equipmentMapper;

    public EquipmentService(EquipmentRepository equipmentRepository, EquipmentMapper equipmentMapper) {
        this.equipmentRepository = equipmentRepository;
        this.equipmentMapper = equipmentMapper;
    }

    public ResponseEntity<EquipmentDto> saveOrUpdateEquipment(Long id, String model, String productCode, Long producerId, MultipartFile image) {
        Equipment equipment;

        if (id != null) {
            Optional<Equipment> optional = equipmentRepository.findById(id);
            if (optional.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            equipment = optional.get();
        } else {
            equipment = new Equipment();
        }

        equipment.setModel(model);
        equipment.setProductCode(productCode);
        equipment.setProducerId(producerId);

        if (image != null && !image.isEmpty()) {
            String imageName = UUID.randomUUID() + "_" + image.getOriginalFilename();
            Path uploadDir = Paths.get(uploadPath);
            try {
                if (!Files.exists(uploadDir)) {
                    Files.createDirectories(uploadDir);
                }
                Path filePath = uploadDir.resolve(imageName);
                Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                equipment.setImageName(imageName);
            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        }

        Equipment saved = equipmentRepository.save(equipment);
        return ResponseEntity.ok(equipmentMapper.toDto(saved));
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

    public List<EquipmentDto> findAll() {
        List<Equipment> equipmentList = equipmentRepository.findAll();
        return equipmentMapper.toDtoList(equipmentList);
    }

    public void delete(Long id) {
        equipmentRepository.deleteById(id);
    }

    public List<EquipmentDto> search(String keyword) {
        List<Equipment> equipmentList = equipmentRepository.seearchEquipments(keyword);
        return equipmentMapper.toDtoList(equipmentList);
    }

}
