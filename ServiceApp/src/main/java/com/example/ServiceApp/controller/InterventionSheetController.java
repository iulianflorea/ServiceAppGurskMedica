package com.example.ServiceApp.controller;

import com.example.ServiceApp.dto.InterventionSheetDto;
import com.example.ServiceApp.service.InterventionSheetService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
