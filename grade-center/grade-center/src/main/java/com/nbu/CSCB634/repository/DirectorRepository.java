package com.nbu.CSCB634.repository;

import com.nbu.CSCB634.model.Director;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DirectorRepository extends JpaRepository<Director, Long> {
    // Find director by associated school ID
    Optional<Director> findBySchoolId(Long schoolId);
}