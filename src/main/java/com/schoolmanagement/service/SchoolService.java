package com.schoolmanagement.service;

import com.schoolmanagement.entity.School;
import com.schoolmanagement.repository.SchoolRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * School Service - Business logic for school management
 * File: backend/src/main/java/com/schoolmanagement/service/SchoolService.java
 */
@Service
@RequiredArgsConstructor
public class SchoolService {

    private final SchoolRepository schoolRepository;

    @Transactional(readOnly = true)
    public Page<School> getAllSchools(Pageable pageable) {
        return schoolRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public List<School> getAllActiveSchools() {
        return schoolRepository.findByIsActive(true);
    }

    @Transactional(readOnly = true)
    public School getSchoolById(Long id) {
        return schoolRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("School not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public School getSchoolByCode(String schoolCode) {
        return schoolRepository.findBySchoolCode(schoolCode)
                .orElseThrow(() -> new RuntimeException("School not found with code: " + schoolCode));
    }

    @Transactional
    public School createSchool(School school) {
        // Validate school code is unique
        if (schoolRepository.existsBySchoolCode(school.getSchoolCode())) {
            throw new RuntimeException("School code already exists: " + school.getSchoolCode());
        }

        // Set defaults
        if (school.getIsActive() == null) {
            school.setIsActive(true);
        }

        return schoolRepository.save(school);
    }

    @Transactional
    public School updateSchool(Long id, School schoolDetails) {
        School school = getSchoolById(id);

        // Update fields
        school.setSchoolName(schoolDetails.getSchoolName());
        school.setSchoolCode(schoolDetails.getSchoolCode());
        school.setEmail(schoolDetails.getEmail());
        school.setPhone(schoolDetails.getPhone());
        school.setWebsite(schoolDetails.getWebsite());
        school.setAddress(schoolDetails.getAddress());
        school.setCity(schoolDetails.getCity());
        school.setState(schoolDetails.getState());
      //  school.setCountry(schoolDetails.getCountry());
        school.setPincode(schoolDetails.getPincode());
        school.setEstablishedYear(schoolDetails.getEstablishedYear());
       // school.setBoard(schoolDetails.getBoard());

        return schoolRepository.save(school);
    }

    @Transactional
    public void updateBranding(Long id, String logoUrl, String primaryColor, String secondaryColor) {
        School school = getSchoolById(id);

        if (logoUrl != null) {
            school.setLogoUrl(logoUrl);
        }
        if (primaryColor != null) {
           // school.setPrimaryColor(primaryColor);
        }
        if (secondaryColor != null) {
           // school.setSecondaryColor(secondaryColor);
        }

        schoolRepository.save(school);
    }

    @Transactional
    public void updateModules(Long id, Boolean attendanceModule, Boolean examModule,
                              Boolean timetableModule, Boolean complaintModule,
                              Boolean eventModule) {
        School school = getSchoolById(id);

        if (attendanceModule != null) {
           // school.setAttendanceModule(attendanceModule);
        }
        if (examModule != null) {
           // school.setExamModule(examModule);
        }
        if (timetableModule != null) {
           // school.setTimetableModule(timetableModule);
        }
        if (complaintModule != null) {
          //  school.setComplaintModule(complaintModule);
        }
        if (eventModule != null) {
           // school.setEventModule(eventModule);
        }

        schoolRepository.save(school);
    }

    @Transactional
    public void toggleSchoolStatus(Long id) {
        School school = getSchoolById(id);
        school.setIsActive(!school.getIsActive());
        schoolRepository.save(school);
    }

    @Transactional
    public void deleteSchool(Long id) {
        School school = getSchoolById(id);
        schoolRepository.delete(school);
    }

    @Transactional(readOnly = true)
    public long countActiveSchools() {
        return schoolRepository.countByIsActive(true);
    }

    @Transactional(readOnly = true)
    public long countTotalSchools() {
        return schoolRepository.count();
    }
}