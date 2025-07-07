package com.nbu.CSCB634.service;

import com.nbu.CSCB634.model.SchoolClass;
import com.nbu.CSCB634.model.Student;
import com.nbu.CSCB634.repository.SchoolClassRepository;
import com.nbu.CSCB634.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentClassEnrollmentService {

    private final StudentRepository studentRepository;
    private final SchoolClassRepository schoolClassRepository;

    /**
     * Записва ученик в клас
     */
    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'DIRECTOR')")
    @Transactional
    public Student enrollStudentInClass(Long studentId, Long classId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Ученикът не е намерен"));
        
        SchoolClass schoolClass = schoolClassRepository.findById(classId)
                .orElseThrow(() -> new IllegalArgumentException("Класът не е намерен"));
        
        // Проверка дали ученикът и класът са от същото училище
        if (!student.getSchool().getId().equals(schoolClass.getSchool().getId())) {
            throw new IllegalArgumentException("Ученикът и класът трябва да са от същото училище");
        }
        
        // Проверка дали ученикът вече е записан в този клас
        if (student.getSchoolClass() != null && student.getSchoolClass().getId().equals(classId)) {
            throw new IllegalArgumentException("Ученикът вече е записан в този клас");
        }
        
        student.setSchoolClass(schoolClass);
        return studentRepository.save(student);
    }

    /**
     * Отписва ученик от клас
     */
    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'DIRECTOR')")
    @Transactional
    public Student withdrawStudentFromClass(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Ученикът не е намерен"));
        
        if (student.getSchoolClass() == null) {
            throw new IllegalArgumentException("Ученикът не е записан в никой клас");
        }
        
        student.setSchoolClass(null);
        return studentRepository.save(student);
    }

    /**
     * Прехвърля ученик от един клас в друг
     */
    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'DIRECTOR')")
    @Transactional
    public Student transferStudentToClass(Long studentId, Long newClassId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Ученикът не е намерен"));
        
        SchoolClass newSchoolClass = schoolClassRepository.findById(newClassId)
                .orElseThrow(() -> new IllegalArgumentException("Новият клас не е намерен"));
        
        // Проверка дали ученикът и новият клас са от същото училище
        if (!student.getSchool().getId().equals(newSchoolClass.getSchool().getId())) {
            throw new IllegalArgumentException("Ученикът и новият клас трябва да са от същото училище");
        }
        
        SchoolClass oldClass = student.getSchoolClass();
        student.setSchoolClass(newSchoolClass);
        
        Student savedStudent = studentRepository.save(student);
        
        return savedStudent;
    }

    /**
     * Взема всички ученици в даден клас
     */
    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'DIRECTOR', 'TEACHER')")
    public List<Student> getStudentsInClass(Long classId) {
        SchoolClass schoolClass = schoolClassRepository.findById(classId)
                .orElseThrow(() -> new IllegalArgumentException("Класът не е намерен"));
        
        return studentRepository.findAll().stream()
                .filter(student -> student.getSchoolClass() != null && 
                         student.getSchoolClass().getId().equals(classId))
                .collect(Collectors.toList());
    }

    /**
     * Взема всички ученици без клас в дадено училище
     */
    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'DIRECTOR')")
    public List<Student> getStudentsWithoutClass(Long schoolId) {
        return studentRepository.findAll().stream()
                .filter(student -> student.getSchool().getId().equals(schoolId) && 
                         student.getSchoolClass() == null)
                .collect(Collectors.toList());
    }

    /**
     * Взема всички класове в дадено училище
     */
    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'DIRECTOR', 'TEACHER')")
    public List<SchoolClass> getClassesInSchool(Long schoolId) {
        return schoolClassRepository.findAll().stream()
                .filter(schoolClass -> schoolClass.getSchool().getId().equals(schoolId))
                .collect(Collectors.toList());
    }

    /**
     * Проверява дали ученик може да бъде записан в клас
     */
    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'DIRECTOR')")
    public boolean canEnrollStudentInClass(Long studentId, Long classId) {
        try {
            Student student = studentRepository.findById(studentId)
                    .orElseThrow(() -> new IllegalArgumentException("Ученикът не е намерен"));
            
            SchoolClass schoolClass = schoolClassRepository.findById(classId)
                    .orElseThrow(() -> new IllegalArgumentException("Класът не е намерен"));
            
            // Проверка дали са от същото училище
            if (!student.getSchool().getId().equals(schoolClass.getSchool().getId())) {
                return false;
            }
            
            // Проверка дали ученикът вече не е в този клас
            return student.getSchoolClass() == null || 
                   !student.getSchoolClass().getId().equals(classId);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Взема броя ученици в даден клас
     */
    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'DIRECTOR', 'TEACHER')")
    public long getStudentCountInClass(Long classId) {
        return getStudentsInClass(classId).size();
    }
} 