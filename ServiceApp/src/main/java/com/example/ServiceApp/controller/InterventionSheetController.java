package com.example.ServiceApp.controller;

import com.example.ServiceApp.dto.InterventionSheetDto;
import com.example.ServiceApp.entity.TypeOfIntervention;
import com.example.ServiceApp.service.InterventionSheetService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/intervention-sheet")
public class InterventionSheetController {

    private final InterventionSheetService interventionSheetService;

    public InterventionSheetController(InterventionSheetService interventionSheetService) {
        this.interventionSheetService = interventionSheetService;
    }
@PostMapping
    public InterventionSheetDto create(@RequestBody InterventionSheetDto interventionSheetDto) {
        return interventionSheetService.create(interventionSheetDto);
    }

    @GetMapping("/{id}")
    public InterventionSheetDto findById(@PathVariable Long id) {
        return interventionSheetService.findById(id);
    }

    @GetMapping("/find-all")
    public List<InterventionSheetDto> findAll(){
        return interventionSheetService.findAll();
    }

    @PutMapping("/update")
    public InterventionSheetDto update(@RequestBody InterventionSheetDto interventionSheetDto) {
        return interventionSheetService.update(interventionSheetDto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        interventionSheetService.delete(id);
    }

@GetMapping("/type")
    public ResponseEntity<String> getStatusList() throws JsonProcessingException {
        ObjectMapper objectMapper =new ObjectMapper();
        String enumAsjson = objectMapper.writeValueAsString(TypeOfIntervention.values());
        return ResponseEntity.ok(enumAsjson);
    }
}
