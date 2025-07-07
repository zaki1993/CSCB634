package com.nbu.CSCB634.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AcademicTermDto {
    private Long id;
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    private Long schoolId;
    private Set<Long> subjectIds;
    private Set<Long> teacherIds;
} 