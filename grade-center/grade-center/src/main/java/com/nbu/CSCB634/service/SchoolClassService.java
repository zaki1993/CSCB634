package com.nbu.CSCB634.service;

import com.nbu.CSCB634.model.SchoolClass;
import com.nbu.CSCB634.repository.SchoolClassRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SchoolClassService {

    private final SchoolClassRepository schoolClassRepository;

    public SchoolClass createSchoolClass(@Valid SchoolClass schoolClassService) {
        return schoolClassRepository.save(schoolClassService);
    }

    public Optional<SchoolClass> getSchoolClassById(Long id) {
        return schoolClassRepository.findById(id);
    }

    public List<SchoolClass> getAllSchoolClasses() {
        return schoolClassRepository.findAll();
    }

    public SchoolClass updateSchoolClass(Long id, @Valid SchoolClass schoolClassDetails) {
        return schoolClassRepository.findById(id)
                .map(schoolClass -> {
                    schoolClass.setName(schoolClassDetails.getName());
                    schoolClass.setGradeNumber(schoolClassDetails.getGradeNumber());
                    schoolClass.setSchool(schoolClassDetails.getSchool());
                    return schoolClassRepository.save(schoolClass);
                }).orElseThrow(() -> new IllegalArgumentException("SchoolClassService not found"));
    }

    public void deleteSchoolClass(Long id) {
        SchoolClass schoolClass = schoolClassRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("SchoolClassService not found"));
        schoolClassRepository.delete(schoolClass);
    }

    public List<SchoolClass> getAll() {
        return schoolClassRepository.findAll();
    }
}