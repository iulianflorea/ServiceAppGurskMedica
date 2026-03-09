package com.example.ServiceApp.controller;

import com.example.ServiceApp.dto.TicketDto;
import com.example.ServiceApp.entity.TicketStatus;
import com.example.ServiceApp.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tickets")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    @PostMapping
    public ResponseEntity<TicketDto> create(@RequestBody TicketDto dto) {
        return ResponseEntity.ok(ticketService.create(dto));
    }

    @GetMapping
    public ResponseEntity<List<TicketDto>> findAll() {
        return ResponseEntity.ok(ticketService.findAll());
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<TicketDto> updateStatus(@PathVariable Long id, @RequestParam String status) {
        return ResponseEntity.ok(ticketService.updateStatus(id, TicketStatus.valueOf(status)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        ticketService.delete(id);
        return ResponseEntity.ok().build();
    }
}
