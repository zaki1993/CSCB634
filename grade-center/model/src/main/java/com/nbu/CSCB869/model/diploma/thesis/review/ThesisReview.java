package com.nbu.CSCB869.model.diploma.thesis.review;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nbu.CSCB869.model.diploma.defense.DiplomaDefense;
import com.nbu.CSCB869.model.diploma.thesis.DiplomaThesis;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import java.time.LocalDateTime;

/**
 * Entity representing a Review for a Diploma Assignment.
 * Includes date, text, conclusion (positive/negative), and reference to the assignment being reviewed.
 */
@Entity
@Data
@NoArgsConstructor
@Table(name = "thesisreview")
public class ThesisReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "upload_time", nullable = false)
    private LocalDateTime uploadTime;

    @Column(nullable = false, length = 2000)
    private String text;

    @Enumerated(EnumType.STRING)
    @Column(name = "review_outcome", nullable = false)
    private ReviewOutcome reviewOutcome;

    @Column(nullable = false)
    private String conclusion;

    @OneToOne
    @JoinColumn(name = "thesis_id", nullable = false)
    @JsonIgnore
    private DiplomaThesis thesis;

    @ManyToOne
    @JoinColumn(name = "defense_id", nullable = true) // Nullable because reviews may exist without a defense
    @JsonIgnore // Prevent serialization of this relationship
    private DiplomaDefense defense;

    @Transient
    public boolean isReadOnly() {
        // If there is a scheduled defense, then this review is read-only
        return defense != null;
    }
}
