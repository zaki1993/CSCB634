package com.nbu.CSCB634.repository;

import com.nbu.CSCB634.model.Administrator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdministratorRepository extends JpaRepository<Administrator, Long> {
    // Additional query methods if needed
} 