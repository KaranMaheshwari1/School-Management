package com.schoolmanagement.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

/**
 * Result Entity - Stores student exam results
 * File: backend/src/main/java/com/schoolmanagement/entity/Result.java
 */
@Entity
@Table(name = "results")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Result extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_id", nullable = false)
    private School school;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_id", nullable = false)
    private Exam exam;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_subject_id", nullable = false)
    private ExamSubject examSubject;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Column(name = "marks_obtained", nullable = false, precision = 10, scale = 2)
    private BigDecimal marksObtained;

    @Column(name = "grade", length = 5)
    private String grade;

    @Column(name = "remarks", columnDefinition = "TEXT")
    private String remarks;

    @Column(name = "is_absent")
    private Boolean isAbsent = false;

    // Performance tracking
    @Column(name = "weakness", columnDefinition = "TEXT")
    private String weakness;

    @Column(name = "improvement_notes", columnDefinition = "TEXT")
    private String improvementNotes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;
}