package com.nbu.CSCB634.service;

import com.nbu.CSCB634.model.Parent;
import com.nbu.CSCB634.repository.ParentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ParentServiceTest {

    @Mock
    private ParentRepository parentRepository;

    @InjectMocks
    private ParentService parentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateParent_Success() {
        Parent parent = new Parent();
        parent.setId(1L);
        parent.setFirstName("Jane");

        when(parentRepository.save(any(Parent.class))).thenReturn(parent);

        Parent result = parentService.createParent(parent);
        assertThat(result).isNotNull();
        assertThat(result.getFirstName()).isEqualTo("Jane");
    }

    @Test
    void testGetParentById_Found() {
        Parent parent = new Parent();
        parent.setId(1L);
        when(parentRepository.findById(1L)).thenReturn(Optional.of(parent));

        Optional<Parent> result = parentService.getParentById(1L);
        assertThat(result).isPresent();
    }

    @Test
    void testGetParentById_NotFound() {
        when(parentRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Parent> result = parentService.getParentById(99L);
        assertThat(result).isEmpty();
    }

    @Test
    void testGetAllParents() {
        Parent p1 = new Parent();
        Parent p2 = new Parent();
        when(parentRepository.findAll()).thenReturn(List.of(p1, p2));

        var result = parentService.getAllParents();
        assertThat(result).hasSize(2);
    }

    @Test
    void testUpdateParent_Success() {
        Parent existing = new Parent();
        existing.setId(1L);
        existing.setFirstName("Old");

        Parent update = new Parent();
        update.setFirstName("New");

        when(parentRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(parentRepository.save(any(Parent.class))).thenReturn(existing);

        Parent updated = parentService.updateParent(1L, update);
        assertThat(updated.getFirstName()).isEqualTo("New");
    }

    @Test
    void testUpdateParent_NotFound() {
        Parent update = new Parent();
        when(parentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> parentService.updateParent(99L, update)).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("Parent not found");
    }

    @Test
    void testDeleteParent_Success() {
        Parent existing = new Parent();
        existing.setId(1L);

        when(parentRepository.findById(1L)).thenReturn(Optional.of(existing));
        doNothing().when(parentRepository).delete(existing);

        parentService.deleteParent(1L);
        verify(parentRepository).delete(existing);
    }

    @Test
    void testDeleteParent_NotFound() {
        when(parentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> parentService.deleteParent(99L)).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("Parent not found");
    }
}