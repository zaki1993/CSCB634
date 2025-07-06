package com.nbu.CSCB634.repository;

import com.nbu.CSCB634.model.AcademicTerm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AcademicTermRepository extends JpaRepository<AcademicTerm, Long> {
    // Additional query methods if needed, e.g. find by name
}