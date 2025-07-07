package com.nbu.CSCB634.service;

import com.nbu.CSCB634.model.*;
import com.nbu.CSCB634.model.auth.User;
import com.nbu.CSCB634.repository.*;
import com.nbu.CSCB634.repository.auth.UserRepository;
import com.nbu.CSCB634.service.auth.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoleChangeService {

    private final UserRepository userRepository;
    private final AdministratorRepository administratorRepository;
    private final DirectorRepository directorRepository;
    private final TeacherRepository teacherRepository;
    private final ParentRepository parentRepository;
    private final StudentRepository studentRepository;
    private final SchoolRepository schoolRepository;
    private final SchoolService schoolService;

    /**
     * Сменя ролята на потребител с прехвърляне на данни между таблиците
     */
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    @Transactional
    public void changeUserRole(Long userId, Role newRole, RoleChangeRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Потребителят не е намерен"));

        Role currentRole = user.getRole();
        
        if (currentRole == newRole) {
            throw new IllegalArgumentException("Потребителят вече има тази роля");
        }

        log.info("Сменяме роля на потребител {} от {} на {}", user.getUsername(), currentRole, newRole);

        // Първо изтриваме от старата таблица
        deleteFromCurrentRoleTable(user, currentRole);

        // Създаваме запис в новата таблица
        createInNewRoleTable(user, newRole, request);

        // Обновяваме role полето в users таблицата
        user.setRole(newRole);
        userRepository.save(user);

        log.info("Успешно сменихме ролята на потребител {}", user.getUsername());
    }

    /**
     * Изтрива записа от текущата роля таблица
     */
    private void deleteFromCurrentRoleTable(User user, Role currentRole) {
        switch (currentRole) {
            case ADMINISTRATOR:
                // Администраторите не се изтриват от отделна таблица
                break;
            case DIRECTOR:
                directorRepository.deleteById(user.getId());
                break;
            case TEACHER:
                teacherRepository.deleteById(user.getId());
                break;
            case PARENT:
                parentRepository.deleteById(user.getId());
                break;
            case STUDENT:
                studentRepository.deleteById(user.getId());
                break;
            case NONE:
                // Няма запис за изтриване
                break;
        }
    }

    /**
     * Създава запис в новата роля таблица
     */
    private void createInNewRoleTable(User user, Role newRole, RoleChangeRequest request) {
        switch (newRole) {
            case ADMINISTRATOR:
                Administrator admin = Administrator.builder()
                        .id(user.getId())
                        .user(user)
                        .build();
                administratorRepository.save(admin);
                break;
            case DIRECTOR:
                if (request.getSchoolId() == null) {
                    throw new IllegalArgumentException("Училището е задължително за роля Директор");
                }
                School school = schoolService.getSchoolById(request.getSchoolId())
                        .orElseThrow(() -> new IllegalArgumentException("Училището не е намерено"));
                
                Director director = Director.builder()
                        .id(user.getId())
                        .user(user)
                        .school(school)
                        .build();
                directorRepository.save(director);
                break;
            case TEACHER:
                if (request.getSchoolId() == null) {
                    throw new IllegalArgumentException("Училището е задължително за роля Учител");
                }
                School teacherSchool = schoolService.getSchoolById(request.getSchoolId())
                        .orElseThrow(() -> new IllegalArgumentException("Училището не е намерено"));
                
                Teacher teacher = Teacher.builder()
                        .id(user.getId())
                        .user(user)
                        .school(teacherSchool)
                        .qualifiedSubjects(new HashSet<>()) // Празен набор първоначално
                        .build();
                teacherRepository.save(teacher);
                break;
            case PARENT:
                Parent parent = Parent.builder()
                        .id(user.getId())
                        .user(user)
                        .students(new HashSet<>()) // Празен набор първоначално
                        .build();
                parentRepository.save(parent);
                break;
            case STUDENT:
                if (request.getSchoolId() == null) {
                    throw new IllegalArgumentException("Училището е задължително за роля Ученик");
                }
                School studentSchool = schoolService.getSchoolById(request.getSchoolId())
                        .orElseThrow(() -> new IllegalArgumentException("Училището не е намерено"));
                
                Student student = Student.builder()
                        .id(user.getId())
                        .user(user)
                        .school(studentSchool)
                        .schoolClass(null) // Клас може да се зададе по-късно
                        .build();
                studentRepository.save(student);
                break;
            case NONE:
                // Няма запис за създаване
                break;
        }
    }

    /**
     * Проверява дали смяната на роля е възможна
     */
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public RoleChangeValidation validateRoleChange(Long userId, Role newRole, RoleChangeRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Потребителят не е намерен"));

        Role currentRole = user.getRole();
        RoleChangeValidation validation = new RoleChangeValidation();

        if (currentRole == newRole) {
            validation.setValid(false);
            validation.addError("Потребителят вече има тази роля");
            return validation;
        }

        // Проверка за задължителни полета
        switch (newRole) {
            case DIRECTOR:
            case TEACHER:
            case STUDENT:
                if (request.getSchoolId() == null) {
                    validation.setValid(false);
                    validation.addError("Училището е задължително за роля " + newRole);
                }
                break;
        }

        // Проверка за данни, които ще се загубят
        switch (currentRole) {
            case TEACHER:
                Optional<Teacher> teacher = teacherRepository.findById(userId);
                if (teacher.isPresent()) {
                    if (!teacher.get().getGrades().isEmpty()) {
                        validation.addWarning("Учителят има " + teacher.get().getGrades().size() + " оценки, които ще бъдат изтрити");
                    }
                    if (!teacher.get().getAbsences().isEmpty()) {
                        validation.addWarning("Учителят има " + teacher.get().getAbsences().size() + " отсъствия, които ще бъдат изтрити");
                    }
                    if (!teacher.get().getQualifiedSubjects().isEmpty()) {
                        validation.addWarning("Учителят има " + teacher.get().getQualifiedSubjects().size() + " предмета, които ще бъдат изтрити");
                    }
                }
                break;
            case STUDENT:
                Optional<Student> student = studentRepository.findById(userId);
                if (student.isPresent()) {
                    if (!student.get().getGrades().isEmpty()) {
                        validation.addWarning("Ученикът има " + student.get().getGrades().size() + " оценки, които ще бъдат изтрити");
                    }
                    if (!student.get().getAbsences().isEmpty()) {
                        validation.addWarning("Ученикът има " + student.get().getAbsences().size() + " отсъствия, които ще бъдат изтрити");
                    }
                    if (!student.get().getParents().isEmpty()) {
                        validation.addWarning("Ученикът има " + student.get().getParents().size() + " родители, които ще загубят връзката");
                    }
                    if (student.get().getSchoolClass() != null) {
                        validation.addWarning("Ученикът е в клас " + student.get().getSchoolClass().getName() + ", който ще бъде изтрит");
                    }
                }
                break;
            case PARENT:
                Optional<Parent> parent = parentRepository.findById(userId);
                if (parent.isPresent()) {
                    if (!parent.get().getStudents().isEmpty()) {
                        validation.addWarning("Родителят има " + parent.get().getStudents().size() + " деца, които ще загубят връзката");
                    }
                }
                break;
            case DIRECTOR:
                Optional<Director> director = directorRepository.findById(userId);
                if (director.isPresent()) {
                    validation.addWarning("Директорът е свързан с училище " + director.get().getSchool().getName());
                }
                break;
        }

        return validation;
    }

    /**
     * Взема информация за текущата роля на потребител
     */
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public RoleInfo getCurrentRoleInfo(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Потребителят не е намерен"));

        RoleInfo info = new RoleInfo();
        info.setUserId(userId);
        info.setCurrentRole(user.getRole());
        info.setUsername(user.getUsername());
        info.setFullName(user.getFirstName() + " " + user.getLastName());

        switch (user.getRole()) {
            case DIRECTOR:
                directorRepository.findById(userId).ifPresent(director -> {
                    info.setSchoolId(director.getSchool().getId());
                    info.setSchoolName(director.getSchool().getName());
                });
                break;
            case TEACHER:
                teacherRepository.findById(userId).ifPresent(teacher -> {
                    info.setSchoolId(teacher.getSchool().getId());
                    info.setSchoolName(teacher.getSchool().getName());
                    info.setSubjectCount(teacher.getQualifiedSubjects().size());
                    info.setGradeCount(teacher.getGrades().size());
                    info.setAbsenceCount(teacher.getAbsences().size());
                });
                break;
            case STUDENT:
                studentRepository.findById(userId).ifPresent(student -> {
                    info.setSchoolId(student.getSchool().getId());
                    info.setSchoolName(student.getSchool().getName());
                    if (student.getSchoolClass() != null) {
                        info.setClassName(student.getSchoolClass().getName());
                    }
                    info.setParentCount(student.getParents().size());
                    info.setGradeCount(student.getGrades().size());
                    info.setAbsenceCount(student.getAbsences().size());
                });
                break;
            case PARENT:
                parentRepository.findById(userId).ifPresent(parent -> {
                    info.setStudentCount(parent.getStudents().size());
                });
                break;
        }

        return info;
    }

    /**
     * Класове за заявки и отговори
     */
    public static class RoleChangeRequest {
        private Long schoolId;
        private Long classId;
        
        // Getters and setters
        public Long getSchoolId() { return schoolId; }
        public void setSchoolId(Long schoolId) { this.schoolId = schoolId; }
        public Long getClassId() { return classId; }
        public void setClassId(Long classId) { this.classId = classId; }
    }

    public static class RoleChangeValidation {
        private boolean valid = true;
        private java.util.List<String> errors = new java.util.ArrayList<>();
        private java.util.List<String> warnings = new java.util.ArrayList<>();

        public boolean isValid() { return valid; }
        public void setValid(boolean valid) { this.valid = valid; }
        public java.util.List<String> getErrors() { return errors; }
        public java.util.List<String> getWarnings() { return warnings; }
        public void addError(String error) { errors.add(error); valid = false; }
        public void addWarning(String warning) { warnings.add(warning); }
    }

    public static class RoleInfo {
        private Long userId;
        private Role currentRole;
        private String username;
        private String fullName;
        private Long schoolId;
        private String schoolName;
        private String className;
        private Integer subjectCount;
        private Integer gradeCount;
        private Integer absenceCount;
        private Integer parentCount;
        private Integer studentCount;

        // Getters and setters
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public Role getCurrentRole() { return currentRole; }
        public void setCurrentRole(Role currentRole) { this.currentRole = currentRole; }
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }
        public Long getSchoolId() { return schoolId; }
        public void setSchoolId(Long schoolId) { this.schoolId = schoolId; }
        public String getSchoolName() { return schoolName; }
        public void setSchoolName(String schoolName) { this.schoolName = schoolName; }
        public String getClassName() { return className; }
        public void setClassName(String className) { this.className = className; }
        public Integer getSubjectCount() { return subjectCount; }
        public void setSubjectCount(Integer subjectCount) { this.subjectCount = subjectCount; }
        public Integer getGradeCount() { return gradeCount; }
        public void setGradeCount(Integer gradeCount) { this.gradeCount = gradeCount; }
        public Integer getAbsenceCount() { return absenceCount; }
        public void setAbsenceCount(Integer absenceCount) { this.absenceCount = absenceCount; }
        public Integer getParentCount() { return parentCount; }
        public void setParentCount(Integer parentCount) { this.parentCount = parentCount; }
        public Integer getStudentCount() { return studentCount; }
        public void setStudentCount(Integer studentCount) { this.studentCount = studentCount; }
    }
} 