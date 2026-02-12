package com.schoolmanagement.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class AttendanceMarkRequest {

    @NotNull(message = "Class section ID is required")
    private Long classSectionId;

    @NotNull(message = "Attendance date is required")
    private LocalDate attendanceDate;

    @NotNull(message = "Attendance list is required")
    private List<AttendanceItem> attendanceList;

    @Data
    public static class AttendanceItem {
        @NotNull(message = "Student ID is required")
        private Long studentId;

        @NotNull(message = "Status is required")
        private String status; // PRESENT, ABSENT, LATE, HALF_DAY, LEAVE

        private String remarks;
    }
}