package com.nbu.CSCB634.service;

import com.nbu.CSCB634.model.Student;
import com.nbu.CSCB634.model.Teacher;
import com.nbu.CSCB634.repository.StudentRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;

    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'DIRECTOR')")
    public Student createStudent(@Valid Student student) {
        return studentRepository.save(student);
    }

    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'DIRECTOR', 'TEACHER', 'PARENT', 'STUDENT')")
    public Optional<Student> getStudentById(Long id) {
        return studentRepository.findById(id);
    }

  @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'DIRECTOR')")
  public List<Student> getStudentsBySchoolId(Long schoolId) {
    return studentRepository.findAll().stream()
                            .filter(teacher -> teacher.getSchool() != null && teacher.getSchool().getId().equals(schoolId))
                            .collect(Collectors.toList());
  }

    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'DIRECTOR', 'TEACHER')")
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'DIRECTOR')")
    public Student updateStudent(Long id, @Valid Student updatedStudent) {
        return studentRepository.findById(id)
                .map(student -> {
                    student.getUser().setFirstName(updatedStudent.getUser().getFirstName());
                    student.getUser().setLastName(updatedStudent.getUser().getLastName());
                    student.setSchool(updatedStudent.getSchool());
                    student.setSchoolClass(updatedStudent.getSchoolClass());
                    return studentRepository.save(student);
                })
                .orElseThrow(() -> new IllegalArgumentException("Student not found"));
    }

    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public void deleteStudent(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Student not found"));
        studentRepository.delete(student);
    }
}