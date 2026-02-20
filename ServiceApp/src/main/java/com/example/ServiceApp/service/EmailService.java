package com.example.ServiceApp.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Slf4j
@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendProductUpdateEmail(String to, String productCode, int quantityChange) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setFrom("office@singularity-ai.eu");  // <- adresa reală de expeditor
            message.setSubject("Modificare cantitate produs");
            message.setText("Buna ziua, \n" +
                    "\nProdusul cu codul " + productCode + " a fost modificat. Cantitatea a fost scăzută cu " + quantityChange + " unități." +
                    "\nVa rog sa-l scadeti din gestiunea service" +
                    "\n\nMultumim," +
                    "\nEchipa service");
            mailSender.send(message);
            System.out.println("Email trimis cu succes!");
        } catch (Exception e) {
            log.error("Eroare la trimiterea emailului către {}: {}", to, e.getMessage(), e);
        }
    }

    public void sendItpExpiryAlert(String to, String licensePlate, LocalDate expiryDate, int daysLeft) {
        try {
            log.info("Trimit alertă ITP → {} | mașina: {} | expiră: {} | zile rămase: {}", to, licensePlate, expiryDate, daysLeft);
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setFrom("office@singularity-ai.eu");
            message.setSubject("Alertă ITP — " + licensePlate + " expiră în " + daysLeft + " zile");
            message.setText("Bună ziua,\n\n" +
                    "ITP-ul mașinii cu numărul " + licensePlate + " expiră pe " + expiryDate +
                    " (în " + daysLeft + " zile).\n\n" +
                    "Vă rugăm să programați reinspecția tehnică.\n\n" +
                    "Mulțumim,\nEchipa service");
            mailSender.send(message);
            log.info("Alertă ITP trimisă cu succes → {}", to);
        } catch (Exception e) {
            log.error("EROARE trimitere alertă ITP către {} ({}): {}", to, licensePlate, e.getMessage(), e);
        }
    }

    public void sendInsuranceExpiryAlert(String to, String licensePlate, LocalDate expiryDate, int daysLeft) {
        try {
            log.info("Trimit alertă asigurare → {} | mașina: {} | expiră: {} | zile rămase: {}", to, licensePlate, expiryDate, daysLeft);
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setFrom("office@singularity-ai.eu");
            message.setSubject("Alertă Asigurare — " + licensePlate + " expiră în " + daysLeft + " zile");
            message.setText("Bună ziua,\n\n" +
                    "Asigurarea auto a mașinii cu numărul " + licensePlate + " expiră pe " + expiryDate +
                    " (în " + daysLeft + " zile).\n\n" +
                    "Vă rugăm să reînnoiți polița de asigurare.\n\n" +
                    "Mulțumim,\nEchipa service");
            mailSender.send(message);
            log.info("Alertă asigurare trimisă cu succes → {}", to);
        } catch (Exception e) {
            log.error("EROARE trimitere alertă asigurare către {} ({}): {}", to, licensePlate, e.getMessage(), e);
        }
    }


}
