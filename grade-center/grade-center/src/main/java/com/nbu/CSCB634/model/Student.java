package com.nbu.CSCB634.model;

import com.nbu.CSCB634.model.auth.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "students")
public class Student {
    @Id
    private Long id;  // Same as User.id

    @OneToOne(cascade = CascadeType.REMOVE)
    @MapsId
    @JoinColumn(name = "id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "school_id", nullable = false)
    private School school;

    @ManyToOne
    @JoinColumn(name = "class_id")
    private SchoolClass schoolClass;

    // Example of ManyToMany with Parents
    @ManyToMany(mappedBy = "students")
    private List<Parent> parents;

    // Каскадно изтриване на оценки на този ученик
    @OneToMany(mappedBy = "student", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Grade> grades;

    // Каскадно изтриване на отсъствия на този ученик
    @OneToMany(mappedBy = "student", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Absence> absences;
}
