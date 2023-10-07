package com.example.ServiceApp.mapper;

import com.example.ServiceApp.dto.ProducerDto;
import com.example.ServiceApp.entity.Producer;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

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

    public List<ProducerDto> toDtoList(List<Producer> producerList) {
        List<ProducerDto> producerDtoList = new ArrayList<>();
        for (Producer producer : producerList) {
            ProducerDto producerDto = toDto(producer);
            producerDtoList.add(producerDto);
        }
        return producerDtoList;
    }
}
