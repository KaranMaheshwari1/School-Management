package com.schoolmanagement.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "students")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Student extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true, nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_id", nullable = false)
    private School school;

    @Column(name = "admission_number", unique = true, nullable = false, length = 50)
    private String admissionNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_section_id", nullable = false)
    private ClassSection classSection;

    @Column(name = "roll_number")
    private Integer rollNumber;

    @Column(name = "admission_date")
    private LocalDate admissionDate;

    @Column(name = "father_name", length = 100)
    private String fatherName;

    @Column(name = "father_phone", length = 20)
    private String fatherPhone;

    @Column(name = "father_occupation", length = 100)
    private String fatherOccupation;

    @Column(name = "mother_name", length = 100)
    private String motherName;

    @Column(name = "mother_phone", length = 20)
    private String motherPhone;

    @Column(name = "mother_occupation", length = 100)
    private String motherOccupation;

    @Column(name = "guardian_name", length = 100)
    private String guardianName;

    @Column(name = "guardian_phone", length = 20)
    private String guardianPhone;

    @Column(name = "guardian_relation", length = 50)
    private String guardianRelation;

    @Column(name = "emergency_contact", length = 20)
    private String emergencyContact;

    @Column(name = "blood_group", length = 5)
    private String bloodGroup;

    @Column(name = "is_active")
    private Boolean isActive = true;
}