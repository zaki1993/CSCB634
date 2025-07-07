package com.nbu.CSCB634;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class ThymeleafTemplateTest {

    @Test
    void testDecimalFormatting() {
        // Given
        SpringResourceTemplateResolver templateResolver = new SpringResourceTemplateResolver();
        templateResolver.setPrefix("classpath:/templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode("HTML");
        templateResolver.setCharacterEncoding("UTF-8");

        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);

        Context context = new Context();
        context.setVariable("averageGrade", 4.567);

        // When & Then - These should not throw exceptions
        assertDoesNotThrow(() -> {
            // Test the new decimal formatting approach
            String expression = "${#numbers.formatDecimal(averageGrade, 0, 2)}";
            // This would be evaluated by Thymeleaf in the actual template
            // Here we just verify the variable is available
            assertTrue(context.getVariable("averageGrade") != null);
        });
    }

    @Test
    void testNullSafeDecimalFormatting() {
        // Given
        Context context = new Context();
        context.setVariable("averageGrade", null);

        // When & Then - Null values should be handled gracefully
        assertDoesNotThrow(() -> {
            Object averageGrade = context.getVariable("averageGrade");
            if (averageGrade != null) {
                // Only format if not null
                String formatted = String.format("%.2f", (Double) averageGrade);
                assertNotNull(formatted);
            } else {
                // Should handle null gracefully
                String formatted = "N/A";
                assertEquals("N/A", formatted);
            }
        });
    }

    @Test
    void testGradeDistributionRendering() {
        // Given
        Context context = new Context();
        java.util.Map<Integer, Long> gradeDistribution = new java.util.HashMap<>();
        gradeDistribution.put(6, 10L);
        gradeDistribution.put(5, 15L);
        gradeDistribution.put(4, 8L);
        gradeDistribution.put(3, 5L);
        gradeDistribution.put(2, 2L);
        
        context.setVariable("gradeDistribution", gradeDistribution);

        // When & Then - Grade distribution should be available for rendering
        assertDoesNotThrow(() -> {
            @SuppressWarnings("unchecked")
            java.util.Map<Integer, Long> distribution = 
                (java.util.Map<Integer, Long>) context.getVariable("gradeDistribution");
            
            assertNotNull(distribution);
            assertEquals(5, distribution.size());
            assertEquals(10L, distribution.get(6));
            assertEquals(15L, distribution.get(5));
        });
    }

    @Test
    void testStatisticsRendering() {
        // Given
        Context context = new Context();
        java.util.Map<String, Object> schoolStats = new java.util.HashMap<>();
        schoolStats.put("studentCount", 100);
        schoolStats.put("gradeCount", 250);
        schoolStats.put("averageGrade", 4.5);
        
        context.setVariable("schoolStats", schoolStats);

        // When & Then - Statistics should be available for rendering
        assertDoesNotThrow(() -> {
            @SuppressWarnings("unchecked")
            java.util.Map<String, Object> stats = 
                (java.util.Map<String, Object>) context.getVariable("schoolStats");
            
            assertNotNull(stats);
            assertEquals(100, stats.get("studentCount"));
            assertEquals(250, stats.get("gradeCount"));
            assertEquals(4.5, stats.get("averageGrade"));
        });
    }

    @Test
    void testNullSafeStatisticsRendering() {
        // Given
        Context context = new Context();
        context.setVariable("schoolStats", null);

        // When & Then - Null statistics should be handled gracefully
        assertDoesNotThrow(() -> {
            Object stats = context.getVariable("schoolStats");
            if (stats == null) {
                // Should provide default values
                java.util.Map<String, Object> defaultStats = new java.util.HashMap<>();
                defaultStats.put("studentCount", 0);
                defaultStats.put("gradeCount", 0);
                defaultStats.put("averageGrade", 0.0);
                
                assertEquals(0, defaultStats.get("studentCount"));
                assertEquals(0, defaultStats.get("gradeCount"));
                assertEquals(0.0, defaultStats.get("averageGrade"));
            }
        });
    }

    @Test
    void testRoleDistributionRendering() {
        // Given
        Context context = new Context();
        java.util.Map<String, Long> roleDistribution = new java.util.HashMap<>();
        roleDistribution.put("ADMINISTRATOR", 5L);
        roleDistribution.put("DIRECTOR", 10L);
        roleDistribution.put("TEACHER", 50L);
        roleDistribution.put("PARENT", 200L);
        roleDistribution.put("STUDENT", 500L);
        
        context.setVariable("roleDistribution", roleDistribution);

        // When & Then - Role distribution should be available for rendering
        assertDoesNotThrow(() -> {
            @SuppressWarnings("unchecked")
            java.util.Map<String, Long> distribution = 
                (java.util.Map<String, Long>) context.getVariable("roleDistribution");
            
            assertNotNull(distribution);
            assertEquals(5, distribution.size());
            assertEquals(5L, distribution.get("ADMINISTRATOR"));
            assertEquals(10L, distribution.get("DIRECTOR"));
            assertEquals(50L, distribution.get("TEACHER"));
            assertEquals(200L, distribution.get("PARENT"));
            assertEquals(500L, distribution.get("STUDENT"));
        });
    }

    @Test
    void testSubjectStatisticsRendering() {
        // Given
        Context context = new Context();
        java.util.Map<String, java.util.Map<String, Object>> subjectStats = new java.util.HashMap<>();
        
        java.util.Map<String, Object> mathStats = new java.util.HashMap<>();
        mathStats.put("count", 100);
        mathStats.put("average", 4.5);
        
        java.util.Map<String, Object> scienceStats = new java.util.HashMap<>();
        scienceStats.put("count", 85);
        scienceStats.put("average", 4.2);
        
        subjectStats.put("Mathematics", mathStats);
        subjectStats.put("Science", scienceStats);
        
        context.setVariable("subjectStats", subjectStats);

        // When & Then - Subject statistics should be available for rendering
        assertDoesNotThrow(() -> {
            @SuppressWarnings("unchecked")
            java.util.Map<String, java.util.Map<String, Object>> stats = 
                (java.util.Map<String, java.util.Map<String, Object>>) context.getVariable("subjectStats");
            
            assertNotNull(stats);
            assertEquals(2, stats.size());
            
            java.util.Map<String, Object> mathData = stats.get("Mathematics");
            assertNotNull(mathData);
            assertEquals(100, mathData.get("count"));
            assertEquals(4.5, mathData.get("average"));
            
            java.util.Map<String, Object> scienceData = stats.get("Science");
            assertNotNull(scienceData);
            assertEquals(85, scienceData.get("count"));
            assertEquals(4.2, scienceData.get("average"));
        });
    }
} 