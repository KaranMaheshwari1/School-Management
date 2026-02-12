package com.schoolmanagement.controller;

import com.schoolmanagement.dto.ApiResponse;
import com.schoolmanagement.dto.StudentResponse;
import com.schoolmanagement.entity.Attendance;
import com.schoolmanagement.entity.Result;
import com.schoolmanagement.entity.Student;
import com.schoolmanagement.repository.AttendanceRepository;
import com.schoolmanagement.service.ExcelExportService;
import com.schoolmanagement.service.ReportService;
import com.schoolmanagement.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * NEW Controller for generating and downloading reports
 * File: backend/src/main/java/com/schoolmanagement/controller/ReportController.java
 */
@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class ReportController {

    private final ReportService reportService;
    private final ExcelExportService excelExportService;
    private final StudentService studentService;
    private final AttendanceRepository attendanceRepository;

    /**
     * Export all students to PDF
     */
    @GetMapping("/students/pdf")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'PRINCIPAL', 'TEACHER')")
    public ResponseEntity<byte[]> exportStudentsPDF(
            @RequestParam Long schoolId,
            @RequestParam(required = false) Long classSectionId,
            @RequestParam(required = false) String searchTerm) {

        try {
            List<Student> students;

            if (searchTerm != null && !searchTerm.trim().isEmpty()) {
                students = studentService.searchStudentsForExport(schoolId, searchTerm);
            } else {
                students = studentService.getAllStudentsForExport(schoolId, classSectionId);
            }

            String schoolName = students.isEmpty() ? "School" :
                    students.get(0).getSchool().getSchoolName();

            byte[] pdfBytes = reportService.generateStudentListPDF(students, schoolName);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment",
                    "students_report_" + LocalDate.now() + ".pdf");

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Export all students to Excel
     */
    @GetMapping("/students/excel")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'PRINCIPAL', 'TEACHER')")
    public ResponseEntity<byte[]> exportStudentsExcel(
            @RequestParam Long schoolId,
            @RequestParam(required = false) Long classSectionId,
            @RequestParam(required = false) String searchTerm) {

        try {
            List<Student> students;

            if (searchTerm != null && !searchTerm.trim().isEmpty()) {
                students = studentService.searchStudentsForExport(schoolId, searchTerm);
            } else {
                students = studentService.getAllStudentsForExport(schoolId, classSectionId);
            }

            String schoolName = students.isEmpty() ? "School" :
                    students.get(0).getSchool().getSchoolName();

            byte[] excelBytes = excelExportService.exportStudentsToExcel(students, schoolName);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment",
                    "students_report_" + LocalDate.now() + ".xlsx");

            return new ResponseEntity<>(excelBytes, headers, HttpStatus.OK);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Export individual student report card to PDF
     */
    @GetMapping("/students/{studentId}/report-card/pdf")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'PRINCIPAL', 'TEACHER', 'STUDENT', 'PARENT')")
    public ResponseEntity<byte[]> exportStudentReportCardPDF(@PathVariable Long studentId) {

        try {
            // Get student
            StudentResponse student = studentService.getStudentById(studentId);

            // Get attendance percentage (simplified - you can enhance this)
            List<Attendance> attendanceList = attendanceRepository
                    .findByUserIdAndAttendanceDateBetween(
                            student.getUser().getId(),
                            LocalDate.now().minusMonths(3),
                            LocalDate.now()
                    );

            long presentCount = attendanceList.stream()
                    .filter(a -> a.getStatus().name().equals("PRESENT"))
                    .count();

            double attendancePercentage = attendanceList.isEmpty() ? 0 :
                    ((double) presentCount / attendanceList.size()) * 100;

            // Get results (you need to implement this based on your Result entity)
            List<Result> results = null; // resultRepository.findByStudentId(studentId);

            byte[] pdfBytes = reportService.generateStudentReportCardPDF(
                    student, results, attendancePercentage
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment",
                    "report_card_" + student.getAdmissionNumber() + ".pdf");

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Export student attendance to PDF
     */
    @GetMapping("/students/{studentId}/attendance/pdf")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'PRINCIPAL', 'TEACHER', 'STUDENT', 'PARENT')")
    public ResponseEntity<byte[]> exportAttendancePDF(
            @PathVariable Long studentId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        try {
            List<Attendance> attendanceList = attendanceRepository
                    .findByUserIdAndAttendanceDateBetween(studentId, startDate, endDate);

            String studentName = attendanceList.isEmpty() ? "Student" :
                    attendanceList.get(0).getUser().getFirstName() + " " +
                            (attendanceList.get(0).getUser().getLastName() != null ?
                                    attendanceList.get(0).getUser().getLastName() : "");

            byte[] pdfBytes = reportService.generateAttendanceReportPDF(
                    attendanceList, studentName, startDate, endDate
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment",
                    "attendance_report_" + LocalDate.now() + ".pdf");

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Export student attendance to Excel
     */
    @GetMapping("/students/{studentId}/attendance/excel")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'PRINCIPAL', 'TEACHER', 'STUDENT', 'PARENT')")
    public ResponseEntity<byte[]> exportAttendanceExcel(
            @PathVariable Long studentId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        try {
            List<Attendance> attendanceList = attendanceRepository
                    .findByUserIdAndAttendanceDateBetween(studentId, startDate, endDate);

            String studentName = attendanceList.isEmpty() ? "Student" :
                    attendanceList.get(0).getUser().getFirstName() + " " +
                            (attendanceList.get(0).getUser().getLastName() != null ?
                                    attendanceList.get(0).getUser().getLastName() : "");

            byte[] excelBytes = excelExportService.exportAttendanceToExcel(
                    attendanceList, studentName, startDate, endDate
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment",
                    "attendance_report_" + LocalDate.now() + ".xlsx");

            return new ResponseEntity<>(excelBytes, headers, HttpStatus.OK);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get report generation status
     */
    @GetMapping("/status")
    public ResponseEntity<ApiResponse<String>> getReportStatus() {
        return ResponseEntity.ok(ApiResponse.success("Report service is running"));
    }
}