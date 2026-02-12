package com.schoolmanagement.repository;

import com.schoolmanagement.entity.School;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * School Repository - Data access layer for School entity
 * File: backend/src/main/java/com/schoolmanagement/repository/SchoolRepository.java
 */
@Repository
public interface SchoolRepository extends JpaRepository<School, Long> {

    /**
     * Find school by school code
     */
    Optional<School> findBySchoolCode(String schoolCode);

    /**
     * Check if school code exists
     */
    Boolean existsBySchoolCode(String schoolCode);

    /**
     * Find all active schools
     */
    List<School> findByIsActive(Boolean isActive);

    /**
     * Find schools by city
     */
    List<School> findByCity(String city);

    /**
     * Find schools by state
     */
    List<School> findByState(String state);

    /**
     * Find schools by board
     */
    List<School> findByBoard(String board);

    /**
     * Search schools by name
     */
    @Query("SELECT s FROM School s WHERE LOWER(s.schoolName) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<School> searchByName(@Param("searchTerm") String searchTerm);

    /**
     * Find schools with a specific module enabled
     */
    @Query("SELECT s FROM School s WHERE s.attendanceModule = true")
    List<School> findSchoolsWithAttendanceModule();

    @Query("SELECT s FROM School s WHERE s.examModule = true")
    List<School> findSchoolsWithExamModule();

    @Query("SELECT s FROM School s WHERE s.timetableModule = true")
    List<School> findSchoolsWithTimetableModule();

    /**
     * Count active schools
     */
    Long countByIsActive(Boolean isActive);

    /**
     * Find schools established in a specific year
     */
    List<School> findByEstablishedYear(Integer year);

    /**
     * Find schools by country
     */
    List<School> findByCountry(String country);

    /**
     * Advanced search with multiple criteria
     */
    @Query("SELECT s FROM School s WHERE " +
            "(:searchTerm IS NULL OR LOWER(s.schoolName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(s.schoolCode) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
            "AND (:city IS NULL OR s.city = :city) " +
            "AND (:state IS NULL OR s.state = :state) " +
            "AND (:isActive IS NULL OR s.isActive = :isActive)")
    List<School> advancedSearch(@Param("searchTerm") String searchTerm,
                                @Param("city") String city,
                                @Param("state") String state,
                                @Param("isActive") Boolean isActive);

    /**
     * Find schools by email
     */
    Optional<School> findByEmail(String email);

    /**
     * Check if email exists
     */
    Boolean existsByEmail(String email);
}