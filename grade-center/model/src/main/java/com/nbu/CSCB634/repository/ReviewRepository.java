package com.nbu.CSCB634.repository;

import com.nbu.CSCB634.model.diploma.thesis.review.ThesisReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing Review entities.
 * Provides CRUD operations and allows custom queries based on review conclusions.
 */
@Repository
public interface ReviewRepository extends JpaRepository<ThesisReview, Long> {

    /**
     * Counts the number of reviews with a specified conclusion.
     *
     * @param conclusion Conclusion type (e.g., "Positive" or "Negative")
     * @return Count of reviews with the specified conclusion
     */
    long countByConclusion(String conclusion);
}
