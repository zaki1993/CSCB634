package com.nbu.CSCB869.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

public enum TeacherType {
    ASSISTANT,
    SENIOR_ASSISTANT,
    ASSOCIATE_PROFESSOR,
    PROFESSOR
}