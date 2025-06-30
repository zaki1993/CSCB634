package com.nbu.CSCB869.model.diploma.thesis.review;

import com.nbu.CSCB869.model.diploma.defense.DiplomaDefense;
import com.nbu.CSCB869.model.diploma.thesis.DiplomaThesis;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ThesisReviewTest {

    private ThesisReview review;

    @BeforeEach
    void setUp() {
        review = new ThesisReview();
        review.setId(1L);
        review.setUploadTime(LocalDateTime.of(2025, 1, 10, 14, 30));
        review.setText("This is a sample review text.");
        review.setReviewOutcome(ReviewOutcome.POSITIVE);
        review.setConclusion("Approved.");
        review.setThesis(new DiplomaThesis());
    }

    @Test
    void testDefaultValues() {
        assertNotNull(review.getUploadTime(), "Upload time should not be null.");
        assertNotNull(review.getText(), "Review text should not be null.");
        assertNotNull(review.getReviewOutcome(), "Review outcome should not be null.");
        assertNotNull(review.getConclusion(), "Conclusion should not be null.");
        assertNotNull(review.getThesis(), "Thesis should not be null.");
        assertNull(review.getDefense(), "Defense should be null by default.");
    }

    @Test
    void testIsReadOnly_NoDefense() {
        review.setDefense(null); // No associated defense
        assertFalse(review.isReadOnly(), "Review should not be read-only when there is no defense.");
    }

    @Test
    void testIsReadOnly_WithDefense() {
        DiplomaDefense defense = new DiplomaDefense();
        review.setDefense(defense); // Associate a defense
        assertTrue(review.isReadOnly(), "Review should be read-only when there is a defense.");
    }

    @Test
    void testSetReviewOutcome() {
        review.setReviewOutcome(ReviewOutcome.NEGATIVE);
        assertEquals(ReviewOutcome.NEGATIVE, review.getReviewOutcome(), "Review outcome should be set to NEGATIVE.");
    }

    @Test
    void testSetUploadTime() {
        LocalDateTime newUploadTime = LocalDateTime.of(2025, 2, 15, 10, 0);
        review.setUploadTime(newUploadTime);
        assertEquals(newUploadTime, review.getUploadTime(), "Upload time should be updated.");
    }

    @Test
    void testSetText() {
        String newText = "Updated review text.";
        review.setText(newText);
        assertEquals(newText, review.getText(), "Review text should be updated.");
    }

    @Test
    void testSetConclusion() {
        String newConclusion = "Rejected.";
        review.setConclusion(newConclusion);
        assertEquals(newConclusion, review.getConclusion(), "Conclusion should be updated.");
    }

    @Test
    void testSetThesis() {
        DiplomaThesis newThesis = new DiplomaThesis();
        review.setThesis(newThesis);
        assertEquals(newThesis, review.getThesis(), "Thesis should be updated.");
    }

    @Test
    void testSetDefense() {
        DiplomaDefense newDefense = new DiplomaDefense();
        review.setDefense(newDefense);
        assertEquals(newDefense, review.getDefense(), "Defense should be updated.");
    }
}
