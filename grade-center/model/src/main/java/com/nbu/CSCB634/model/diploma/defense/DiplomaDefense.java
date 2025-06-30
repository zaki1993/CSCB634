package com.nbu.CSCB634.model.diploma.defense;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Entity representing a Diploma Defense procedure.
 * Contains details such as defense date, associated students and teachers, and scores.
 */
@Entity
@Data
@NoArgsConstructor
public class DiplomaDefense {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime defenseDate;

    @Column(nullable = false)
    private String location;

    @ElementCollection
    @CollectionTable(name = "defense_thesis", joinColumns = @JoinColumn(name = "defense_id"))
    @Column(name = "thesis_id")
    private List<Long> theses; // Stores only the IDs of the theses

    // List of Teacher IDs
    @ElementCollection
    @CollectionTable(name = "defense_teacher", joinColumns = @JoinColumn(name = "defense_id"))
    @Column(name = "teacher_id")
    private List<Long> teachers;

    // List of Student IDs
    @ElementCollection
    @CollectionTable(name = "defense_student", joinColumns = @JoinColumn(name = "defense_id"))
    @Column(name = "student_id")
    private List<Long> students;

    // Map to store grades for each thesis
    @ElementCollection
    @CollectionTable(name = "defense_grades", joinColumns = @JoinColumn(name = "defense_id"))
    @MapKeyColumn(name = "thesis_id")
    @Column(name = "grade")
    private Map<Long, Double> gradles = new HashMap<>(); // Thesis ID -> Grade

    /**
     * Checks if the defense is read-only.
     * A defense is read-only if it has any associated grades.
     *
     * @return true if the defense has grades, false otherwise.
     */
    public boolean isReadOnly() {
        return gradles != null && !gradles.isEmpty();
    }

}
