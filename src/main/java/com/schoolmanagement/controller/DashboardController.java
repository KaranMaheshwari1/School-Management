package com.schoolmanagement.controller;

import com.schoolmanagement.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class DashboardController {

    @GetMapping("/principal")
    @PreAuthorize("hasRole('PRINCIPAL')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getPrincipalDashboard(
            @RequestParam Long schoolId) {

        Map<String, Object> dashboard = new HashMap<>();

        // Statistics
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalStudents", 450);
        stats.put("totalTeachers", 25);
        stats.put("totalClasses", 30);
        stats.put("averageAttendance", 92.5);
        dashboard.put("statistics", stats);

        // Today's attendance
        Map<String, Object> todayAttendance = new HashMap<>();
        todayAttendance.put("present", 425);
        todayAttendance.put("absent", 25);
        todayAttendance.put("percentage", 94.4);
        dashboard.put("todayAttendance", todayAttendance);

        return ResponseEntity.ok(ApiResponse.success(dashboard));
    }

    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getStudentDashboard(
            @PathVariable Long studentId) {

        Map<String, Object> dashboard = new HashMap<>();
        dashboard.put("studentId", studentId);
        dashboard.put("attendancePercentage", 92.5);
        dashboard.put("recentResults", new HashMap<>());

        return ResponseEntity.ok(ApiResponse.success(dashboard));
    }
}