package com.example.ServiceApp.repository;

import com.example.ServiceApp.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
}
