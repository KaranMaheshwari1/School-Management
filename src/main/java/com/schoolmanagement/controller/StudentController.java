package com.schoolmanagement.controller;

import com.schoolmanagement.dto.StudentCreateRequest;
import com.schoolmanagement.dto.ApiResponse;
import com.schoolmanagement.dto.PageResponse;
import com.schoolmanagement.dto.StudentResponse;
import com.schoolmanagement.service.StudentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * UPDATED: Added search endpoint with filters
 * File: backend/src/main/java/com/schoolmanagement/controller/StudentController.java
 */
@RestController
@RequestMapping("/students")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class StudentController {

    private final StudentService studentService;

    /**
     * UPDATED: Get all students with pagination
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'PRINCIPAL', 'TEACHER')")
    public ResponseEntity<ApiResponse<PageResponse<StudentResponse>>> getAllStudents(
            @RequestParam(required = false) Long schoolId,
            @RequestParam(required = false) Long classSectionId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        PageResponse<StudentResponse> response = studentService.getAllStudents(
                schoolId, classSectionId, page, size
        );
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * NEW: Search students with filters
     */
    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'PRINCIPAL', 'TEACHER')")
    public ResponseEntity<ApiResponse<PageResponse<StudentResponse>>> searchStudents(
            @RequestParam Long schoolId,
            @RequestParam(required = false) String searchTerm,
            @RequestParam(required = false) Long classSectionId,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        PageResponse<StudentResponse> response = studentService.searchStudents(
                schoolId, searchTerm, classSectionId, isActive, page, size, sortBy, sortDir
        );
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<StudentResponse>> getStudentById(@PathVariable Long id) {
        StudentResponse response = studentService.getStudentById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'PRINCIPAL')")
    public ResponseEntity<ApiResponse<StudentResponse>> createStudent(
            @Valid @RequestBody StudentCreateRequest request) {
        StudentResponse response = studentService.createStudent(request);
        return ResponseEntity.ok(ApiResponse.success("Student created successfully", response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'PRINCIPAL')")
    public ResponseEntity<ApiResponse<Void>> deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return ResponseEntity.ok(ApiResponse.success("Student deleted successfully", null));
    }
}