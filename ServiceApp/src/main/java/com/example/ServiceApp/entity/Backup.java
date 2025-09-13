package com.example.ServiceApp.entity;

import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Data
@Builder
@Entity
public class Backup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "document_path")
    private String documentPath;
    @Column(name = "sql_path")
    private String sqlPath;


    public Backup(String sqlPath, String docPath) {

    }
}
