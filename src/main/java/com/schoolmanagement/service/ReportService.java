package com.schoolmanagement.service;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.schoolmanagement.dto.StudentResponse;
import com.schoolmanagement.entity.Student;
import com.schoolmanagement.entity.Attendance;
import com.schoolmanagement.entity.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Service for generating PDF reports
 * File: backend/src/main/java/com/schoolmanagement/service/ReportService.java
 */
@Service
@RequiredArgsConstructor
public class ReportService {

    private static final Font TITLE_FONT = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
    private static final Font HEADER_FONT = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
    private static final Font NORMAL_FONT = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL);

    /**
     * Generate Student List PDF Report
     */
    public byte[] generateStudentListPDF(List<Student> students, String schoolName) throws DocumentException {
        Document document = new Document(PageSize.A4.rotate());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, baos);

        document.open();

        // Add Title
        Paragraph title = new Paragraph("STUDENT LIST REPORT", TITLE_FONT);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(10f);
        document.add(title);

        // Add School Name
        Paragraph school = new Paragraph(schoolName, HEADER_FONT);
        school.setAlignment(Element.ALIGN_CENTER);
        school.setSpacingAfter(5f);
        document.add(school);

        // Add Date
        Paragraph date = new Paragraph("Generated on: " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")), NORMAL_FONT);
        date.setAlignment(Element.ALIGN_CENTER);
        date.setSpacingAfter(20f);
        document.add(date);

        // Create Table
        PdfPTable table = new PdfPTable(8);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);
        table.setSpacingAfter(10f);

        // Set Column Widths
        float[] columnWidths = {1f, 2f, 2f, 1.5f, 1f, 2f, 1.5f, 1.5f};
        table.setWidths(columnWidths);

        // Add Headers
        addTableHeader(table, new String[]{
                "S.No", "Admission No", "Student Name", "Class", "Roll No",
                "Father Name", "Phone", "Blood Group"
        });

        // Add Data
        int serialNo = 1;
        for (Student student : students) {
            addTableCell(table, String.valueOf(serialNo++));
            addTableCell(table, student.getAdmissionNumber());
            addTableCell(table, student.getUser().getFirstName() + " " +
                    (student.getUser().getLastName() != null ? student.getUser().getLastName() : ""));
            addTableCell(table, student.getClassSection().getClassEntity().getClassName() + " " +
                    student.getClassSection().getSection().getSectionName());
            addTableCell(table, student.getRollNumber() != null ? String.valueOf(student.getRollNumber()) : "-");
            addTableCell(table, student.getFatherName() != null ? student.getFatherName() : "-");
            addTableCell(table, student.getFatherPhone() != null ? student.getFatherPhone() : "-");
            addTableCell(table, student.getBloodGroup() != null ? student.getBloodGroup() : "-");
        }

        document.add(table);

        // Add Footer
        Paragraph footer = new Paragraph("Total Students: " + students.size(), HEADER_FONT);
        footer.setAlignment(Element.ALIGN_RIGHT);
        document.add(footer);

        document.close();
        return baos.toByteArray();
    }

    /**
     * Generate Individual Student Report Card PDF
     */
    public byte[] generateStudentReportCardPDF(StudentResponse student, List<Result> results,
                                               Double attendancePercentage) throws DocumentException {
        Document document = new Document(PageSize.A4);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, baos);

        document.open();

        // Add School Header
        Paragraph title = new Paragraph("STUDENT REPORT CARD", TITLE_FONT);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(20f);
        document.add(title);

        // Student Details Table
        PdfPTable detailsTable = new PdfPTable(2);
        detailsTable.setWidthPercentage(100);
        detailsTable.setSpacingAfter(20f);

        addDetailRow(detailsTable, "Student Name:",
                student.getUser().getFirstName() + " " +
                        (student.getUser().getLastName() != null ? student.getUser().getLastName() : ""));
        addDetailRow(detailsTable, "Admission Number:", student.getAdmissionNumber());
        addDetailRow(detailsTable, "Class:",
                student.getClassSection().getClassName() + " " +
                        student.getClassSection().getSectionName());
        addDetailRow(detailsTable, "Roll Number:",
                student.getRollNumber() != null ? String.valueOf(student.getRollNumber()) : "-");
        addDetailRow(detailsTable, "Attendance:", String.format("%.2f%%", attendancePercentage));

        document.add(detailsTable);

        // Results Table
        if (results != null && !results.isEmpty()) {
            Paragraph resultTitle = new Paragraph("Academic Performance", HEADER_FONT);
            resultTitle.setSpacingAfter(10f);
            document.add(resultTitle);

            PdfPTable resultTable = new PdfPTable(5);
            resultTable.setWidthPercentage(100);
            float[] resultWidths = {3f, 1.5f, 1.5f, 1.5f, 1.5f};
            resultTable.setWidths(resultWidths);

            addTableHeader(resultTable, new String[]{
                    "Subject", "Max Marks", "Obtained", "Percentage", "Grade"
            });

            double totalMax = 0;
            double totalObtained = 0;

            for (Result result : results) {
                addTableCell(resultTable, result.getExamSubject().getSubject().getSubjectName());
                addTableCell(resultTable, String.valueOf(result.getExamSubject().getMaxMarks()));
                addTableCell(resultTable, String.valueOf(result.getMarksObtained()));

                double percentage = (result.getMarksObtained().doubleValue() /
                        result.getExamSubject().getMaxMarks().doubleValue()) * 100;
                addTableCell(resultTable, String.format("%.2f%%", percentage));
                addTableCell(resultTable, result.getGrade() != null ? result.getGrade() : "-");

                totalMax += result.getExamSubject().getMaxMarks().doubleValue();
                totalObtained += result.getMarksObtained().doubleValue();
            }

            // Add Total Row
            PdfPCell totalLabel = new PdfPCell(new Phrase("TOTAL", HEADER_FONT));
            totalLabel.setHorizontalAlignment(Element.ALIGN_RIGHT);
            resultTable.addCell(totalLabel);
            addTableCell(resultTable, String.valueOf(totalMax));
            addTableCell(resultTable, String.valueOf(totalObtained));

            double overallPercentage = (totalObtained / totalMax) * 100;
            addTableCell(resultTable, String.format("%.2f%%", overallPercentage));
            addTableCell(resultTable, calculateGrade(overallPercentage));

            document.add(resultTable);
        }

        document.close();
        return baos.toByteArray();
    }

    /**
     * Generate Attendance Report PDF
     */
    public byte[] generateAttendanceReportPDF(List<Attendance> attendanceList,
                                              String studentName,
                                              LocalDate startDate,
                                              LocalDate endDate) throws DocumentException {
        Document document = new Document(PageSize.A4);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, baos);

        document.open();

        // Title
        Paragraph title = new Paragraph("ATTENDANCE REPORT", TITLE_FONT);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(20f);
        document.add(title);

        // Details
        PdfPTable detailsTable = new PdfPTable(2);
        detailsTable.setWidthPercentage(100);
        detailsTable.setSpacingAfter(20f);

        addDetailRow(detailsTable, "Student Name:", studentName);
        addDetailRow(detailsTable, "Period:",
                startDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) + " to " +
                        endDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));

        document.add(detailsTable);

        // Attendance Table
        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);

        addTableHeader(table, new String[]{"Date", "Status", "Remarks", "Marked By"});

        long presentCount = 0;
        long absentCount = 0;

        for (Attendance attendance : attendanceList) {
            addTableCell(table, attendance.getAttendanceDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
            addTableCell(table, attendance.getStatus().name());
            addTableCell(table, attendance.getRemarks() != null ? attendance.getRemarks() : "-");
            addTableCell(table, attendance.getMarkedBy() != null ?
                    attendance.getMarkedBy().getFirstName() : "-");

            if (attendance.getStatus().name().equals("PRESENT")) {
                presentCount++;
            } else if (attendance.getStatus().name().equals("ABSENT")) {
                absentCount++;
            }
        }

        document.add(table);

        // Summary
        double attendancePercentage = attendanceList.isEmpty() ? 0 :
                ((double) presentCount / attendanceList.size()) * 100;

        Paragraph summary = new Paragraph("\n\nSUMMARY", HEADER_FONT);
        summary.setSpacingAfter(10f);
        document.add(summary);

        Paragraph stats = new Paragraph(
                "Total Days: " + attendanceList.size() + "\n" +
                        "Present: " + presentCount + "\n" +
                        "Absent: " + absentCount + "\n" +
                        "Attendance Percentage: " + String.format("%.2f%%", attendancePercentage),
                NORMAL_FONT
        );
        document.add(stats);

        document.close();
        return baos.toByteArray();
    }

    // Helper Methods
    private void addTableHeader(PdfPTable table, String[] headers) {
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, HEADER_FONT));
            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(5);
            table.addCell(cell);
        }
    }

    private void addTableCell(PdfPTable table, String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text, NORMAL_FONT));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(5);
        table.addCell(cell);
    }

    private void addDetailRow(PdfPTable table, String label, String value) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, HEADER_FONT));
        labelCell.setBorder(Rectangle.NO_BORDER);
        labelCell.setPadding(5);
        table.addCell(labelCell);

        PdfPCell valueCell = new PdfPCell(new Phrase(value, NORMAL_FONT));
        valueCell.setBorder(Rectangle.NO_BORDER);
        valueCell.setPadding(5);
        table.addCell(valueCell);
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