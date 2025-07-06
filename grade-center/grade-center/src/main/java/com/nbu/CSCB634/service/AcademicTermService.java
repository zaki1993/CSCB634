package com.nbu.CSCB634.service;

import com.nbu.CSCB634.model.AcademicTerm;
import com.nbu.CSCB634.repository.AcademicTermRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AcademicTermService {

    private final AcademicTermRepository academicTermRepository;


    public AcademicTerm createAcademicTerm(@Valid AcademicTerm AcademicTerm) {
        return academicTermRepository.save(AcademicTerm);
    }

    public Optional<AcademicTerm> getAcademicTermById(Long id) {
        return academicTermRepository.findById(id);
    }

    public List<AcademicTerm> getAllAcademicTerms() {
        return academicTermRepository.findAll();
    }

    public AcademicTerm updateAcademicTerm(Long id, @Valid AcademicTerm academicTermDetails) {
        return academicTermRepository.findById(id)
                .map(academicTerm -> {
                    academicTerm.setName(academicTermDetails.getName());
                    academicTerm.setStartDate(academicTermDetails.getStartDate());
                    academicTerm.setEndDate(academicTermDetails.getEndDate());
                    academicTerm.setSchool(academicTermDetails.getSchool());
                    academicTerm.setSubjects(academicTermDetails.getSubjects());
                    academicTerm.setTeachers(academicTermDetails.getTeachers());
                    return academicTermRepository.save(academicTerm);
                }).orElseThrow(() -> new IllegalArgumentException("AcademicTerm not found"));
    }

    public void deleteAcademicTerm(Long id) {
        AcademicTerm AcademicTerm = academicTermRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("AcademicTerm not found"));
        academicTermRepository.delete(AcademicTerm);
    }

    public List<AcademicTerm> getAll() {
        return academicTermRepository.findAll();
    }
}
