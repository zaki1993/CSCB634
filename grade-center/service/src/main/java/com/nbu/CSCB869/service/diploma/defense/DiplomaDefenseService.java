package com.nbu.CSCB869.service.diploma.defense;

import com.nbu.CSCB869.model.diploma.defense.DiplomaDefense;
import com.nbu.CSCB869.model.diploma.thesis.DiplomaThesis;
import com.nbu.CSCB869.model.diploma.thesis.review.ThesisReview;
import com.nbu.CSCB869.repository.diploma.defense.DiplomaDefenseRepository;
import com.nbu.CSCB869.repository.diploma.thesis.DiplomaThesisRepository;
import com.nbu.CSCB869.repository.diploma.thesis.review.ThesisReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DiplomaDefenseService {

    private final DiplomaDefenseRepository diplomaDefenseRepository;
    private final DiplomaThesisRepository diplomaThesisRepository;
    private final ThesisReviewRepository thesisReviewRepository;

    @Autowired
    public DiplomaDefenseService(DiplomaDefenseRepository diplomaDefenseRepository, DiplomaThesisRepository diplomaThesisRepository, ThesisReviewRepository thesisReviewRepository) {
        this.diplomaDefenseRepository = diplomaDefenseRepository;
        this.diplomaThesisRepository = diplomaThesisRepository;
        this.thesisReviewRepository = thesisReviewRepository;
    }

    public List<DiplomaDefense> getAllDefenses() {
        return diplomaDefenseRepository.findAll();
    }

    public DiplomaDefense getDefenseById(Long id) {
        return diplomaDefenseRepository.findById(id).orElse(null);
    }

    public void saveDefense(DiplomaDefense diplomaDefense) {
        diplomaDefenseRepository.save(diplomaDefense);
    }

    public void deleteDefense(Long id) {
        // Remove the relationship from ThesisReview
        thesisReviewRepository.removeDiplomaDefenseReferences(id);

        // Delete the DiplomaDefense
        diplomaDefenseRepository.deleteById(id);
    }

    public DiplomaDefense scheduleDefense(DiplomaDefense def) {
        // Save the defense to the database first to obtain its ID
        DiplomaDefense savedDefense = diplomaDefenseRepository.save(def);

        // Iterate over each thesis ID associated with this defense
        for (Long thesisId : def.getTheses()) {
            // Fetch the diploma thesis by ID
            DiplomaThesis thesis = diplomaThesisRepository.findById(thesisId)
                    .orElseThrow(() -> new IllegalArgumentException("Thesis not found with ID: " + thesisId));

            // Get the thesis review associated with this thesis
            ThesisReview review = thesis.getReview();
            if (review != null) {
                // Update the defense field in the review with the saved defense ID
                review.setDefense(savedDefense);
                thesisReviewRepository.saveAndFlush(review);
            }
        }

        return savedDefense;
    }

    public DiplomaDefense editDiplomaDefense(Long defenseId, DiplomaDefense updatedDefense) {
        // Fetch the existing defense
        DiplomaDefense existingDefense = diplomaDefenseRepository.findById(defenseId)
                .orElseThrow(() -> new IllegalArgumentException("Defense not found with ID: " + defenseId));

        // Update basic defense details
        existingDefense.setDefenseDate(updatedDefense.getDefenseDate());
        existingDefense.setLocation(updatedDefense.getLocation());

        // Handle theses updates
        List<Long> newThesesIds = updatedDefense.getTheses();
        List<Long> oldThesesIds = existingDefense.getTheses();

        // Remove old theses not in the new list
        for (Long oldThesisId : oldThesesIds) {
            if (!newThesesIds.contains(oldThesisId)) {
                DiplomaThesis oldThesis = diplomaThesisRepository.findById(oldThesisId)
                        .orElseThrow(() -> new IllegalArgumentException("Thesis not found with ID: " + oldThesisId));
                ThesisReview review = oldThesis.getReview();
                if (review != null) {
                    // Remove the association with the defense
                    review.setDefense(null);
                    thesisReviewRepository.saveAndFlush(review);
                }
            }
        }

        // Add or update new theses
        for (Long newThesisId : newThesesIds) {
            DiplomaThesis newThesis = diplomaThesisRepository.findById(newThesisId)
                    .orElseThrow(() -> new IllegalArgumentException("Thesis not found with ID: " + newThesisId));
            ThesisReview review = newThesis.getReview();
            if (review != null) {
                // Update the defense association
                review.setDefense(existingDefense);
                thesisReviewRepository.saveAndFlush(review);
            }
        }

        // Update theses list in the defense
        existingDefense.setTheses(newThesesIds);

        // Save the updated defense
        return diplomaDefenseRepository.save(existingDefense);
    }

    public Double getAverageStudentsPerDefenseInPeriod(LocalDateTime startDate, LocalDateTime endDate) {
        return diplomaDefenseRepository.findAverageStudentsPerDefenseInPeriod(startDate, endDate);
    }

    public double getThesisGradle(long thesisId) {
        return diplomaDefenseRepository.findGradeByThesisId(thesisId);
    }

    public boolean isThesisDefended(long thesisId) {

        // grade exist for previous defenses
        boolean gradeExist = diplomaDefenseRepository.gradeExistForThesis(thesisId);
        // if the grade is >= 3 then it is defended
        if (gradeExist) {
            return diplomaDefenseRepository.isGradePositive(thesisId) || diplomaDefenseRepository.isNotGraded(thesisId);
        }

        // if the thesis is still under another defend but not yet graded, then it is still defending
        return diplomaDefenseRepository.isThesisInDefense(thesisId);
    }
}
