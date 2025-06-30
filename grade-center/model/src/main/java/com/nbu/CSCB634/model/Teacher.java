package com.nbu.CSCB634.model;

import com.nbu.CSCB634.model.auth.User;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity representing a Teacher in the system.
 * Contains teacher's name and position (e.g., Assistant, Associate Professor).
 */
@Entity
@Data
@Table(name="teacher")
@NoArgsConstructor
public class Teacher {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "teachertype")
    private TeacherType type;

    @Column
    private String username;

    @OneToOne
    @JoinColumn(name = "username", referencedColumnName = "username", nullable = false, insertable = false, updatable = false)
    private User user;

    public Teacher(String name, TeacherType type) {
        setName(name);
        setType(type);
    }
}
