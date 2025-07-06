package com.nbu.CSCB634.service;

import com.nbu.CSCB634.model.Director;
import com.nbu.CSCB634.repository.DirectorRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DirectorService {

    private final DirectorRepository directorRepository;

    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public Director createDirector(@Valid Director director) {
        return directorRepository.save(director);
    }

    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'DIRECTOR')")
    public Optional<Director> getDirectorById(Long id) {
        return directorRepository.findById(id);
    }

    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'DIRECTOR')")
    public List<Director> getAllDirectors() {
        return directorRepository.findAll();
    }

    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public Director updateDirector(Long id, @Valid Director updatedDirector) {
        return directorRepository.findById(id)
                .map(director -> {
                    director.getUser().setFirstName(updatedDirector.getUser().getFirstName());
                    director.getUser().setLastName(updatedDirector.getUser().getLastName());
                    director.setSchool(updatedDirector.getSchool());
                    return directorRepository.save(director);
                })
                .orElseThrow(() -> new IllegalArgumentException("Director not found"));
    }

    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public void deleteDirector(Long id) {
        Director director = directorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Director not found"));
        directorRepository.delete(director);
    }

    public Long getNextAvailableId() {
        List<Director> allDirectors = directorRepository.findAll();
        Long maxId = allDirectors.stream()
                .mapToLong(Director::getId)
                .max()
                .orElse(0L);
        return maxId + 1;
    }
}