package com.nbu.CSCB634.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SchoolClass {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Example: "1A", "12B" etc
     */
    @NotBlank
    private String name;

    private Integer gradeNumber; // 1..12

    @ManyToOne
    @JoinColumn(name = "school_id")
    private School school;
}