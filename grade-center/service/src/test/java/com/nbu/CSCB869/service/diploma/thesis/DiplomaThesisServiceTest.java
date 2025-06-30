package com.nbu.CSCB869.service.diploma.thesis;

import com.nbu.CSCB869.model.diploma.thesis.DiplomaThesis;
import com.nbu.CSCB869.repository.diploma.thesis.DiplomaThesisRepository;
import com.nbu.CSCB869.service.exceptions.DiplomaThesisNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DiplomaThesisServiceTest {

    @Mock
    private DiplomaThesisRepository diplomaThesisRepository;

    @InjectMocks
    private DiplomaThesisService diplomaThesisService;

    @Test
    void createDiplomaThesis() {
        DiplomaThesis thesis = new DiplomaThesis();
        when(diplomaThesisRepository.save(thesis)).thenReturn(thesis);

        diplomaThesisService.createDiplomaThesis(thesis);

        verify(diplomaThesisRepository, times(1)).save(thesis);
    }

    @Test
    void getThesisById_found() {
        DiplomaThesis thesis = new DiplomaThesis();
        when(diplomaThesisRepository.findById(1L)).thenReturn(Optional.of(thesis));

        var result = diplomaThesisService.getThesisById(1L);

        assertNotNull(result);
    }

    @Test
    void getThesisById_notFound() {
        when(diplomaThesisRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(DiplomaThesisNotFoundException.class, () -> diplomaThesisService.getThesisById(1L));
    }

    @Test
    void getAllTheses() {
        DiplomaThesis thesis1 = new DiplomaThesis();
        DiplomaThesis thesis2 = new DiplomaThesis();
        when(diplomaThesisRepository.findAll()).thenReturn(Arrays.asList(thesis1, thesis2));

        var theses = diplomaThesisService.getAllTheses();

        assertEquals(2, theses.size());
    }
}
