package com.schoolmanagement.controller;

import com.schoolmanagement.dto.ApiResponse;
import com.schoolmanagement.dto.PageResponse;
import com.schoolmanagement.entity.School;
import com.schoolmanagement.service.SchoolService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * School Controller - REST API endpoints for school management
 * File: backend/src/main/java/com/schoolmanagement/controller/SchoolController.java
 */
@RestController
@RequestMapping("/schools")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class SchoolController {

    private final SchoolService schoolService;

    /**
     * Get all schools with pagination
     */
    @GetMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<PageResponse<School>>> getAllSchools(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<School> schoolPage = schoolService.getAllSchools(pageable);
        PageResponse<School> response = PageResponse.fromPage(schoolPage);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Get all active schools (for dropdowns)
     */
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<School>>> getAllActiveSchools() {
        List<School> schools = schoolService.getAllActiveSchools();
        return ResponseEntity.ok(ApiResponse.success(schools));
    }

    /**
     * Get school by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<School>> getSchoolById(@PathVariable Long id) {
        School school = schoolService.getSchoolById(id);
        return ResponseEntity.ok(ApiResponse.success(school));
    }

    /**
     * Create new school
     */
    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<School>> createSchool(@Valid @RequestBody School school) {
        School createdSchool = schoolService.createSchool(school);
        return ResponseEntity.ok(ApiResponse.success("School created successfully", createdSchool));
    }

    /**
     * Update school
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'PRINCIPAL')")
    public ResponseEntity<ApiResponse<School>> updateSchool(
            @PathVariable Long id,
            @Valid @RequestBody School school) {
        School updatedSchool = schoolService.updateSchool(id, school);
        return ResponseEntity.ok(ApiResponse.success("School updated successfully", updatedSchool));
    }

    /**
     * Update school branding (logo, colors)
     */
    @PatchMapping("/{id}/branding")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'PRINCIPAL')")
    public ResponseEntity<ApiResponse<Void>> updateBranding(
            @PathVariable Long id,
            @RequestParam(required = false) String logoUrl,
            @RequestParam(required = false) String primaryColor,
            @RequestParam(required = false) String secondaryColor) {

        schoolService.updateBranding(id, logoUrl, primaryColor, secondaryColor);
        return ResponseEntity.ok(ApiResponse.success("Branding updated successfully", null));
    }

    /**
     * Update school modules (enable/disable features)
     */
    @PatchMapping("/{id}/modules")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'PRINCIPAL')")
    public ResponseEntity<ApiResponse<Void>> updateModules(
            @PathVariable Long id,
            @RequestParam(required = false) Boolean attendanceModule,
            @RequestParam(required = false) Boolean examModule,
            @RequestParam(required = false) Boolean timetableModule,
            @RequestParam(required = false) Boolean complaintModule,
            @RequestParam(required = false) Boolean eventModule) {

        schoolService.updateModules(id, attendanceModule, examModule,
                timetableModule, complaintModule, eventModule);
        return ResponseEntity.ok(ApiResponse.success("Modules updated successfully", null));
    }

    /**
     * Toggle school active status
     */
    @PatchMapping("/{id}/toggle-status")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> toggleSchoolStatus(@PathVariable Long id) {
        schoolService.toggleSchoolStatus(id);
        return ResponseEntity.ok(ApiResponse.success("School status toggled successfully", null));
    }

    /**
     * Delete school
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteSchool(@PathVariable Long id) {
        schoolService.deleteSchool(id);
        return ResponseEntity.ok(ApiResponse.success("School deleted successfully", null));
    }

    /**
     * Get school statistics
     */
    @GetMapping("/statistics")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getSchoolStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalSchools", schoolService.countTotalSchools());
        stats.put("activeSchools", schoolService.countActiveSchools());
        stats.put("inactiveSchools", schoolService.countTotalSchools() - schoolService.countActiveSchools());

        return ResponseEntity.ok(ApiResponse.success(stats));
    }
}