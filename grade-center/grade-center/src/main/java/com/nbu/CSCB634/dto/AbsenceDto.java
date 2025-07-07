package com.nbu.CSCB634.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AbsenceDto {
    private Long id;
    private LocalDate absenceDate;
    private Boolean justified;
    private Long studentId;
    private Long teacherId;
    private Long subjectId;
} 