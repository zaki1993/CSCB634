-- Insert Schools
INSERT INTO school (id, name, address) VALUES (1, 'Springfield High School', '742 Evergreen Terrace')
ON CONFLICT (id) DO NOTHING;
INSERT INTO school (id, name, address) VALUES (2, 'Shelbyville Secondary School', '123 Main Street')
ON CONFLICT (id) DO NOTHING;

-- Insert Classes
INSERT INTO schoolclass (id, name, gradenumber, school_id) VALUES (1, '1A', 1, 1)
ON CONFLICT (id) DO NOTHING;
INSERT INTO schoolclass (id, name, gradenumber, school_id) VALUES (2, '12B', 12, 1)
ON CONFLICT (id) DO NOTHING;
INSERT INTO schoolclass (id, name, gradenumber, school_id) VALUES (3, '10C', 10, 2)
ON CONFLICT (id) DO NOTHING;

-- Insert Subjects
INSERT INTO subject (id, name) VALUES (1, 'Mathematics') ON CONFLICT (id) DO NOTHING;
INSERT INTO subject (id, name) VALUES (2, 'History') ON CONFLICT (id) DO NOTHING;
INSERT INTO subject (id, name) VALUES (3, 'Physics') ON CONFLICT (id) DO NOTHING;
INSERT INTO subject (id, name) VALUES (4, 'English') ON CONFLICT (id) DO NOTHING;

-- Insert Users
INSERT INTO grade_center_users (id, username, password, role, firstname, lastname, email) VALUES
(1, 'admin', '$2a$10$6PSIH5sVDZvcq3VR2J2YT.3pXqdBI..asG.z30myo6fZw24pwT4jW', 'ADMINISTRATOR', 'Admin', 'User', 'admin@domain.com')
ON CONFLICT (id) DO NOTHING;

INSERT INTO grade_center_users (id, username, password, role, firstname, lastname, email) VALUES
(2, 'seymour', '$2a$10$JkqnJiubEG3SYim6X9qsc.oizsDyoar8kEP7CyhBdehn0gLO4eoXy', 'DIRECTOR', 'Seymour', 'Skinner', 'seymour@school.com')
ON CONFLICT (id) DO NOTHING;

-- Repeat similarly for other users...
INSERT INTO grade_center_users (id, username, password, role, firstname, lastname, email) VALUES
(3, 'chalmers', '$2a$10$omq3e6rq0tqo.RkPNvNdHuOVypOTOqjAlwp1v81k4V37XKaXApchK', 'DIRECTOR', 'Chalmers', 'Superintendent', 'chalmers@school.com')
ON CONFLICT (id) DO NOTHING;
INSERT INTO grade_center_users (id, username, password, role, firstname, lastname, email) VALUES
(4, 'edna', '$2a$10$kryMVZO0/cMBzts7Z0JFj.yVT3CWPu2gpHRNr6h/2Je05Xbc8X/W.', 'TEACHER', 'Edna', 'Krabappel', 'edna@school.com')
ON CONFLICT (id) DO NOTHING;
INSERT INTO grade_center_users (id, username, password, role, firstname, lastname, email) VALUES
(5, 'elizabeth', '$2a$10$pn5Jcpvv8uKyVcZduzf0pOXL3JYRcLuMcTdDSEeA7rWzQSJlNZuAO', 'TEACHER', 'Elizabeth', 'Hoover', 'elizabeth@school.com')
ON CONFLICT (id) DO NOTHING;
INSERT INTO grade_center_users (id, username, password, role, firstname, lastname, email) VALUES
(6, 'marge', '$2a$10$tDO8fIYg6HNBIo0lrNwqnugI17SR4ppipM7QtjmGcJ/JAk4dHzLdG', 'PARENT', 'Marge', 'Simpson', 'marge@domain.com')
ON CONFLICT (id) DO NOTHING;
INSERT INTO grade_center_users (id, username, password, role, firstname, lastname, email) VALUES
(7, 'kirk', '$2a$10$WBe/Rg7PxcoIpHg7dQeVDumpEjMexekGT75qo.ipaMpqdeWhiTUnO', 'PARENT', 'Kirk', 'Van Houten', 'kirk@domain.com')
ON CONFLICT (id) DO NOTHING;
INSERT INTO grade_center_users (id, username, password, role, firstname, lastname, email) VALUES
(8, 'bart', '$2a$10$C7q1VDwbLC3UPH8DrRcql.J47K6IVMR08YxEMDAFPzBdlUvJKbPgy', 'STUDENT', 'Bart', 'Simpson', 'bart@school.com')
ON CONFLICT (id) DO NOTHING;
INSERT INTO grade_center_users (id, username, password, role, firstname, lastname, email) VALUES
(9, 'lisa', '$2a$10$qxFPUizwdMhxkDVxPREeUuPgvL/IGg.B1FoE9uI7wgtcJgVcnOSpq', 'STUDENT', 'Lisa', 'Simpson', 'lisa@school.com')
ON CONFLICT (id) DO NOTHING;
INSERT INTO grade_center_users (id, username, password, role, firstname, lastname, email) VALUES
(10, 'milhouse', '$2a$10$/2WqlJRi/GtZ/KZ6R6fdH.7oqlZlXya9Rl2l16NllG48RirmKc3DS', 'STUDENT', 'Milhouse', 'Van Houten', 'milhouse@school.com')
ON CONFLICT (id) DO NOTHING;
INSERT INTO grade_center_users (id, username, password, role, firstname, lastname, email) VALUES
(11, 'nelson', '$2a$10$yRLpxVe.7Cnj6VeMlzaX5O7HdI.c7lrlXzPutpFSKqyzHT7wYRqc.', 'STUDENT', 'Nelson', 'Muntz', 'nelson@school.com')
ON CONFLICT (id) DO NOTHING;

-- Directors
INSERT INTO directors (id, school_id) VALUES (2, 1) ON CONFLICT (id) DO NOTHING;
INSERT INTO directors (id, school_id) VALUES (3, 2) ON CONFLICT (id) DO NOTHING;

-- Teachers
INSERT INTO teachers (id, school_id) VALUES (4, 1) ON CONFLICT (id) DO NOTHING;
INSERT INTO teachers (id, school_id) VALUES (5, 2) ON CONFLICT (id) DO NOTHING;

-- Parents
INSERT INTO parents (id) VALUES (6) ON CONFLICT (id) DO NOTHING;
INSERT INTO parents (id) VALUES (7) ON CONFLICT (id) DO NOTHING;

-- Students
INSERT INTO students (id, school_id, class_id) VALUES (8, 1, 1) ON CONFLICT (id) DO NOTHING;
INSERT INTO students (id, school_id, class_id) VALUES (9, 1, 2) ON CONFLICT (id) DO NOTHING;
INSERT INTO students (id, school_id, class_id) VALUES (10, 1, 1) ON CONFLICT (id) DO NOTHING;
INSERT INTO students (id, school_id, class_id) VALUES (11, 2, 3) ON CONFLICT (id) DO NOTHING;

-- Parent-Student Relationships
INSERT INTO parent_students (parent_id, student_id) VALUES (6, 8) ON CONFLICT DO NOTHING;
INSERT INTO parent_students (parent_id, student_id) VALUES (6, 9) ON CONFLICT DO NOTHING;
INSERT INTO parent_students (parent_id, student_id) VALUES (7, 10) ON CONFLICT DO NOTHING;

-- Teacher Qualified Subjects
INSERT INTO teacher_qualified_subjects (teacher_id, subject_id) VALUES (4, 1) ON CONFLICT DO NOTHING;
INSERT INTO teacher_qualified_subjects (teacher_id, subject_id) VALUES (4, 3) ON CONFLICT DO NOTHING;
INSERT INTO teacher_qualified_subjects (teacher_id, subject_id) VALUES (5, 2) ON CONFLICT DO NOTHING;
INSERT INTO teacher_qualified_subjects (teacher_id, subject_id) VALUES (5, 4) ON CONFLICT DO NOTHING;

-- Academic Terms
INSERT INTO academicterm (id, name, startdate, enddate, school_id) VALUES (1, '2023 Fall', '2023-09-01', '2023-12-31', 1)
ON CONFLICT (id) DO NOTHING;
INSERT INTO academicterm (id, name, startdate, enddate, school_id) VALUES (2, '2024 Spring', '2024-01-15', '2024-05-31', 1)
ON CONFLICT (id) DO NOTHING;

-- Academic Term Subjects
INSERT INTO academicterm_subjects (academic_term_id, subject_id) VALUES (1, 1) ON CONFLICT DO NOTHING;
INSERT INTO academicterm_subjects (academic_term_id, subject_id) VALUES (1, 2) ON CONFLICT DO NOTHING;
INSERT INTO academicterm_subjects (academic_term_id, subject_id) VALUES (2, 3) ON CONFLICT DO NOTHING;
INSERT INTO academicterm_subjects (academic_term_id, subject_id) VALUES (2, 4) ON CONFLICT DO NOTHING;

-- Academic Term Teachers
INSERT INTO academicterm_teachers (academic_term_id, teacher_id) VALUES (1, 4) ON CONFLICT DO NOTHING;
INSERT INTO academicterm_teachers (academic_term_id, teacher_id) VALUES (2, 5) ON CONFLICT DO NOTHING;

-- Sample Grades
INSERT INTO grade (id, value, dateawarded, student_id, subject_id, teacher_id, term_id) VALUES
(1, 6, '2024-01-15', 8, 1, 4, 1) ON CONFLICT (id) DO NOTHING;
INSERT INTO grade (id, value, dateawarded, student_id, subject_id, teacher_id, term_id) VALUES
(2, 5, '2024-01-20', 8, 3, 4, 1) ON CONFLICT (id) DO NOTHING;
INSERT INTO grade (id, value, dateawarded, student_id, subject_id, teacher_id, term_id) VALUES
(3, 6, '2024-01-25', 9, 1, 4, 1) ON CONFLICT (id) DO NOTHING;
INSERT INTO grade (id, value, dateawarded, student_id, subject_id, teacher_id, term_id) VALUES
(4, 4, '2024-02-01', 9, 3, 4, 1) ON CONFLICT (id) DO NOTHING;
INSERT INTO grade (id, value, dateawarded, student_id, subject_id, teacher_id, term_id) VALUES
(5, 3, '2024-02-05', 10, 1, 4, 1) ON CONFLICT (id) DO NOTHING;
INSERT INTO grade (id, value, dateawarded, student_id, subject_id, teacher_id, term_id) VALUES
(6, 5, '2024-02-10', 11, 2, 5, 2) ON CONFLICT (id) DO NOTHING;

-- Sample Absences
INSERT INTO absence (id, absencedate, justified, student_id, teacher_id, subject_id) VALUES
(1, '2024-01-16', true, 8, 4, 1) ON CONFLICT (id) DO NOTHING;
INSERT INTO absence (id, absencedate, justified, student_id, teacher_id, subject_id) VALUES
(2, '2024-01-22', false, 8, 4, 3) ON CONFLICT (id) DO NOTHING;
INSERT INTO absence (id, absencedate, justified, student_id, teacher_id, subject_id) VALUES
(3, '2024-01-30', null, 9, 4, 1) ON CONFLICT (id) DO NOTHING;
INSERT INTO absence (id, absencedate, justified, student_id, teacher_id, subject_id) VALUES
(4, '2024-02-03', true, 9, 4, null) ON CONFLICT (id) DO NOTHING;
INSERT INTO absence (id, absencedate, justified, student_id, teacher_id, subject_id) VALUES
(5, '2024-02-08', false, 10, 4, 1) ON CONFLICT (id) DO NOTHING;
INSERT INTO absence (id, absencedate, justified, student_id, teacher_id, subject_id) VALUES
(6, '2024-02-12', null, 11, 5, 2) ON CONFLICT (id) DO NOTHING;

-- Synchronize sequences with existing data
-- This fixes the duplicate key error when creating new records
SELECT setval('school_id_seq', COALESCE((SELECT MAX(id) FROM school), 1), true);
SELECT setval('schoolclass_id_seq', COALESCE((SELECT MAX(id) FROM schoolclass), 1), true);
SELECT setval('subject_id_seq', COALESCE((SELECT MAX(id) FROM subject), 1), true);
SELECT setval('grade_center_users_id_seq', COALESCE((SELECT MAX(id) FROM grade_center_users), 1), true);
SELECT setval('academicterm_id_seq', COALESCE((SELECT MAX(id) FROM academicterm), 1), true);
SELECT setval('grade_id_seq', COALESCE((SELECT MAX(id) FROM grade), 1), true);
SELECT setval('absence_id_seq', COALESCE((SELECT MAX(id) FROM absence), 1), true);

commit;