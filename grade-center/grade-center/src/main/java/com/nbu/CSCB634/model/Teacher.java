package com.nbu.CSCB634.model;

import com.nbu.CSCB634.model.auth.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "teachers")
@Builder
public class Teacher {
    @Id
    private Long id;  // Same as User.id

    @OneToOne(cascade = CascadeType.REMOVE)
    @MapsId
    @JoinColumn(name = "id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "school_id", nullable = false)
    private School school;

    @ManyToMany
    @JoinTable(
            name = "teacher_qualified_subjects",
            joinColumns = @JoinColumn(name = "teacher_id"),
            inverseJoinColumns = @JoinColumn(name = "subject_id")
    )
    private Set<Subject> qualifiedSubjects;

    // Каскадно изтриване на оценки дадени от този учител
    @OneToMany(mappedBy = "teacher", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Grade> grades;

    // Каскадно изтриване на отсъствия отбелязани от този учител
    @OneToMany(mappedBy = "teacher", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Absence> absences;
}
