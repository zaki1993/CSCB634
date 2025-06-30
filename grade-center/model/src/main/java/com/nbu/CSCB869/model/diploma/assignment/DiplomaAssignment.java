package com.nbu.CSCB869.model.diploma.assignment;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nbu.CSCB869.model.Student;
import com.nbu.CSCB869.model.Teacher;
import com.nbu.CSCB869.model.diploma.thesis.DiplomaThesis;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;


/**
 * Entity representing a Diploma Assignment.
 * Contains information about the diploma topic, purpose, tasks, technologies, student, and supervisor.
 */
@Entity
@Data
@NoArgsConstructor
public class DiplomaAssignment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String purpose;
    private String tasks;
    private String technologies;
    private boolean approved;

    @ManyToOne
    private Student student;

    @ManyToOne
    private Teacher teacher;

    @OneToOne(mappedBy = "assignment", targetEntity = DiplomaThesis.class, fetch=FetchType.LAZY)
    @JsonIgnore
    private DiplomaThesis thesis;

    @Transient
    public boolean isReadOnly() {
        // The assignment is read only if there is no teacher (this should never happen)
        // or the current user is not the teacher which created the assignment
        // or the assignment is already approved (no changes can be done after approval)
        return teacher == null || !teacher.getUsername().equals(getCurrentUser()) || approved;
    }

    @Transient
    public boolean hasDiplomaThesis() {
        return thesis != null;
    }

    private String getCurrentUser() {
        return SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
    }
}
