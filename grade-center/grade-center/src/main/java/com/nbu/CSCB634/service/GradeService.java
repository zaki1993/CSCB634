package com.nbu.CSCB634.service;

import com.nbu.CSCB634.config.DatabaseSequenceSync;
import com.nbu.CSCB634.model.Grade;
import com.nbu.CSCB634.model.Parent;
import com.nbu.CSCB634.model.Student;
import com.nbu.CSCB634.model.Teacher;
import com.nbu.CSCB634.repository.GradeRepository;
import com.nbu.CSCB634.repository.ParentRepository;
import com.nbu.CSCB634.repository.StudentRepository;
import com.nbu.CSCB634.repository.TeacherRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class GradeService {

    private final GradeRepository gradeRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final ParentRepository parentRepository;
    private final DatabaseSequenceSync sequenceSync;

    @PreAuthorize("hasRole('TEACHER')")
    public Grade createGrade(@Valid Grade grade) {
        try {
            // Първи опит - нормално запазване с auto-increment
            return gradeRepository.save(grade);
        } catch (Exception e) {
            // Ако има проблем с sequence, синхронизираме го и опитваме отново
            if (e.getMessage().contains("duplicate key") || e.getMessage().contains("already exists")) {
                log.warn("Открит конфликт с grade ID, синхронизираме sequence и опитваме отново");
                
                try {
                    // Синхронизираме grade sequence с текущите данни
                    sequenceSync.syncGradeSequence();
                    
                    // Създаваме нова оценка без ID (за да се генерира автоматично)
                    Grade newGrade = Grade.builder()
                            .value(grade.getValue())
                            .dateAwarded(grade.getDateAwarded())
                            .student(grade.getStudent())
                            .teacher(grade.getTeacher())
                            .subject(grade.getSubject())
                            .build();
                    
                    // Втори опит със синхронизиран sequence
                    return gradeRepository.save(newGrade);
                    
                } catch (Exception retryException) {
                    log.error("Неуспешен втори опит за създаване на оценка: {}", retryException.getMessage());
                    // Ако и втория опит не успее, хвърляме оригиналната грешка
                    throw new RuntimeException("Неуспешно създаване на оценка: " + e.getMessage(), e);
                }
            }
            // Ако не е sequence проблем, хвърляме оригиналната грешка
            throw e;
        }
    }

    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'DIRECTOR', 'TEACHER', 'PARENT', 'STUDENT')")
    public Optional<Grade> getGradeById(Long id) {
        return gradeRepository.findById(id);
    }

    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'DIRECTOR', 'TEACHER', 'PARENT')")
    public List<Grade> getGradesByStudentId(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found"));
        return gradeRepository.findByStudent(student);
    }

    @PreAuthorize("hasRole('TEACHER')")
    public Grade updateGrade(Long id, @Valid Grade updatedGrade) {
        return gradeRepository.findById(id)
                .map(grade -> {
                    grade.setValue(updatedGrade.getValue());
                    grade.setDateAwarded(updatedGrade.getDateAwarded());
                    grade.setSubject(updatedGrade.getSubject());
                    // teacher/student usually not updated here
                    return gradeRepository.save(grade);
                })
                .orElseThrow(() -> new IllegalArgumentException("Grade not found"));
    }

    @PreAuthorize("hasRole('TEACHER')")
    public void deleteGrade(Long id) {
        Grade grade = gradeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Grade not found"));
        gradeRepository.delete(grade);
    }

    public List<Grade> getAll() {
        return gradeRepository.findAll();
    }

    @PreAuthorize("hasRole('TEACHER')")
    public List<Grade> getGradesByTeacherId(Long teacherId) {
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new IllegalArgumentException("Teacher not found"));
        return gradeRepository.findByTeacher(teacher);
    }

    // Get students that a teacher can manage (same school)
    @PreAuthorize("hasRole('TEACHER')")
    public List<Student> getStudentsForTeacher(Long teacherId) {
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new IllegalArgumentException("Teacher not found"));
        return studentRepository.findBySchoolId(teacher.getSchool().getId());
    }

    // Check if teacher can manage a specific student
    @PreAuthorize("hasRole('TEACHER')")
    public boolean canTeacherManageStudent(Long teacherId, Long studentId) {
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new IllegalArgumentException("Teacher not found"));
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found"));
        
        // Teacher can manage students from the same school
        return teacher.getSchool().getId().equals(student.getSchool().getId());
    }

    // Get grades for all children of a parent
    @PreAuthorize("hasRole('PARENT')")
    public List<Grade> getGradesByParentId(Long parentId) {
        Parent parent = parentRepository.findById(parentId)
                .orElseThrow(() -> new IllegalArgumentException("Parent not found"));
        
        if (parent.getStudents() == null || parent.getStudents().isEmpty()) {
            return List.of(); // Return empty list if parent has no children
        }
        
        return parent.getStudents().stream()
                .flatMap(student -> gradeRepository.findByStudent(student).stream())
                .collect(Collectors.toList());
    }

    // Get grades for a specific child of a parent
    @PreAuthorize("hasRole('PARENT')")
    public List<Grade> getGradesByParentAndStudent(Long parentId, Long studentId) {
        Parent parent = parentRepository.findById(parentId)
                .orElseThrow(() -> new IllegalArgumentException("Parent not found"));
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found"));
        
        // Check if this student belongs to this parent
        if (parent.getStudents() == null || !parent.getStudents().contains(student)) {
            throw new IllegalArgumentException("Student does not belong to this parent");
        }
        
        return gradeRepository.findByStudent(student);
    }

    // Check if parent can view grades of a specific student
    @PreAuthorize("hasRole('PARENT')")
    public boolean canParentViewStudent(Long parentId, Long studentId) {
        Parent parent = parentRepository.findById(parentId)
                .orElseThrow(() -> new IllegalArgumentException("Parent not found"));
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found"));
        
        return parent.getStudents() != null && parent.getStudents().contains(student);
    }

    // Get students for a parent
    @PreAuthorize("hasRole('PARENT')")
    public List<Student> getStudentsForParent(Long parentId) {
        Parent parent = parentRepository.findById(parentId)
                .orElseThrow(() -> new IllegalArgumentException("Parent not found"));
        
        return parent.getStudents() != null ? 
                parent.getStudents().stream().collect(Collectors.toList()) : 
                List.of();
    }

    /**
     * Получава следващото налично ID за оценка (backup метод)
     */
    public Long getNextAvailableId() {
        List<Grade> allGrades = gradeRepository.findAll();
        Long maxId = allGrades.stream()
                .mapToLong(Grade::getId)
                .max()
                .orElse(0L);
        return maxId + 1;
    }
}