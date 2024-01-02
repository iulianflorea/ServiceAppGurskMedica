package com.example.ServiceApp.controller;

import com.example.ServiceApp.dto.InterventionSheetDto;
import com.example.ServiceApp.service.InterventionSheetService;
import org.springframework.web.bind.annotation.*;

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
}
