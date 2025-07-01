package com.nbu.CSCB634.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Absence {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private LocalDate absenceDate;

    private Boolean justified;

    @ManyToOne
    private Student student;

    @ManyToOne
    private Teacher teacher;

    @ManyToOne
    private Subject subject;
}