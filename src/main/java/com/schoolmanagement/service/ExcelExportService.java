package com.schoolmanagement.service;

import com.schoolmanagement.entity.Student;
import com.schoolmanagement.entity.Attendance;
import com.schoolmanagement.entity.Result;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Service for generating Excel reports
 * File: backend/src/main/java/com/schoolmanagement/service/ExcelExportService.java
 */
@Service
@RequiredArgsConstructor
public class ExcelExportService {

    /**
     * Export Students to Excel
     */
    public byte[] exportStudentsToExcel(List<Student> students, String schoolName) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Students");

        // Create Header Style
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle dataStyle = createDataStyle(workbook);

        // Add Title Row
        Row titleRow = sheet.createRow(0);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("STUDENT LIST - " + schoolName);
        CellStyle titleStyle = workbook.createCellStyle();
        Font titleFont = workbook.createFont();
        titleFont.setBold(true);
        titleFont.setFontHeightInPoints((short) 16);
        titleStyle.setFont(titleFont);
        titleCell.setCellStyle(titleStyle);

        // Add Date Row
        Row dateRow = sheet.createRow(1);
        Cell dateCell = dateRow.createCell(0);
        dateCell.setCellValue("Generated on: " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));

        // Create Header Row
        Row headerRow = sheet.createRow(3);
        String[] headers = {
                "S.No", "Admission Number", "Student Name", "Class", "Section",
                "Roll Number", "Father Name", "Father Phone", "Mother Name",
                "Mother Phone", "Blood Group", "Email", "Phone", "Address", "Status"
        };

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // Add Data Rows
        int rowNum = 4;
        int serialNo = 1;
        for (Student student : students) {
            Row row = sheet.createRow(rowNum++);

            createCell(row, 0, serialNo++, dataStyle);
            createCell(row, 1, student.getAdmissionNumber(), dataStyle);
            createCell(row, 2, student.getUser().getFirstName() + " " +
                    (student.getUser().getLastName() != null ? student.getUser().getLastName() : ""), dataStyle);
            createCell(row, 3, student.getClassSection().getClassEntity().getClassName(), dataStyle);
            createCell(row, 4, student.getClassSection().getSection().getSectionName(), dataStyle);
            createCell(row, 5, student.getRollNumber() != null ? student.getRollNumber() : "", dataStyle);
            createCell(row, 6, student.getFatherName() != null ? student.getFatherName() : "", dataStyle);
            createCell(row, 7, student.getFatherPhone() != null ? student.getFatherPhone() : "", dataStyle);
            createCell(row, 8, student.getMotherName() != null ? student.getMotherName() : "", dataStyle);
            createCell(row, 9, student.getMotherPhone() != null ? student.getMotherPhone() : "", dataStyle);
            createCell(row, 10, student.getBloodGroup() != null ? student.getBloodGroup() : "", dataStyle);
            createCell(row, 11, student.getUser().getEmail(), dataStyle);
            createCell(row, 12, student.getUser().getPhone() != null ? student.getUser().getPhone() : "", dataStyle);
            createCell(row, 13, student.getUser().getAddress() != null ? student.getUser().getAddress() : "", dataStyle);
            createCell(row, 14, student.getIsActive() ? "Active" : "Inactive", dataStyle);
        }

        // Auto-size columns
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        // Write to ByteArray
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        workbook.write(baos);
        workbook.close();

        return baos.toByteArray();
    }

    /**
     * Export Attendance to Excel
     */
    public byte[] exportAttendanceToExcel(List<Attendance> attendanceList,
                                          String studentName,
                                          LocalDate startDate,
                                          LocalDate endDate) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Attendance");

        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle dataStyle = createDataStyle(workbook);

        // Title
        Row titleRow = sheet.createRow(0);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("ATTENDANCE REPORT");
        CellStyle titleStyle = workbook.createCellStyle();
        Font titleFont = workbook.createFont();
        titleFont.setBold(true);
        titleFont.setFontHeightInPoints((short) 14);
        titleStyle.setFont(titleFont);
        titleCell.setCellStyle(titleStyle);

        // Details
        Row studentRow = sheet.createRow(1);
        studentRow.createCell(0).setCellValue("Student Name:");
        studentRow.createCell(1).setCellValue(studentName);

        Row periodRow = sheet.createRow(2);
        periodRow.createCell(0).setCellValue("Period:");
        periodRow.createCell(1).setCellValue(
                startDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) + " to " +
                        endDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
        );

        // Headers
        Row headerRow = sheet.createRow(4);
        String[] headers = {"Date", "Day", "Status", "Remarks", "Marked By"};

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // Data
        int rowNum = 5;
        long presentCount = 0;
        long absentCount = 0;

        for (Attendance attendance : attendanceList) {
            Row row = sheet.createRow(rowNum++);

            createCell(row, 0, attendance.getAttendanceDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")), dataStyle);
            createCell(row, 1, attendance.getAttendanceDate().getDayOfWeek().toString(), dataStyle);
            createCell(row, 2, attendance.getStatus().name(), dataStyle);
            createCell(row, 3, attendance.getRemarks() != null ? attendance.getRemarks() : "", dataStyle);
            createCell(row, 4, attendance.getMarkedBy() != null ?
                    attendance.getMarkedBy().getFirstName() : "", dataStyle);

            if (attendance.getStatus().name().equals("PRESENT")) {
                presentCount++;
            } else if (attendance.getStatus().name().equals("ABSENT")) {
                absentCount++;
            }
        }

        // Summary
        Row summaryTitleRow = sheet.createRow(rowNum + 2);
        Cell summaryCell = summaryTitleRow.createCell(0);
        summaryCell.setCellValue("SUMMARY");
        summaryCell.setCellStyle(headerStyle);

        double attendancePercentage = attendanceList.isEmpty() ? 0 :
                ((double) presentCount / attendanceList.size()) * 100;

        sheet.createRow(rowNum + 3).createCell(0).setCellValue("Total Days: " + attendanceList.size());
        sheet.createRow(rowNum + 4).createCell(0).setCellValue("Present: " + presentCount);
        sheet.createRow(rowNum + 5).createCell(0).setCellValue("Absent: " + absentCount);
        sheet.createRow(rowNum + 6).createCell(0).setCellValue("Attendance %: " + String.format("%.2f%%", attendancePercentage));

        // Auto-size
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        workbook.write(baos);
        workbook.close();

        return baos.toByteArray();
    }

    /**
     * Export Results to Excel
     */
    public byte[] exportResultsToExcel(List<Result> results, String studentName, String examName) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Results");

        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle dataStyle = createDataStyle(workbook);

        // Title
        Row titleRow = sheet.createRow(0);
        titleRow.createCell(0).setCellValue("EXAM RESULTS - " + examName);

        Row studentRow = sheet.createRow(1);
        studentRow.createCell(0).setCellValue("Student: " + studentName);

        // Headers
        Row headerRow = sheet.createRow(3);
        String[] headers = {"Subject", "Max Marks", "Obtained Marks", "Percentage", "Grade", "Remarks"};

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // Data
        int rowNum = 4;
        double totalMax = 0;
        double totalObtained = 0;

        for (Result result : results) {
            Row row = sheet.createRow(rowNum++);

            double percentage = (result.getMarksObtained().doubleValue() /
                    result.getExamSubject().getMaxMarks().doubleValue()) * 100;

            createCell(row, 0, result.getExamSubject().getSubject().getSubjectName(), dataStyle);
            createCell(row, 1, result.getExamSubject().getMaxMarks().doubleValue(), dataStyle);
            createCell(row, 2, result.getMarksObtained().doubleValue(), dataStyle);
            createCell(row, 3, String.format("%.2f%%", percentage), dataStyle);
            createCell(row, 4, result.getGrade() != null ? result.getGrade() : "", dataStyle);
            createCell(row, 5, result.getRemarks() != null ? result.getRemarks() : "", dataStyle);

            totalMax += result.getExamSubject().getMaxMarks().doubleValue();
            totalObtained += result.getMarksObtained().doubleValue();
        }

        // Total Row
        Row totalRow = sheet.createRow(rowNum);
        Cell totalLabelCell = totalRow.createCell(0);
        totalLabelCell.setCellValue("TOTAL");
        totalLabelCell.setCellStyle(headerStyle);

        createCell(totalRow, 1, totalMax, headerStyle);
        createCell(totalRow, 2, totalObtained, headerStyle);

        double overallPercentage = (totalObtained / totalMax) * 100;
        createCell(totalRow, 3, String.format("%.2f%%", overallPercentage), headerStyle);
        createCell(totalRow, 4, calculateGrade(overallPercentage), headerStyle);

        // Auto-size
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        workbook.write(baos);
        workbook.close();

        return baos.toByteArray();
    }

    // Helper Methods
    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 11);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }

    private CellStyle createDataStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }

    private void createCell(Row row, int column, Object value, CellStyle style) {
        Cell cell = row.createCell(column);

        if (value instanceof String) {
            cell.setCellValue((String) value);
        } else if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        } else if (value instanceof Double) {
            cell.setCellValue((Double) value);
        } else if (value instanceof Long) {
            cell.setCellValue((Long) value);
        } else {
            cell.setCellValue(value != null ? value.toString() : "");
        }

        cell.setCellStyle(style);
    }

    private String calculateGrade(double percentage) {
        if (percentage >= 90) return "A+";
        if (percentage >= 80) return "A";
        if (percentage >= 70) return "B+";
        if (percentage >= 60) return "B";
        if (percentage >= 50) return "C";
        if (percentage >= 40) return "D";
        return "F";
    }
}