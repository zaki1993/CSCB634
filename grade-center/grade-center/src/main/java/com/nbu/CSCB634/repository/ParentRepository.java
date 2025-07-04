package com.nbu.CSCB634.repository;

import com.nbu.CSCB634.model.Parent;
import com.nbu.CSCB634.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface ParentRepository extends JpaRepository<Parent, Long> {
    Set<Parent> findByStudentsContaining(Student student);
}