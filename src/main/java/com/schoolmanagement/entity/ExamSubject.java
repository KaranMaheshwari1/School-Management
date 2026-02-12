package com.schoolmanagement.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * NEW: ExamSubject Entity
 * File: backend/src/main/java/com/schoolmanagement/entity/ExamSubject.java
 */
@Entity
@Table(name = "exam_subjects")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExamSubject extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_id", nullable = false)
    private Exam exam;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @Column(name = "exam_date")
    private LocalDate examDate;

    @Column(name = "max_marks", nullable = false, precision = 10, scale = 2)
    private BigDecimal maxMarks;

    @Column(name = "passing_marks", nullable = false, precision = 10, scale = 2)
    private BigDecimal passingMarks;

    @Column(name = "duration_minutes")
    private Integer durationMinutes;
}