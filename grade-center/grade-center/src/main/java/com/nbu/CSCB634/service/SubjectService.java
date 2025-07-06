package com.nbu.CSCB634.service;

import com.nbu.CSCB634.model.Subject;
import com.nbu.CSCB634.repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class SubjectService {

    private final SubjectRepository subjectRepository;

    public List<Subject> getAll() {
        return subjectRepository.findAll();
    }

    public void deleteSubject(Long id) {
        Subject subject = subjectRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Subject not found"));
        subjectRepository.delete(subject);
    }

    public Subject createSubject(Subject subject) {
        return subjectRepository.save(subject);
    }
}
