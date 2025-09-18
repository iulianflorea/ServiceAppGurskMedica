package com.example.ServiceApp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

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
            System.err.println("Eroare la trimiterea emailului:");
            e.printStackTrace();
        }
    }


}
