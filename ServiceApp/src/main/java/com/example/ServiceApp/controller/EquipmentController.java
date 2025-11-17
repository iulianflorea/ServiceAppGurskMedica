package com.example.ServiceApp.controller;

import com.example.ServiceApp.dto.EquipmentDto;
import com.example.ServiceApp.dto.InterventionSheetDto;
import com.example.ServiceApp.service.EquipmentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/equipment")
public class EquipmentController {

    private final EquipmentService equipmentService;

    public EquipmentController(EquipmentService equipmentService) {
        this.equipmentService = equipmentService;
    }

    @PostMapping
    public EquipmentDto create(@RequestBody EquipmentDto equipmentDto) {
        return equipmentService.create(equipmentDto);
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

    @PutMapping
    public EquipmentDto update(@RequestBody EquipmentDto equipmentDto) {
        return equipmentService.update(equipmentDto);
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
