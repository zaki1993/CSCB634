package com.nbu.CSCB634.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParentDto {
    
    private Long id;
    
    @NotBlank(message = "Потребителското име е задължително")
    private String username;
    
    @NotBlank(message = "Първото име е задължително")
    private String firstName;
    
    @NotBlank(message = "Фамилията е задължителна")
    private String lastName;
    
    @NotBlank(message = "Имейлът е задължителен")
    @Email(message = "Невалиден имейл формат")
    private String email;
    
    private Set<Long> studentIds;
    
    // Допълнителни полета за показване
    private List<String> studentNames;
    private String fullName;
    
    public String getFullName() {
        if (firstName != null && lastName != null) {
            return firstName + " " + lastName;
        }
        return fullName;
    }
} 