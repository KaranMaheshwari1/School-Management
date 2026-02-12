package com.schoolmanagement.repository;

import com.schoolmanagement.entity.Exam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ExamRepository extends JpaRepository<Exam, Long> {
    List<Exam> findBySchoolId(Long schoolId);
    List<Exam> findByClassSectionId(Long classSectionId);
    List<Exam> findBySchoolIdAndAcademicYear(Long schoolId, String academicYear);
}