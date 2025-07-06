package com.nbu.CSCB634.repository;

import com.nbu.CSCB634.model.SchoolClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SchoolClassRepository extends JpaRepository<SchoolClass, Long> {
    // Additional query methods if needed, e.g. find by name
}