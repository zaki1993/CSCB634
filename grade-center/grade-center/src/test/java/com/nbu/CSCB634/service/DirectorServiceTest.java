package com.nbu.CSCB634.service;

import com.nbu.CSCB634.model.Director;
import com.nbu.CSCB634.repository.DirectorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class DirectorServiceTest {

    @Mock
    private DirectorRepository directorRepository;

    @InjectMocks
    private DirectorService directorService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateDirector_Success() {
        Director dir = new Director();
        dir.setId(1L);
        dir.getUser().setFirstName("John");
        dir.getUser().setLastName("Doe");

        when(directorRepository.save(any(Director.class))).thenReturn(dir);

        Director result = directorService.createDirector(dir);
        assertThat(result).isNotNull();
        assertThat(result.getUser().getFirstName()).isEqualTo("John");
        verify(directorRepository).save(dir);
    }

    @Test
    void testGetDirectorById_Found() {
        Director director = new Director();
        director.setId(1L);
        when(directorRepository.findById(1L)).thenReturn(Optional.of(director));

        Optional<Director> result = directorService.getDirectorById(1L);
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1L);
    }

    @Test
    void testGetDirectorById_NotFound() {
        when(directorRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Director> result = directorService.getDirectorById(99L);
        assertThat(result).isEmpty();
    }

    @Test
    void testGetAllDirectors() {
        Director d1 = new Director();
        Director d2 = new Director();
        when(directorRepository.findAll()).thenReturn(java.util.List.of(d1, d2));

        var result = directorService.getAllDirectors();
        assertThat(result).hasSize(2);
    }

    @Test
    void testUpdateDirector_Success() {
        Director existing = new Director();
        existing.setId(1L);
        existing.getUser().setFirstName("Old");
        existing.getUser().setLastName("Name");

        Director update = new Director();
        update.getUser().setFirstName("New");
        update.getUser().setLastName("NameNew");

        when(directorRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(directorRepository.save(any(Director.class))).thenReturn(existing);

        Director updated = directorService.updateDirector(1L, update);
        assertThat(updated.getUser().getFirstName()).isEqualTo("New");
        assertThat(updated.getUser().getLastName()).isEqualTo("NameNew");
    }

    @Test
    void testUpdateDirector_NotFound() {
        Director update = new Director();
        when(directorRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> directorService.updateDirector(99L, update))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Director not found");
    }

    @Test
    void testDeleteDirector_Success() {
        Director existing = new Director();
        existing.setId(1L);

        when(directorRepository.findById(1L)).thenReturn(Optional.of(existing));
        doNothing().when(directorRepository).delete(existing);

        directorService.deleteDirector(1L);
        verify(directorRepository).delete(existing);
    }

    @Test
    void testDeleteDirector_NotFound() {
        when(directorRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> directorService.deleteDirector(99L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Director not found");
    }
}