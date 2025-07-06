-- Insert Schools
INSERT INTO school (id, name, address) VALUES (1, 'Springfield High School', '742 Evergreen Terrace');
INSERT INTO school (id, name, address) VALUES (2, 'Shelbyville Secondary School', '123 Main Street');

-- Insert Classes
INSERT INTO schoolclass (id, name, gradenumber, school_id) VALUES (1, '1A', 1, 1);
INSERT INTO schoolclass (id, name, gradenumber, school_id) VALUES (2, '12B', 12, 1);
INSERT INTO schoolclass (id, name, gradenumber, school_id) VALUES (3, '10C', 10, 2);

-- Insert Subjects
INSERT INTO subject (id, name) VALUES (1, 'Mathematics');
INSERT INTO subject (id, name) VALUES (2, 'History');
INSERT INTO subject (id, name) VALUES (3, 'Physics');
INSERT INTO subject (id, name) VALUES (4, 'English');

-- Insert Users (no school_id or class_id in grade_center_users)
INSERT INTO grade_center_users (id, username, password, role, firstname, lastname, email) VALUES
(1, 'admin', '$2a$10$6PSIH5sVDZvcq3VR2J2YT.3pXqdBI..asG.z30myo6fZw24pwT4jW', 'ADMINISTRATOR', 'Admin', 'User', 'admin@domain.com'),
(2, 'seymour', '$2a$10$JkqnJiubEG3SYim6X9qsc.oizsDyoar8kEP7CyhBdehn0gLO4eoXy', 'DIRECTOR', 'Seymour', 'Skinner', 'seymour@school.com'),
(3, 'chalmers', '$2a$10$omq3e6rq0tqo.RkPNvNdHuOVypOTOqjAlwp1v81k4V37XKaXApchK', 'DIRECTOR', 'Chalmers', 'Superintendent', 'chalmers@school.com'),
(4, 'edna', '$2a$10$kryMVZO0/cMBzts7Z0JFj.yVT3CWPu2gpHRNr6h/2Je05Xbc8X/W.', 'TEACHER', 'Edna', 'Krabappel', 'edna@school.com'),
(5, 'elizabeth', '$2a$10$pn5Jcpvv8uKyVcZduzf0pOXL3JYRcLuMcTdDSEeA7rWzQSJlNZuAO', 'TEACHER', 'Elizabeth', 'Hoover', 'elizabeth@school.com'),
(6, 'marge', '$2a$10$tDO8fIYg6HNBIo0lrNwqnugI17SR4ppipM7QtjmGcJ/JAk4dHzLdG', 'PARENT', 'Marge', 'Simpson', 'marge@domain.com'),
(7, 'kirk', '$2a$10$WBe/Rg7PxcoIpHg7dQeVDumpEjMexekGT75qo.ipaMpqdeWhiTUnO', 'PARENT', 'Kirk', 'Van Houten', 'kirk@domain.com'),
(8, 'bart', '$2a$10$C7q1VDwbLC3UPH8DrRcql.J47K6IVMR08YxEMDAFPzBdlUvJKbPgy', 'STUDENT', 'Bart', 'Simpson', 'bart@school.com'),
(9, 'lisa', '$2a$10$qxFPUizwdMhxkDVxPREeUuPgvL/IGg.B1FoE9uI7wgtcJgVcnOSpq', 'STUDENT', 'Lisa', 'Simpson', 'lisa@school.com'),
(10, 'milhouse', '$2a$10$/2WqlJRi/GtZ/KZ6R6fdH.7oqlZlXya9Rl2l16NllG48RirmKc3DS', 'STUDENT', 'Milhouse', 'Van Houten', 'milhouse@school.com'),
(11, 'nelson', '$2a$10$yRLpxVe.7Cnj6VeMlzaX5O7HdI.c7lrlXzPutpFSKqyzHT7wYRqc.', 'STUDENT', 'Nelson', 'Muntz', 'nelson@school.com');

-- Insert Directors (linked to grade_center_users, has school_id)
INSERT INTO directors (id, school_id) VALUES (2, 1);
INSERT INTO directors (id, school_id) VALUES (3, 2);

-- Insert Teachers (linked to grade_center_users, has school_id)
INSERT INTO teachers (id, school_id) VALUES (4, 1);
INSERT INTO teachers (id, school_id) VALUES (5, 2);

-- Insert Parents (linked to grade_center_users)
INSERT INTO parents (id) VALUES (6);
INSERT INTO parents (id) VALUES (7);

-- Insert Students (linked to grade_center_users, has school_id and class_id)
INSERT INTO students (id, school_id, class_id) VALUES (8, 1, 1); -- Bart -> 1A
INSERT INTO students (id, school_id, class_id) VALUES (9, 1, 2); -- Lisa -> 12B
INSERT INTO students (id, school_id, class_id) VALUES (10, 1, 1); -- Milhouse -> 1A
INSERT INTO students (id, school_id, class_id) VALUES (11, 2, 3); -- Nelson -> 10C

-- Parent-Student Relationships
INSERT INTO parent_students (parent_id, student_id) VALUES (6, 8); -- Marge -> Bart
INSERT INTO parent_students (parent_id, student_id) VALUES (6, 9); -- Marge -> Lisa
INSERT INTO parent_students (parent_id, student_id) VALUES (7, 10); -- Kirk -> Milhouse

-- Teacher Qualified Subjects (ManyToMany Mapping)
INSERT INTO teacher_qualified_subjects (teacher_id, subject_id) VALUES (4, 1); -- Edna teaches Mathematics
INSERT INTO teacher_qualified_subjects (teacher_id, subject_id) VALUES (4, 3); -- Edna teaches Physics
INSERT INTO teacher_qualified_subjects (teacher_id, subject_id) VALUES (5, 2); -- Elizabeth teaches History
INSERT INTO teacher_qualified_subjects (teacher_id, subject_id) VALUES (5, 4); -- Elizabeth teaches English

-- Academic Terms
INSERT INTO academicterm (id, name, startdate, enddate, school_id) VALUES
(1, '2023 Fall', '2023-09-01', '2023-12-31', 1),
(2, '2024 Spring', '2024-01-15', '2024-05-31', 1);

-- Academic Term Subjects
INSERT INTO academicterm_subjects (academic_term_id, subject_id) VALUES
(1, 1), (1, 2),
(2, 3), (2, 4);

-- Academic Term Teachers
INSERT INTO academicterm_teachers (academic_term_id, teacher_id) VALUES
(1, 4),
(2, 5);

COMMIT;
