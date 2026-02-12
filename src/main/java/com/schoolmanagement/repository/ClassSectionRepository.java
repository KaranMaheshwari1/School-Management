package com.schoolmanagement.repository;

import com.schoolmanagement.entity.ClassSection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ClassSectionRepository extends JpaRepository<ClassSection, Long> {
    List<ClassSection> findBySchoolId(Long schoolId);
    List<ClassSection> findBySchoolIdAndAcademicYear(Long schoolId, String academicYear);
}