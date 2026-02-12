package com.schoolmanagement.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "schools")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class School extends BaseEntity {

    @Column(name = "school_code", unique = true, nullable = false, length = 20)
    private String schoolCode;

    @Column(name = "school_name", nullable = false, length = 200)
    private String schoolName;

    @Column(name = "logo_url", length = 500)
    private String logoUrl;

    @Column(name = "address", columnDefinition = "TEXT")
    private String address;

    @Column(name = "city", length = 100)
    private String city;

    @Column(name = "state", length = 100)
    private String state;

    @Column(name = "pincode", length = 10)
    private String pincode;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "website", length = 200)
    private String website;

    @Column(name = "established_year")
    private Integer establishedYear;

    // Branding
    @Column(name = "theme_primary_color", length = 7)
    private String themePrimaryColor = "#1976d2";

    @Column(name = "theme_secondary_color", length = 7)
    private String themeSecondaryColor = "#dc004e";

    @Column(name = "theme_mode", length = 10)
    private String themeMode = "light";

    // Module toggles


    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "board", length = 100)
    private String board;

    @Column(name = "exam_module_enabled")
    private Boolean examModule = true;

    @Column(name = "timetable_module")
    private Boolean timetableModule = true;
// ===== MODULE ENABLE/DISABLE FLAGS =====

    @Column(name = "attendance_module")
    private Boolean attendanceModule = true;

    @Column(name = "country", length = 100)
    private String country;


    @Column(name = "fee_module")
    private Boolean feeModule = true;

    @Column(name = "result_module")
    private Boolean resultModule = true;

    @Column(name = "notice_module")
    private Boolean noticeModule = true;

    @Column(name = "event_module")
    private Boolean eventModule = true;

    @Column(name = "complaint_module")
    private Boolean complaintModule = true;


}