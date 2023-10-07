package com.example.ServiceApp.service;

import com.example.ServiceApp.dto.ProducerDto;
import com.example.ServiceApp.entity.Producer;
import com.example.ServiceApp.mapper.ProducerMapper;
import com.example.ServiceApp.repository.ProducerRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProducerService {
    private final ProducerRepository producerRepository;
    private final ProducerMapper producerMapper;

    public ProducerService(ProducerRepository producerRepository, ProducerMapper producerMapper) {
        this.producerRepository = producerRepository;
        this.producerMapper = producerMapper;
    }

    public Producer create(ProducerDto producerDto) {
        Producer producer = producerMapper.toProducer(producerDto);
        return producerRepository.save(producer);
    }

    public ProducerDto findById(Long id) {
        Producer producer = producerRepository.findById(id).orElseThrow();
        return producerMapper.toDto(producer);
    }

    public List<ProducerDto> findAll() {
        List<Producer> producers = producerRepository.findAll();
        return producerMapper.toDtoList(producers);
    }

    public Producer update(ProducerDto producerDto) {
        Producer producer = producerRepository.findById(producerDto.getId()).orElseThrow();
        producer.setName(producerDto.getName());
        return producerRepository.save(producer);
    }

    public void delete(Long id) {
        producerRepository.deleteById(id);
    }


}
