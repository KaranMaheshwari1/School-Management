package com.schoolmanagement.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "classes")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClassEntity extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_id", nullable = false)
    private School school;

    @Column(name = "class_name", nullable = false, length = 50)
    private String className;

    @Column(name = "class_number", nullable = false)
    private Integer classNumber;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "is_active")
    private Boolean isActive = true;
}