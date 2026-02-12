package com.schoolmanagement.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "notices")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notice extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_id", nullable = false)
    private School school;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "notice_type")
    private NoticeType noticeType = NoticeType.GENERAL;

    @Enumerated(EnumType.STRING)
    @Column(name = "target_audience")
    private TargetAudience targetAudience = TargetAudience.ALL;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority")
    private Priority priority = Priority.MEDIUM;

    @Column(name = "publish_date", nullable = false)
    private LocalDate publishDate;

    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    public enum NoticeType {
        GENERAL, URGENT, ACADEMIC, EVENT, HOLIDAY
    }

    public enum TargetAudience {
        ALL, STUDENTS, TEACHERS, PARENTS, STAFF
    }

    public enum Priority {
        LOW, MEDIUM, HIGH
    }
}