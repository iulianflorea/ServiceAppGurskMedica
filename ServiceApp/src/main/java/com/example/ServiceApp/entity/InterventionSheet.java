package com.example.ServiceApp.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Entity
public class InterventionSheet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(value = EnumType.STRING)
    private TypeOfIntervention typeOfIntervention;
    @Column(name = "equipment_id")
    private Long equipmentId;
    @ManyToOne(targetEntity = Equipment.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "equipment_id", insertable = false, updatable = false)
    private Equipment equipment;
    private String serialNumber;
    @Column(name = "date_of_intervention")
    private LocalDate dateOfIntervention;
    @Column(name = "customer_id")
    private Long customerId;
    @ManyToOne(targetEntity = Customer.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "customer_id", insertable = false, updatable = false)
    private Customer customer;
    private Integer yearsOfWarranty;
    @Column(name = "employee_id")
    private Long employeeId;
    @ManyToOne(targetEntity = Employee.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "employee_id", insertable = false, updatable = false)
    private Employee employee;
    private String noticed;
    private String fixed;
    private String engineerNote;
    @Lob
    private String signatureBase64;
    private LocalDate dataOfExpireWarranty;
}
