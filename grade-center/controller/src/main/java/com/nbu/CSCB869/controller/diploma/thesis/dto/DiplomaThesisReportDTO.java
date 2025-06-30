package com.nbu.CSCB869.controller.diploma.thesis.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DiplomaThesisReportDTO {
    private String title;
    private String studentName;
    private String teacherName;
    private double grade;
}
