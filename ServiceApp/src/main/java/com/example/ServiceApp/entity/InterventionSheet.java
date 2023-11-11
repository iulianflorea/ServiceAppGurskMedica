package com.example.ServiceApp.entity;

import jakarta.persistence.*;
import lombok.*;

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
    @ManyToOne
    @JoinColumn(name = "equipment_id")
    private Equipment equipmentId;
    private String serialNumber;
    private Date dateOfIntervention;
    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customerId;
    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employeeId;
    private String noticed;
    private String fixed;
    private String engineerNote;

}
