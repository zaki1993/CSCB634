package com.nbu.CSCB634.repository;

import com.nbu.CSCB634.model.School;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SchoolRepository extends JpaRepository<School, Long> {
    // Additional query methods if needed, e.g. find by name
}