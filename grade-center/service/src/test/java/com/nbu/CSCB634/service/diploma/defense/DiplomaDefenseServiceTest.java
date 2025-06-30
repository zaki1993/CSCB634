package com.nbu.CSCB634.service.diploma.defense;

import com.nbu.CSCB634.model.diploma.defense.DiplomaDefense;
import com.nbu.CSCB634.repository.diploma.defense.DiplomaDefenseRepository;
import com.nbu.CSCB634.repository.diploma.thesis.DiplomaThesisRepository;
import com.nbu.CSCB634.repository.diploma.thesis.review.ThesisReviewRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class DiplomaDefenseServiceTest {

    @Mock
    private DiplomaDefenseRepository diplomaDefenseRepository;

    @Mock
    private DiplomaThesisRepository diplomaThesisRepository;

    @Mock
    private ThesisReviewRepository thesisReviewRepository;

    @InjectMocks
    private DiplomaDefenseService diplomaDefenseService;

    @Test
    void getAllDefenses() {
        DiplomaDefense defense1 = new DiplomaDefense();
        DiplomaDefense defense2 = new DiplomaDefense();
        when(diplomaDefenseRepository.findAll()).thenReturn(Arrays.asList(defense1, defense2));

        var defenses = diplomaDefenseService.getAllDefenses();

        assertEquals(2, defenses.size());
    }

    @Test
    void getDefenseById_found() {
        DiplomaDefense defense = new DiplomaDefense();
        when(diplomaDefenseRepository.findById(1L)).thenReturn(Optional.of(defense));

        var result = diplomaDefenseService.getDefenseById(1L);

        assertNotNull(result);
    }

    @Test
    void getDefenseById_notFound() {
        when(diplomaDefenseRepository.findById(1L)).thenReturn(Optional.empty());

        var result = diplomaDefenseService.getDefenseById(1L);

        assertNull(result);
    }

    @Test
    void saveDefense() {
        // Arrange: Set up the input and mock repository behavior
        DiplomaDefense defense = new DiplomaDefense();
        defense.setId(1L);
        defense.setLocation("Room 101");
        defense.setDefenseDate(LocalDateTime.now());

        // Act: Call the method under test
        diplomaDefenseService.saveDefense(defense);

        // Assert: Verify the interaction with the repository
        verify(diplomaDefenseRepository, times(1)).save(defense);
    }

    @Test
    void deleteDefense() {
        DiplomaDefense defense = new DiplomaDefense();
        lenient().when(diplomaDefenseRepository.findById(1L)).thenReturn(Optional.of(new DiplomaDefense()));
        doNothing().when(diplomaDefenseRepository).deleteById(1L);

        diplomaDefenseService.deleteDefense(1L);

        verify(diplomaDefenseRepository, times(1)).deleteById(1L);
    }
}
