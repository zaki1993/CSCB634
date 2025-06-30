package com.nbu.CSCB634.controller.diploma.defense.dto;

import com.nbu.CSCB634.model.diploma.defense.DiplomaDefense;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Data
public class DiplomaDefenseDTO {
    private Long id;
    private LocalDateTime defenseDate;
    private String location;
    private Set<String> studentNames;
    private Set<String> teacherNames;

    private DiplomaDefense delegate;

    /**
     * Checks if the defense is read-only.
     * A defense is read-only if it has any associated grades.
     *
     * @return true if the defense has grades, false otherwise.
     */
    public boolean isReadOnly() {
        return delegate != null && delegate.getGradles() != null && !delegate.getGradles().isEmpty();
    }
}
