package com.nbu.CSCB634.repository.diploma.thesis.review;

import com.nbu.CSCB634.model.diploma.thesis.review.ThesisReview;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ThesisReviewRepository extends JpaRepository<ThesisReview, Long> {
    @Modifying
    @Transactional
    @Query("UPDATE ThesisReview tr SET tr.defense = null WHERE tr.defense.id = :defenseId")
    void removeDiplomaDefenseReferences(@Param("defenseId") Long defenseId);

    @Query("SELECT COUNT(tr) FROM ThesisReview tr WHERE tr.reviewOutcome = 'NEGATIVE'")
    long countNegativeReviews();
}
