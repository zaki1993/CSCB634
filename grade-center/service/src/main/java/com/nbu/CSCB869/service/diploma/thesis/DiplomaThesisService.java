package com.nbu.CSCB869.service.diploma.thesis;


import com.nbu.CSCB869.model.diploma.thesis.DiplomaThesis;
import com.nbu.CSCB869.repository.diploma.thesis.DiplomaThesisRepository;
import com.nbu.CSCB869.service.exceptions.DiplomaAssignmentNotFoundException;
import com.nbu.CSCB869.service.exceptions.DiplomaThesisNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DiplomaThesisService {
    private final DiplomaThesisRepository diplomaThesisRepository;

    public void createDiplomaThesis(DiplomaThesis diplomaThesis) {
        // Set when the diploma thesis was created
        diplomaThesis.setUploadTime(LocalDateTime.now());

        diplomaThesisRepository.save(diplomaThesis);
    }

    public DiplomaThesis getThesisById(Long id) {
        return diplomaThesisRepository.findById(id).orElseThrow(() -> new DiplomaThesisNotFoundException(id));
    }

    public void saveThesis(DiplomaThesis toUpdate) {
        diplomaThesisRepository.save(toUpdate);
    }

    public List<DiplomaThesis> getAllTheses() {
        return diplomaThesisRepository.findAll();
    }

    public List<DiplomaThesis> getThesesByIds(List<Long> thesisIds) {
        return diplomaThesisRepository.findAllById(thesisIds);
    }

    public List<DiplomaThesis> getThesesWithGradesInRange(double minGrade, double maxGrade) {
        return diplomaThesisRepository.findThesesWithGradesInRange(minGrade, maxGrade);
    }
}

