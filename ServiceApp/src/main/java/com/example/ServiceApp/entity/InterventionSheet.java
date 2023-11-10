package com.example.ServiceApp.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class InterventionSheet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private TypeOfIntervention typeOfIntervention;
    @OneToMany(mappedBy = "interventionId")
    private List<Equipment> equipmentId;
    private Date dateOfIntervention;
    @ManyToOne
    private Customer customerId;
    @ManyToOne
    private Employee employeeId;
    private String noticed;
    private String fixed;
    private String engineerNote;

}
