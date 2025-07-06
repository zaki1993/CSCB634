package com.nbu.CSCB634.service;

import com.nbu.CSCB634.model.Subject;
import com.nbu.CSCB634.repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class SubjectService {

    private final SubjectRepository subjectRepository;

    public List<Subject> getAll() {
        return subjectRepository.findAll();
    }

    public List<Subject> getAllSubjects() {
        return subjectRepository.findAll();
    }

    public Optional<Subject> getSubjectById(Long id) {
        return subjectRepository.findById(id);
    }

    public Subject createSubject(Subject subject) {
        return subjectRepository.save(subject);
    }

    public Subject updateSubject(Long id, Subject subjectDetails) {
        return subjectRepository.findById(id)
                .map(subject -> {
                    subject.setName(subjectDetails.getName());
                    return subjectRepository.save(subject);
                }).orElseThrow(() -> new IllegalArgumentException("Subject not found"));
    }

    public void deleteSubject(Long id) {
        Subject subject = subjectRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Subject not found"));
        subjectRepository.delete(subject);
    }
}
