package com.nbu.CSCB634.service;

import com.nbu.CSCB634.model.School;
import com.nbu.CSCB634.repository.SchoolRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SchoolService {

    private final SchoolRepository schoolRepository;

    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'DIRECTOR')")
    public School createSchool(@Valid School school) {
        return schoolRepository.save(school);
    }

    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'DIRECTOR', 'TEACHER', 'PARENT', 'STUDENT')")
    public Optional<School> getSchoolById(Long id) {
        return schoolRepository.findById(id);
    }

    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'DIRECTOR', 'TEACHER', 'PARENT', 'STUDENT')")
    public List<School> getAllSchools() {
        return schoolRepository.findAll();
    }

    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'DIRECTOR')")
    public School updateSchool(Long id, @Valid School schoolDetails) {
        return schoolRepository.findById(id)
                .map(school -> {
                    school.setName(schoolDetails.getName());
                    school.setAddress(schoolDetails.getAddress());
                    return schoolRepository.save(school);
                }).orElseThrow(() -> new IllegalArgumentException("School not found"));
    }

    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public void deleteSchool(Long id) {
        School school = schoolRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("School not found"));
        schoolRepository.delete(school);
    }
}