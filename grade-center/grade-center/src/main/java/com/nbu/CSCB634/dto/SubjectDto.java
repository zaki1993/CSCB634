package com.nbu.CSCB634.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubjectDto {
    private Long id;
    
    @NotBlank(message = "Името на предмета е задължително")
    private String name;
    
    // Helper method to get full display name
    public String getFullName() {
        return name;
    }
} 