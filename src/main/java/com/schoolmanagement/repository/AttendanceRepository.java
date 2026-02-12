package com.schoolmanagement.repository;

import com.schoolmanagement.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    List<Attendance> findByUserIdAndAttendanceDateBetween(
            Long userId, LocalDate startDate, LocalDate endDate
    );
    List<Attendance> findByClassSectionIdAndAttendanceDate(
            Long classSectionId, LocalDate date
    );
    Optional<Attendance> findByUserIdAndAttendanceDate(Long userId, LocalDate date);
}