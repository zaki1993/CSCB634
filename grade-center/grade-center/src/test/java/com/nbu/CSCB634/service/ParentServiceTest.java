package com.nbu.CSCB634.service;

import com.nbu.CSCB634.model.Parent;
import com.nbu.CSCB634.model.auth.User;
import com.nbu.CSCB634.repository.ParentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

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
        User user = new User();
        user.setFirstName("Jane");
        parent.setUser(user);

        when(parentRepository.save(any(Parent.class))).thenReturn(parent);

        Parent result = parentService.createParent(parent);
        assertThat(result).isNotNull();
        assertThat(result.getUser().getFirstName()).isEqualTo("Jane");
    }

    @Test
    void testGetParentById_Found() {
        Parent parent = new Parent();
        parent.setId(1L);
        User user = new User();
        user.setFirstName("John");
        parent.setUser(user);

        when(parentRepository.findById(1L)).thenReturn(Optional.of(parent));

        Optional<Parent> result = parentService.getParentById(1L);
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1L);
        assertThat(result.get().getUser().getFirstName()).isEqualTo("John");
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
        p1.setId(1L);
        Parent p2 = new Parent();
        p2.setId(2L);

        when(parentRepository.findAll()).thenReturn(List.of(p1, p2));

        List<Parent> result = parentService.getAllParents();
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        assertThat(result.get(1).getId()).isEqualTo(2L);
    }

    @Test
    void testUpdateParent_Success() {
        Parent existing = new Parent();
        existing.setId(1L);
        User existingUser = new User();
        existingUser.setFirstName("Old");
        existing.setUser(existingUser);

        Parent update = new Parent();
        User updateUser = new User();
        updateUser.setFirstName("New");
        update.setUser(updateUser);

        when(parentRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(parentRepository.save(any(Parent.class))).thenReturn(existing);

        Parent updated = parentService.updateParent(1L, update);
        assertThat(updated.getUser().getFirstName()).isEqualTo("New");
    }

    @Test
    void testUpdateParent_NotFound() {
        Parent update = new Parent();
        User updateUser = new User();
        updateUser.setFirstName("New");
        update.setUser(updateUser);

        when(parentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> parentService.updateParent(99L, update))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Parent not found");
    }

    @Test
    void testDeleteParent_Success() {
        Parent existing = new Parent();
        existing.setId(1L);

        when(parentRepository.findById(1L)).thenReturn(Optional.of(existing));
        doNothing().when(parentRepository).delete(existing);

        parentService.deleteParent(1L);
        verify(parentRepository, times(1)).delete(existing);
    }

    @Test
    void testDeleteParent_NotFound() {
        when(parentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> parentService.deleteParent(99L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Parent not found");
    }
}
