package com.nbu.CSCB869.controller.diploma.thesis.review;

import com.nbu.CSCB869.model.diploma.thesis.DiplomaThesis;
import com.nbu.CSCB869.model.diploma.thesis.review.ThesisReview;
import com.nbu.CSCB869.service.diploma.thesis.DiplomaThesisService;
import com.nbu.CSCB869.service.diploma.thesis.review.ThesisReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/diploma-review")
@RequiredArgsConstructor
public class DiplomaReviewController {

    private final DiplomaThesisService diplomaThesisService;
    private final ThesisReviewService thesisReviewService;

    /**
     * Displays all diploma assignments for specific username.
     *
     * @param model The model object to which the data will be added.
     * @return The name of the Thymeleaf template to render.
     */
    @GetMapping("/{thesisId}/create")
    @PreAuthorize("hasAuthority('TEACHER')")
    public String getCreateReviewPage(@PathVariable("thesisId") Long thesisId, Model model) {
        // check if the thesis exist
        DiplomaThesis thesis = diplomaThesisService.getThesisById(thesisId);

        ThesisReview review = new ThesisReview();
        review.setThesis(thesis);
        model.addAttribute("diplomaReview", review);
        model.addAttribute("diplomaThesis", thesis);

        return "diploma-review/create-edit";
    }

    /**
     * Displays all diploma assignments for specific username.
     *
     * @param model The model object to which the data will be added.
     * @return The name of the Thymeleaf template to render.
     */
    @PostMapping("/{thesisId}/create")
    @PreAuthorize("hasAuthority('TEACHER')")
    public String createDiplomaReview(@PathVariable("thesisId") Long thesisId, @ModelAttribute("diplomaReview") ThesisReview review, Model model) {
        // Validate if the diploma thesis exist
        DiplomaThesis thesis = diplomaThesisService.getThesisById(thesisId);

        // Check if the thesis has been uploaded
        if (thesis.getUploadTime() != null) {
            review.setThesis(thesis);

            thesisReviewService.createThesisReview(review);

            model.addAttribute("diplomaReview", review);
            model.addAttribute("diplomaThesis", thesis);
            return "diploma-review/create-edit";
        }

        return "redirect:/diploma-assignments";
    }

    @GetMapping("/edit/{reviewId}")
    @PreAuthorize("hasAuthority('STUDENT')")
    public String getEditThesisReview(@PathVariable("reviewId") Long reviewId, Model model) {
        ThesisReview review = thesisReviewService.getById(reviewId);

        // Can update only if is already review
        if (review.getUploadTime() != null) {
            model.addAttribute("diplomaReview", review);
            model.addAttribute("diplomaThesis", review.getThesis());
            return "diploma-review/create-edit";
        }
        return "redirect:/diploma-assignments";
    }

    @PostMapping("/edit/{reviewId}")
    @PreAuthorize("hasAuthority('TEACHER')")
    public String editThesisReview(@PathVariable("reviewId") Long reviewId,
                                   @ModelAttribute("diplomaReview") ThesisReview review, Model model) {

        ThesisReview toUpdate = thesisReviewService.getById(reviewId);

        // Can update only if is already review
        if (toUpdate.getUploadTime() != null) {
            toUpdate.setText(review.getText());
            toUpdate.setConclusion(review.getConclusion());
            toUpdate.setReviewOutcome(review.getReviewOutcome());

            thesisReviewService.saveThesisReview(toUpdate);

            model.addAttribute("diplomaReview", toUpdate);
            model.addAttribute("diplomaThesis", toUpdate.getThesis());
            return "diploma-review/create-edit";
        }
        return "redirect:/diploma-assignments";
    }
}