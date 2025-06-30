package com.nbu.CSCB634.service.diploma.thesis.review;

import com.nbu.CSCB634.model.diploma.thesis.review.ThesisReview;
import com.nbu.CSCB634.repository.diploma.thesis.review.ThesisReviewRepository;
import com.nbu.CSCB634.service.exceptions.ThesisReviewNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ThesisReviewServiceTest {

    @Mock
    private ThesisReviewRepository thesisReviewRepository;

    @InjectMocks
    private ThesisReviewService thesisReviewService;

    @Test
    void createThesisReview() {
        ThesisReview review = new ThesisReview();
        when(thesisReviewRepository.save(review)).thenReturn(review);

        thesisReviewService.createThesisReview(review);

        verify(thesisReviewRepository, times(1)).save(review);
    }

    @Test
    void getById_found() {
        ThesisReview review = new ThesisReview();
        when(thesisReviewRepository.findById(1L)).thenReturn(Optional.of(review));

        var result = thesisReviewService.getById(1L);

        assertNotNull(result);
    }

    @Test
    void getById_notFound() {
        when(thesisReviewRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ThesisReviewNotFoundException.class, () -> thesisReviewService.getById(1L));
    }

    @Test
    void getAllReviews() {
        ThesisReview review1 = new ThesisReview();
        ThesisReview review2 = new ThesisReview();
        when(thesisReviewRepository.findAll()).thenReturn(Arrays.asList(review1, review2));

        var reviews = thesisReviewService.getAllReviews();

        assertEquals(2, reviews.size());
    }

    @Test
    void getNegativeReviewsCount() {
        when(thesisReviewRepository.countNegativeReviews()).thenReturn(5L);

        long count = thesisReviewService.getNegativeReviewsCount();

        assertEquals(5, count);
    }
}
