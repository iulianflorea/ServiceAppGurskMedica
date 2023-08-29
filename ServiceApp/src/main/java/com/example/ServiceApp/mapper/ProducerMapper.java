package com.example.ServiceApp.mapper;

import com.example.ServiceApp.dto.ProducerDto;
import com.example.ServiceApp.entity.Producer;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ProducerMapper {

    public Producer toProducer(ProducerDto producerDto) {
        return Producer.builder()
                .name(producerDto.getName())
                .build();
    }

    public ProducerDto toDto(Producer producer) {
        return ProducerDto.builder()
                .id(producer.getId())
                .name(producer.getName())
                .build();
    }
}
