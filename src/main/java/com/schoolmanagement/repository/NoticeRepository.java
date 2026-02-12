package com.schoolmanagement.repository;

import com.schoolmanagement.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long> {
    List<Notice> findBySchoolIdAndIsActiveTrueOrderByPublishDateDesc(Long schoolId);
    List<Notice> findBySchoolIdAndPublishDateBeforeAndIsActiveTrue(
            Long schoolId, LocalDate date
    );
}