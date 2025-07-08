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

    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'DIRECTOR', 'TEACHER', 'PARENT', 'STUDENT')")
    public Optional<Student> getStudentByUserId(Long userId) {
        return studentRepository.findAll().stream()
                .filter(student -> student.getUser() != null && student.getUser().getId().equals(userId))
                .findFirst();
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

    // Специален метод за обновяване на собствения профил от ученик
    @PreAuthorize("hasRole('STUDENT')")
    public Student updateOwnProfile(Long userId, String firstName, String lastName, String email) {
        Optional<Student> studentOpt = getStudentByUserId(userId);
        if (studentOpt.isEmpty()) {
            throw new IllegalArgumentException("Student not found");
        }
        
        Student student = studentOpt.get();
        student.getUser().setFirstName(firstName);
        student.getUser().setLastName(lastName);
        student.getUser().setEmail(email);
        
        return studentRepository.save(student);
    }

    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public void deleteStudent(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Student not found"));
        // Каскадното изтриване ще се случи автоматично чрез JPA
        studentRepository.delete(student);
    }

    public Long getNextAvailableId() {
        List<Student> allStudents = studentRepository.findAll();
        Long maxId = allStudents.stream()
                .mapToLong(Student::getId)
                .max()
                .orElse(0L);
        return maxId + 1;
    }
}