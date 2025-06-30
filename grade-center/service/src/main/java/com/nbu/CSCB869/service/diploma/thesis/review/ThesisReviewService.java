package com.nbu.CSCB869.service.diploma.thesis.review;

import com.nbu.CSCB869.model.diploma.thesis.review.ThesisReview;
import com.nbu.CSCB869.repository.diploma.thesis.review.ThesisReviewRepository;
import com.nbu.CSCB869.service.exceptions.ThesisReviewNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ThesisReviewService {
    private final ThesisReviewRepository thesisReviewRepository;

    public void createThesisReview(ThesisReview review) {
        // Set when the diploma thesis was created
        review.setUploadTime(LocalDateTime.now());

        thesisReviewRepository.save(review);
    }

    public void saveThesisReview(ThesisReview toUpdate) {
        thesisReviewRepository.save(toUpdate);
    }

    public List<ThesisReview> getAllReviews() {
        return thesisReviewRepository.findAll();
    }

    public ThesisReview getById(Long reviewId) {
        return thesisReviewRepository.findById(reviewId).orElseThrow(() -> new ThesisReviewNotFoundException(reviewId));
    }

    public long getNegativeReviewsCount() {
        return thesisReviewRepository.countNegativeReviews();
    }
}
