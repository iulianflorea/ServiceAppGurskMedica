package com.example.ServiceApp.controller;

import com.example.ServiceApp.dto.OrderFormDto;
import com.example.ServiceApp.dto.OrderProductDto;
import com.example.ServiceApp.entity.Customer;
import com.example.ServiceApp.entity.Product;
import com.example.ServiceApp.repository.CustomerRepository;
import com.example.ServiceApp.repository.ProductRepository;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.mail.SimpleMailMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;
    private final JavaMailSender mailSender;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> handleOrder(
            @RequestPart("orderForm") OrderFormDto orderForm,
            @RequestPart(value = "pdfFile", required = false) MultipartFile pdfFile
    ) {
        List<OrderProductDto> validProducts = orderForm.getProducts() == null ? new ArrayList<>() :
                orderForm.getProducts().stream()
                        .filter(p -> p.getProductId() != null && p.getQuantity() > 0)
                        .toList();

        if ((orderForm.getClientId() == null) && validProducts.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Trebuie să selectezi un client sau produse valide.");
        }

        StringBuilder emailBody = new StringBuilder("Buna ziua,\n\n");

        if (orderForm.getClientId() != null) {
            Customer customer = customerRepository.findById(orderForm.getClientId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Client not found"));

            emailBody.append("Va rog sa facturati catre ")
                    .append(customer.getName())
                    .append(", CUI: ").append(customer.getCui())
                    .append(", adresa: ").append(customer.getAddress())
                    .append("\nSi sa scadeti din gestiunea service, urmatoarele:\n")
                    .append("\n");
            if(pdfFile != null){
                emailBody.append("Conform devizului atasat\n\n");
            }

            for (OrderProductDto item : validProducts) {
                Product product = productRepository.findById(item.getProductId())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));

                if (product.getQuantity() < item.getQuantity()) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Stoc insuficient pentru produsul " + product.getName());
                }

                product.setQuantity(product.getQuantity() - item.getQuantity());
                productRepository.save(product);

                emailBody.append(product.getCod())
                        .append(" - ").append(product.getName())
                        .append(": Cantitate: ").append(item.getQuantity())
                        .append(" unitati\n");
            }
            if (orderForm.getDeliveryAddress() != null && !orderForm.getDeliveryAddress().isEmpty()) {
                emailBody.append("\nAdresa de livrare este: ").append(orderForm.getDeliveryAddress()).append("\n");
            } else {
                emailBody.append("\nAdresa de livrare este: ").append(customer.getAddress()).append("\n");
            }

            emailBody.append("\nVa mulțumim!\nCu stimă,\nEchipa Service");

            if (pdfFile != null && !pdfFile.isEmpty()) {
                sendEmailWithPdf("comenzi@gurskmedica.ro",
                        "Facturare catre " + customer.getName(),
                        emailBody.toString(),
                        pdfFile);
                sendEmailWithPdf("service@gurskmedica.ro",
                        "Facturare catre " + customer.getName(),
                        emailBody.toString(),
                        pdfFile);
            } else {
                sendEmailSimple("comenzi@gurskmedica.ro",
                        "Facturare catre " + customer.getName(),
                        emailBody.toString());
                sendEmailSimple("service@gurskmedica.ro",
                        "Facturare catre " + customer.getName(),
                        emailBody.toString());
            }
        }

        return ResponseEntity.ok(Map.of("message", "Comanda procesată cu succes"));
    }


    private void sendEmailSimple(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("office@singularity-ai.eu");
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }

    private void sendEmailWithPdf(String to, String subject, String text, MultipartFile pdfFile) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

            helper.setFrom("office@singularity-ai.eu");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text);

            helper.addAttachment("deviz.pdf", new ByteArrayResource(pdfFile.getBytes()));

            mailSender.send(mimeMessage);
        } catch (Exception e) {
            throw new RuntimeException("Eroare la trimiterea emailului cu PDF", e);
        }
    }
}


