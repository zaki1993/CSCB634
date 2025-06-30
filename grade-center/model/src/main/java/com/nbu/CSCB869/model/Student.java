package com.nbu.CSCB869.model;

import com.nbu.CSCB869.model.auth.User;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity representing a Student in the system.
 * Holds details about the student, including their name and faculty number.
 */
@Entity
@Data
@Table(name="student")
@NoArgsConstructor
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column
    private String name;

    @Column(unique = true)
    private String fn;

    @Column
    private String username;

    @OneToOne
    @JoinColumn(name = "username", referencedColumnName = "username", nullable = false, insertable = false, updatable = false)
    private User user;

    public Student(String name, String fn) {
        setName(name);
        setFn(fn);
    }
}
