package com.schoolmanagement.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;

@Data
public class StudentCreateRequest {

    @NotNull(message = "School ID is required")
    private Long schoolId;

    @NotBlank(message = "Admission number is required")
    private String admissionNumber;

    @NotNull(message = "Class section ID is required")
    private Long classSectionId;

    private Integer rollNumber;

    private LocalDate admissionDate;

    // User details
    @NotBlank(message = "First name is required")
    private String firstName;

    private String lastName;

    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "Password is required")
    private String password;

    private LocalDate dateOfBirth;

    private String gender;

    private String phone;

    private String address;

    private String city;

    private String state;

    // Parent details
    private String fatherName;
    private String fatherPhone;
    private String fatherOccupation;

    private String motherName;
    private String motherPhone;
    private String motherOccupation;

    private String guardianName;
    private String guardianPhone;
    private String guardianRelation;

    private String emergencyContact;
    private String bloodGroup;
}