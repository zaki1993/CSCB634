package com.nbu.CSCB634.service;

import com.nbu.CSCB634.config.DatabaseSequenceSync;
import com.nbu.CSCB634.model.Absence;
import com.nbu.CSCB634.model.Parent;
import com.nbu.CSCB634.model.Student;
import com.nbu.CSCB634.model.Teacher;
import com.nbu.CSCB634.repository.AbsenceRepository;
import com.nbu.CSCB634.repository.ParentRepository;
import com.nbu.CSCB634.repository.StudentRepository;
import com.nbu.CSCB634.repository.TeacherRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AbsenceService {

    private final AbsenceRepository absenceRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final ParentRepository parentRepository;
    private final DatabaseSequenceSync sequenceSync;

    @PreAuthorize("hasRole('TEACHER')")
    public Absence createAbsence(@Valid Absence absence) {
        try {
            // Първи опит - нормално запазване с auto-increment
            return absenceRepository.save(absence);
        } catch (Exception e) {
            // Ако има проблем с sequence, синхронизираме го и опитваме отново
            if (e.getMessage().contains("duplicate key") || e.getMessage().contains("already exists")) {
                log.warn("Открит конфликт с absence ID, синхронизираме sequence и опитваме отново");
                
                try {
                    // Синхронизираме absence sequence с текущите данни
                    sequenceSync.syncAbsenceSequence();
                    
                    // Създаваме нов обект без ID (за да се генерира автоматично)
                    Absence newAbsence = Absence.builder()
                            .absenceDate(absence.getAbsenceDate())
                            .justified(absence.getJustified())
                            .student(absence.getStudent())
                            .teacher(absence.getTeacher())
                            .subject(absence.getSubject())
                            .build();
                    
                    // Втори опит със синхронизиран sequence
                    return absenceRepository.save(newAbsence);
                    
                } catch (Exception retryException) {
                    log.error("Неуспешен втори опит за създаване на отсъствие: {}", retryException.getMessage());
                    // Ако и втория опит не успее, хвърляме оригиналната грешка
                    throw new RuntimeException("Неуспешно създаване на отсъствие: " + e.getMessage(), e);
                }
            }
            // Ако не е sequence проблем, хвърляме оригиналната грешка
            throw e;
        }
    }

    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'DIRECTOR', 'TEACHER', 'PARENT', 'STUDENT')")
    public Optional<Absence> getAbsenceById(Long id) {
        return absenceRepository.findById(id);
    }

    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'DIRECTOR', 'TEACHER', 'PARENT')")
    public List<Absence> getAbsencesByStudentId(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found"));
        return absenceRepository.findByStudent(student);
    }

    @PreAuthorize("hasRole('TEACHER')")
    public List<Absence> getAbsencesByTeacherId(Long teacherId) {
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new IllegalArgumentException("Teacher not found"));
        return absenceRepository.findByTeacher(teacher);
    }

    @PreAuthorize("hasRole('TEACHER')")
    public Absence updateAbsence(Long id, @Valid Absence updatedAbsence) {
        return absenceRepository.findById(id)
                .map(absence -> {
                    absence.setAbsenceDate(updatedAbsence.getAbsenceDate());
                    absence.setJustified(updatedAbsence.getJustified());
                    absence.setSubject(updatedAbsence.getSubject());
                    return absenceRepository.save(absence);
                })
                .orElseThrow(() -> new IllegalArgumentException("Absence not found"));
    }

    @PreAuthorize("hasRole('TEACHER')")
    public void deleteAbsence(Long id) {
        Absence absence = absenceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Absence not found"));
        absenceRepository.delete(absence);
    }

    public List<Absence> getAll() {
        return absenceRepository.findAll();
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

    // Get absences for all children of a parent
    @PreAuthorize("hasRole('PARENT')")
    public List<Absence> getAbsencesByParentId(Long parentId) {
        Parent parent = parentRepository.findById(parentId)
                .orElseThrow(() -> new IllegalArgumentException("Parent not found"));
        
        if (parent.getStudents() == null || parent.getStudents().isEmpty()) {
            return List.of(); // Return empty list if parent has no children
        }
        
        return parent.getStudents().stream()
                .flatMap(student -> absenceRepository.findByStudent(student).stream())
                .collect(Collectors.toList());
    }

    // Get absences for a specific child of a parent
    @PreAuthorize("hasRole('PARENT')")
    public List<Absence> getAbsencesByParentAndStudent(Long parentId, Long studentId) {
        Parent parent = parentRepository.findById(parentId)
                .orElseThrow(() -> new IllegalArgumentException("Parent not found"));
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found"));
        
        // Check if this student belongs to this parent
        if (parent.getStudents() == null || !parent.getStudents().contains(student)) {
            throw new IllegalArgumentException("Student does not belong to this parent");
        }
        
        return absenceRepository.findByStudent(student);
    }

    // Check if parent can view absences of a specific student
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
     * Получава следващото налично ID за отсъствие (backup метод)
     */
    public Long getNextAvailableId() {
        List<Absence> allAbsences = absenceRepository.findAll();
        Long maxId = allAbsences.stream()
                .mapToLong(Absence::getId)
                .max()
                .orElse(0L);
        return maxId + 1;
    }
} 