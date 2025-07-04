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

-- Insert Users
INSERT INTO grade_center_users (id, username, password, role, firstname, lastname, school_id) VALUES
(1, 'admin', '{bcrypt}admin', 'ADMINISTRATOR', 'Admin', 'User', NULL),
(2, 'seymour', '{bcrypt}seymour', 'DIRECTOR', 'Seymour', 'Skinner', 1),
(3, 'chalmers', '{bcrypt}chalmers', 'DIRECTOR', 'Chalmers', 'Superintendent', 2),
(4, 'edna', '{bcrypt}edna', 'TEACHER', 'Edna', 'Krabappel', 1),
(5, 'elizabeth', '{bcrypt}elizabeth', 'TEACHER', 'Elizabeth', 'Hoover', 2),
(6, 'marge', '{bcrypt}marge', 'PARENT', 'Marge', 'Simpson', NULL),
(7, 'kirk', '{bcrypt}kirk', 'PARENT', 'Kirk', 'Van Houten', NULL),
(8, 'bart', '{bcrypt}bart', 'STUDENT', 'Bart', 'Simpson', 1),
(9, 'lisa', '{bcrypt}lisa', 'STUDENT', 'Lisa', 'Simpson', 1),
(10, 'milhouse', '{bcrypt}milhouse', 'STUDENT', 'Milhouse', 'Van Houten', 1),
(11, 'nelson', '{bcrypt}nelson', 'STUDENT', 'Nelson', 'Muntz', 2);

-- Parent-Student Relationships
INSERT INTO parent_students (parent_id, student_id) VALUES (6, 8); -- Marge -> Bart
INSERT INTO parent_students (parent_id, student_id) VALUES (6, 9); -- Marge -> Lisa
INSERT INTO parent_students (parent_id, student_id) VALUES (7, 10); -- Kirk -> Milhouse

-- Student-Class Assignments
UPDATE grade_center_users SET class_id = 1 WHERE id = 8; -- Bart
UPDATE grade_center_users SET class_id = 2 WHERE id = 9; -- Lisa
UPDATE grade_center_users SET class_id = 1 WHERE id = 10; -- Milhouse
UPDATE grade_center_users SET class_id = 3 WHERE id = 11; -- Nelson

-- Teacher Qualified Subjects (ElementCollection)
INSERT INTO teacher_qualified_subjects (teacher_id, qualified_subjects) VALUES (4, 'Mathematics');
INSERT INTO teacher_qualified_subjects (teacher_id, qualified_subjects) VALUES (4, 'Physics');
INSERT INTO teacher_qualified_subjects (teacher_id, qualified_subjects) VALUES (5, 'History');
INSERT INTO teacher_qualified_subjects (teacher_id, qualified_subjects) VALUES (5, 'English');

-- Academic Terms
INSERT INTO academicterm (id, name, start_date, end_date, school_id) VALUES
(1, '2023 Fall', '2023-09-01', '2023-12-31', 1),
(2, '2024 Spring', '2024-01-15', '2024-05-31', 1);

-- Academic Term Subjects
INSERT INTO academicterm_subjects (academic_term_id, subjects_id) VALUES
(1, 1), (1, 2),
(2, 3), (2, 4);

-- Academic Term Teachers
INSERT INTO academicterm_teachers (academic_term_id, teachers_id) VALUES
(1, 4),
(2, 5);

COMMIT;
