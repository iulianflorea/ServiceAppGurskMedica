package com.example.ServiceApp.controller;

import com.example.ServiceApp.dto.EquipmentDto;
import com.example.ServiceApp.service.EquipmentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/equipment")
public class EquipmentController {

    private final EquipmentService equipmentService;

    public EquipmentController(EquipmentService equipmentService) {
        this.equipmentService = equipmentService;
    }

    @PostMapping
    public ResponseEntity<EquipmentDto> saveOrUpdate(
            @RequestParam(required = false) Long id,
            @RequestParam String model,
            @RequestParam String productCode,
            @RequestParam Long producerId,
            @RequestParam(required = false) MultipartFile image
    ) {
        return equipmentService.saveOrUpdateEquipment(id, model, productCode, producerId, image);
    }

    @GetMapping("find-by-id/{id}")
    public EquipmentDto findById(@PathVariable Long id) {
        return equipmentService.findById(id);
    }

    @GetMapping("/find-all-last-50")
    public List<EquipmentDto> findAllLast50() {
        return equipmentService.findAllLast50();
    }

    @GetMapping("/find-all")
    public List<EquipmentDto> findAll() {
        return equipmentService.findAll();
    }

    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable Long id) {
        equipmentService.delete(id);
    }

    @GetMapping("/search")
    public ResponseEntity<List<EquipmentDto>> search(@RequestParam String keyword) {
        List<EquipmentDto> equipmentDtoList = equipmentService.search(keyword);
        return new ResponseEntity<>(equipmentDtoList, HttpStatus.OK);
    }
}
