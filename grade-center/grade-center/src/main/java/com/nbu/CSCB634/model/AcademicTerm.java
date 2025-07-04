package com.nbu.CSCB634.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDate;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AcademicTerm {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name; // E.g. "2023 Fall", "2024 Spring"

    private LocalDate startDate;

    private LocalDate endDate;

    @ManyToOne
    @JoinColumn(name = "school_id")
    private School school;
    @ManyToMany
    @JoinTable(
            name = "academicterm_subjects",
            joinColumns = @JoinColumn(name = "academic_term_id"),
            inverseJoinColumns = @JoinColumn(name = "subject_id")
    )
    private Set<Subject> subjects;

    @ManyToMany
    @JoinTable(
            name = "academicterm_teachers",
            joinColumns = @JoinColumn(name = "academic_term_id"),
            inverseJoinColumns = @JoinColumn(name = "teacher_id")
    )
    private Set<Teacher> teachers;
}