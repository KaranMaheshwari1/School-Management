package com.schoolmanagement.repository;

import com.schoolmanagement.entity.Result;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * NEW: Result Repository
 * File: backend/src/main/java/com/schoolmanagement/repository/ResultRepository.java
 */
@Repository
public interface ResultRepository extends JpaRepository<Result, Long> {

    List<Result> findByStudentId(Long studentId);

    List<Result> findByExamId(Long examId);

    @Query("SELECT r FROM Result r WHERE r.student.id = :studentId AND r.exam.id = :examId")
    List<Result> findByStudentIdAndExamId(@Param("studentId") Long studentId,
                                          @Param("examId") Long examId);

    @Query("SELECT r FROM Result r WHERE r.exam.id = :examId AND r.examSubject.id = :examSubjectId")
    List<Result> findByExamIdAndExamSubjectId(@Param("examId") Long examId,
                                              @Param("examSubjectId") Long examSubjectId);
}