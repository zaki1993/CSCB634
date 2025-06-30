-- Insert users
INSERT INTO graduation_system_users (username, password, user_type)
VALUES
    ('teacher1', 'teacher1', 0),
    ('teacher2', 'teacher2', 0),
    ('teacher3', 'teacher3', 0),
    ('teacher4', 'teacher4', 0),
    ('student1', 'student1', 1),
    ('student2', 'student2', 1);

-- Insert teachers
INSERT INTO teacher (name, type, username)
VALUES
    ('Teacher One', "ASSISTANT", 'teacher1'),
    ('Teacher Two', "SENIOR_ASSISTANT", 'teacher2')
    ('Teacher One', "ASSOCIATE_PROFESSOR", 'teacher3'),
    ('Teacher Two', "PROFESSOR", 'teacher4');

-- Insert students
INSERT INTO student (name, fn, username)
VALUES
    ('Student One', '12345', 'student1'),
    ('Student Two', '12346', 'student2')

commit;