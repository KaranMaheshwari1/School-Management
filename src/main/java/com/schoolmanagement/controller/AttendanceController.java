package com.schoolmanagement.controller;

import com.schoolmanagement.dto.AttendanceMarkRequest;
import com.schoolmanagement.dto.ApiResponse;
import com.schoolmanagement.entity.Attendance;
import com.schoolmanagement.service.AttendanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/attendance")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class AttendanceController {

    private final AttendanceService attendanceService;

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'PRINCIPAL', 'TEACHER')")
    public ResponseEntity<ApiResponse<Void>> markAttendance(
            @Valid @RequestBody AttendanceMarkRequest request) {
        attendanceService.markAttendance(request);
        return ResponseEntity.ok(ApiResponse.success("Attendance marked successfully", null));
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<ApiResponse<List<Attendance>>> getStudentAttendance(
            @PathVariable Long studentId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        List<Attendance> attendance = attendanceService.getStudentAttendance(
                studentId, startDate, endDate
        );
        return ResponseEntity.ok(ApiResponse.success(attendance));
    }
}