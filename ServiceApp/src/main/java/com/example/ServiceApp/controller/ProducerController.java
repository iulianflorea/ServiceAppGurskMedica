package com.example.ServiceApp.controller;

import com.example.ServiceApp.dto.ProducerDto;
import com.example.ServiceApp.entity.Producer;
import com.example.ServiceApp.service.ProducerService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/producer")
public class ProducerController {

    private final ProducerService producerService;

    public ProducerController(ProducerService producerService) {
        this.producerService = producerService;
    }

    @PostMapping
    public Producer create(@RequestBody ProducerDto producerDto) {
        return producerService.create(producerDto);
    }

    @GetMapping("/{id}")
    public ProducerDto getById(@PathVariable Long id) {
        return producerService.findById(id);
    }

    @GetMapping("/getAll")
    public List<ProducerDto> getAll() {
        return producerService.findAll();
    }

    @PutMapping
    public Producer update(@RequestBody ProducerDto producerDto) {
        return producerService.update(producerDto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        producerService.delete(id);
    }
}
