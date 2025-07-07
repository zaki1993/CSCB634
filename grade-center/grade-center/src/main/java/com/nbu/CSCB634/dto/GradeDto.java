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
public class GradeDto {
    private Long id;
    private Integer value;
    private LocalDate dateAwarded;
    private Long studentId;
    private Long subjectId;
    private Long teacherId;
    private Long termId;
} 