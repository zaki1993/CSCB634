package com.nbu.CSCB634.controller.diploma.thesis.dto;

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
