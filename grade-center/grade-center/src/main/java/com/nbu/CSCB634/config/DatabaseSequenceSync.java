package com.nbu.CSCB634.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DatabaseSequenceSync implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) {
        try {
            log.info("Синхронизиране на PostgreSQL sequence-овете...");
            
            // Синхронизиране на всички sequence-ове
            syncSequence("school_id_seq", "school");
            syncSequence("schoolclass_id_seq", "schoolclass");
            syncSequence("subject_id_seq", "subject");
            syncSequence("grade_center_users_id_seq", "grade_center_users");
            syncSequence("academicterm_id_seq", "academicterm");
            syncSequence("grade_id_seq", "grade");
            syncSequence("absence_id_seq", "absence");
            
            log.info("Sequence-овете са синхронизирани успешно!");
            
        } catch (Exception e) {
            log.error("Грешка при синхронизиране на sequence-овете: {}", e.getMessage());
            // Не спираме приложението, защото може да не е PostgreSQL
        }
    }

    /**
     * Синхронизира конкретен sequence - може да се извика runtime
     */
    public void syncSequence(String sequenceName, String tableName) {
        try {
            String sql = "SELECT setval('" + sequenceName + "', COALESCE((SELECT MAX(id) FROM " + tableName + "), 1), true)";
            Long newValue = jdbcTemplate.queryForObject(sql, Long.class);
            log.debug("Sequence {} синхронизиран с стойност: {}", sequenceName, newValue);
        } catch (Exception e) {
            log.warn("Не може да се синхронизира sequence {}: {}", sequenceName, e.getMessage());
        }
    }

    /**
     * Синхронизира absence sequence - специален метод за absence таблицата
     */
    public void syncAbsenceSequence() {
        syncSequence("absence_id_seq", "absence");
    }

    /**
     * Синхронизира grade sequence - специален метод за grade таблицата
     */
    public void syncGradeSequence() {
        syncSequence("grade_id_seq", "grade");
    }

    /**
     * Синхронизира всички основни sequences
     */
    public void syncAllSequences() {
        syncSequence("school_id_seq", "school");
        syncSequence("schoolclass_id_seq", "schoolclass");
        syncSequence("subject_id_seq", "subject");
        syncSequence("grade_center_users_id_seq", "grade_center_users");
        syncSequence("academicterm_id_seq", "academicterm");
        syncSequence("grade_id_seq", "grade");
        syncSequence("absence_id_seq", "absence");
    }
} 