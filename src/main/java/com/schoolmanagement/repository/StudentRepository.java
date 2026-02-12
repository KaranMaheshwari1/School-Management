package com.schoolmanagement.repository;

import com.schoolmanagement.entity.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * UPDATED: Added search and filter methods
 * File: backend/src/main/java/com/schoolmanagement/repository/StudentRepository.java
 */
@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    // Existing methods
    Optional<Student> findByAdmissionNumber(String admissionNumber);
    List<Student> findBySchoolId(Long schoolId);
    List<Student> findByClassSectionId(Long classSectionId);
    Boolean existsByAdmissionNumber(String admissionNumber);

    // NEW: Paginated version
    Page<Student> findBySchoolId(Long schoolId, Pageable pageable);
    Page<Student> findByClassSectionId(Long classSectionId, Pageable pageable);

    // NEW: Search by name or admission number
    @Query("SELECT s FROM Student s WHERE s.school.id = :schoolId AND " +
            "(LOWER(s.user.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(s.user.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(s.admissionNumber) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    Page<Student> searchStudents(@Param("schoolId") Long schoolId,
                                 @Param("searchTerm") String searchTerm,
                                 Pageable pageable);

    // NEW: Advanced search with multiple filters
    @Query("SELECT s FROM Student s WHERE s.school.id = :schoolId " +
            "AND (:searchTerm IS NULL OR " +
            "LOWER(s.user.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(s.user.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(s.admissionNumber) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
            "AND (:classSectionId IS NULL OR s.classSection.id = :classSectionId) " +
            "AND (:isActive IS NULL OR s.isActive = :isActive)")
    Page<Student> advancedSearch(@Param("schoolId") Long schoolId,
                                 @Param("searchTerm") String searchTerm,
                                 @Param("classSectionId") Long classSectionId,
                                 @Param("isActive") Boolean isActive,
                                 Pageable pageable);

    // NEW: Find students by admission date range
    @Query("SELECT s FROM Student s WHERE s.school.id = :schoolId " +
            "AND s.admissionDate BETWEEN :startDate AND :endDate")
    List<Student> findByAdmissionDateRange(@Param("schoolId") Long schoolId,
                                           @Param("startDate") LocalDate startDate,
                                           @Param("endDate") LocalDate endDate);

    // NEW: Get all students for a school (for export)
    @Query("SELECT s FROM Student s WHERE s.school.id = :schoolId ORDER BY s.classSection.id, s.rollNumber")
    List<Student> findAllBySchoolIdOrdered(@Param("schoolId") Long schoolId);

    // NEW: Find by class and section
    @Query("SELECT s FROM Student s WHERE s.school.id = :schoolId " +
            "AND s.classSection.classEntity.id = :classId " +
            "AND s.classSection.section.id = :sectionId")
    List<Student> findByClassAndSection(@Param("schoolId") Long schoolId,
                                        @Param("classId") Long classId,
                                        @Param("sectionId") Long sectionId);
}