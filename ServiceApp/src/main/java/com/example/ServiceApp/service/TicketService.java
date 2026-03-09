package com.example.ServiceApp.service;

import com.example.ServiceApp.dto.TicketDto;
import com.example.ServiceApp.entity.Ticket;
import com.example.ServiceApp.entity.TicketStatus;
import com.example.ServiceApp.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;

    public TicketDto create(TicketDto dto) {
        Ticket ticket = Ticket.builder()
                .clinicName(dto.getClinicName())
                .equipmentModel(dto.getEquipmentModel())
                .equipmentBrand(dto.getEquipmentBrand())
                .serialNumber(dto.getSerialNumber())
                .phone(dto.getPhone())
                .email(dto.getEmail())
                .problem(dto.getProblem())
                .city(dto.getCity())
                .address(dto.getAddress())
                .status(TicketStatus.IN_ASTEPTARE)
                .createdAt(LocalDateTime.now())
                .build();
        return toDto(ticketRepository.save(ticket));
    }

    public List<TicketDto> findAll() {
        return ticketRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public TicketDto updateStatus(Long id, TicketStatus status) {
        Ticket ticket = ticketRepository.findById(id).orElseThrow();
        ticket.setStatus(status);
        return toDto(ticketRepository.save(ticket));
    }

    public void delete(Long id) {
        ticketRepository.deleteById(id);
    }

    private TicketDto toDto(Ticket t) {
        return TicketDto.builder()
                .id(t.getId())
                .clinicName(t.getClinicName())
                .equipmentModel(t.getEquipmentModel())
                .equipmentBrand(t.getEquipmentBrand())
                .serialNumber(t.getSerialNumber())
                .phone(t.getPhone())
                .email(t.getEmail())
                .problem(t.getProblem())
                .city(t.getCity())
                .address(t.getAddress())
                .status(t.getStatus())
                .createdAt(t.getCreatedAt())
                .build();
    }
}
