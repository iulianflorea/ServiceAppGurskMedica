package com.example.ServiceApp.controller;

import com.example.ServiceApp.dto.InterventionSheetDto;
import com.example.ServiceApp.service.InterventionSheetService;
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
}
