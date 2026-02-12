package com.schoolmanagement.service;

import com.schoolmanagement.dto.AttendanceMarkRequest;
import com.schoolmanagement.entity.Attendance;
import com.schoolmanagement.entity.ClassSection;
import com.schoolmanagement.entity.User;
import com.schoolmanagement.repository.AttendanceRepository;
import com.schoolmanagement.repository.ClassSectionRepository;
import com.schoolmanagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final ClassSectionRepository classSectionRepository;
    private final UserRepository userRepository;

    @Transactional
    public void markAttendance(AttendanceMarkRequest request) {
        ClassSection classSection = classSectionRepository.findById(request.getClassSectionId())
                .orElseThrow(() -> new RuntimeException("Class section not found"));

        String currentUsername = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        User markedBy = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("Current user not found"));

        for (AttendanceMarkRequest.AttendanceItem item : request.getAttendanceList()) {
            User student = userRepository.findById(item.getStudentId())
                    .orElseThrow(() -> new RuntimeException("Student not found"));

            // Check if attendance already exists
            attendanceRepository.findByUserIdAndAttendanceDate(
                    student.getId(),
                    request.getAttendanceDate()
            ).ifPresent(existing -> {
                attendanceRepository.delete(existing);
            });

            Attendance attendance = Attendance.builder()
                    .school(classSection.getSchool())
                    .user(student)
                    .classSection(classSection)
                    .attendanceDate(request.getAttendanceDate())
                    .status(Attendance.AttendanceStatus.valueOf(item.getStatus()))
                    .remarks(item.getRemarks())
                    .markedBy(markedBy)
                    .build();

            attendanceRepository.save(attendance);
        }
    }

    @Transactional(readOnly = true)
    public List<Attendance> getStudentAttendance(Long studentId,
                                                 LocalDate startDate,
                                                 LocalDate endDate) {
        return attendanceRepository.findByUserIdAndAttendanceDateBetween(
                studentId, startDate, endDate
        );
    }
}