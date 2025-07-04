package com.nbu.CSCB634.model;

import com.nbu.CSCB634.model.auth.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Teacher extends User {
    @ManyToOne
    @JoinColumn(name = "school_id", nullable = false)
    private School school;

    @ElementCollection
    @CollectionTable(name = "teacher_qualified_subjects", joinColumns = @JoinColumn(name = "teacher_id"))
    @Column(name = "qualifiedSubjects")
    private Set<String> qualifiedSubjects;
}
