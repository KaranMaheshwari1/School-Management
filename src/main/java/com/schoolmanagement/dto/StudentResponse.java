package com.schoolmanagement.dto;

import com.schoolmanagement.entity.Student;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentResponse {

    private Long id;
    private String admissionNumber;
    private Integer rollNumber;
    private LocalDate admissionDate;
    private UserResponse user;
    private ClassSectionResponse classSection;
    private String fatherName;
    private String fatherPhone;
    private String motherName;
    private String motherPhone;
    private String bloodGroup;
    private Boolean isActive;

    public static StudentResponse fromEntity(Student student) {
        return StudentResponse.builder()
                .id(student.getId())
                .admissionNumber(student.getAdmissionNumber())
                .rollNumber(student.getRollNumber())
                .admissionDate(student.getAdmissionDate())
                .user(UserResponse.fromEntity(student.getUser()))
                .classSection(ClassSectionResponse.fromEntity(student.getClassSection()))
                .fatherName(student.getFatherName())
                .fatherPhone(student.getFatherPhone())
                .motherName(student.getMotherName())
                .motherPhone(student.getMotherPhone())
                .bloodGroup(student.getBloodGroup())
                .isActive(student.getIsActive())
                .build();
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ClassSectionResponse {
        private Long id;
        private String className;
        private String sectionName;

        public static ClassSectionResponse fromEntity(com.schoolmanagement.entity.ClassSection cs) {
            return ClassSectionResponse.builder()
                    .id(cs.getId())
                    .className(cs.getClassEntity().getClassName())
                    .sectionName(cs.getSection().getSectionName())
                    .build();
        }
    }
}