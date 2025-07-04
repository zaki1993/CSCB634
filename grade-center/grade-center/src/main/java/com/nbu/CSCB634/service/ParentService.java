package com.nbu.CSCB634.service;

import com.nbu.CSCB634.model.Parent;
import com.nbu.CSCB634.repository.ParentRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ParentService {

    private final ParentRepository parentRepository;

    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public Parent createParent(@Valid Parent parent) {
        return parentRepository.save(parent);
    }

    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'DIRECTOR', 'PARENT')")
    public Optional<Parent> getParentById(Long id) {
        return parentRepository.findById(id);
    }

    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'DIRECTOR')")
    public List<Parent> getAllParents() {
        return parentRepository.findAll();
    }

    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public Parent updateParent(Long id, @Valid Parent updatedParent) {
        return parentRepository.findById(id)
                .map(parent -> {
                    parent.setFirstName(updatedParent.getFirstName());
                    parent.setLastName(updatedParent.getLastName());
                    parent.setChildren(updatedParent.getChildren());
                    return parentRepository.save(parent);
                })
                .orElseThrow(() -> new IllegalArgumentException("Parent not found"));
    }

    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public void deleteParent(Long id) {
        Parent parent = parentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Parent not found"));
        parentRepository.delete(parent);
    }
}