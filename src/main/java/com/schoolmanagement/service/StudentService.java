package com.schoolmanagement.service;

import com.schoolmanagement.dto.StudentCreateRequest;
import com.schoolmanagement.dto.StudentResponse;
import com.schoolmanagement.dto.PageResponse;
import com.schoolmanagement.entity.*;
import com.schoolmanagement.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * UPDATED: Added search, filter, and export methods
 * File: backend/src/main/java/com/schoolmanagement/service/StudentService.java
 */
@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;
    private final UserRepository userRepository;
    private final SchoolRepository schoolRepository;
    private final ClassSectionRepository classSectionRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public PageResponse<StudentResponse> getAllStudents(Long schoolId, Long classSectionId,
                                                        int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Student> studentPage;

        if (classSectionId != null) {
            studentPage = studentRepository.findByClassSectionId(classSectionId, pageable);
        } else if (schoolId != null) {
            studentPage = studentRepository.findBySchoolId(schoolId, pageable);
        } else {
            studentPage = studentRepository.findAll(pageable);
        }

        Page<StudentResponse> responsePage = studentPage.map(StudentResponse::fromEntity);
        return PageResponse.fromPage(responsePage);
    }

    /**
     * NEW: Search students with filters
     */
    @Transactional(readOnly = true)
    public PageResponse<StudentResponse> searchStudents(Long schoolId,
                                                        String searchTerm,
                                                        Long classSectionId,
                                                        Boolean isActive,
                                                        int page,
                                                        int size,
                                                        String sortBy,
                                                        String sortDir) {
        Sort sort = Sort.by(sortDir.equalsIgnoreCase("desc") ?
                Sort.Direction.DESC : Sort.Direction.ASC, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Student> studentPage;

        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            studentPage = studentRepository.advancedSearch(
                    schoolId, searchTerm, classSectionId, isActive, pageable
            );
        } else if (classSectionId != null) {
            studentPage = studentRepository.findByClassSectionId(classSectionId, pageable);
        } else {
            studentPage = studentRepository.findBySchoolId(schoolId, pageable);
        }

        Page<StudentResponse> responsePage = studentPage.map(StudentResponse::fromEntity);
        return PageResponse.fromPage(responsePage);
    }

    /**
     * NEW: Get all students for export (no pagination)
     */
    @Transactional(readOnly = true)
    public List<Student> getAllStudentsForExport(Long schoolId, Long classSectionId) {
        if (classSectionId != null) {
            return studentRepository.findByClassSectionId(classSectionId);
        } else if (schoolId != null) {
            return studentRepository.findAllBySchoolIdOrdered(schoolId);
        } else {
            return studentRepository.findAll();
        }
    }

    /**
     * NEW: Search students by name or admission number (for export)
     */
    @Transactional(readOnly = true)
    public List<Student> searchStudentsForExport(Long schoolId, String searchTerm) {
        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            Page<Student> page = studentRepository.searchStudents(
                    schoolId, searchTerm, Pageable.unpaged()
            );
            return page.getContent();
        }
        return studentRepository.findAllBySchoolIdOrdered(schoolId);
    }

    /**
     * NEW: Get students by date range
     */
    @Transactional(readOnly = true)
    public List<Student> getStudentsByDateRange(Long schoolId, LocalDate startDate, LocalDate endDate) {
        return studentRepository.findByAdmissionDateRange(schoolId, startDate, endDate);
    }

    @Transactional(readOnly = true)
    public StudentResponse getStudentById(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found with id: " + id));
        return StudentResponse.fromEntity(student);
    }

    @Transactional
    public StudentResponse createStudent(StudentCreateRequest request) {
        // Validate school
        School school = schoolRepository.findById(request.getSchoolId())
                .orElseThrow(() -> new RuntimeException("School not found"));

        // Validate class section
        ClassSection classSection = classSectionRepository.findById(request.getClassSectionId())
                .orElseThrow(() -> new RuntimeException("Class section not found"));

        // Check if admission number exists
        if (studentRepository.existsByAdmissionNumber(request.getAdmissionNumber())) {
            throw new RuntimeException("Admission number already exists");
        }

        // Check if username exists
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        // Create user
        User user = User.builder()
                .school(school)
                .username(request.getUsername())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .dateOfBirth(request.getDateOfBirth())
                .gender(User.Gender.valueOf(request.getGender()))
                .phone(request.getPhone())
                .address(request.getAddress())
                .city(request.getCity())
                .state(request.getState())
                .role(User.Role.STUDENT)
                .isActive(true)
                .build();

        user = userRepository.save(user);

        // Create student
        Student student = Student.builder()
                .user(user)
                .school(school)
                .admissionNumber(request.getAdmissionNumber())
                .classSection(classSection)
                .rollNumber(request.getRollNumber())
                .admissionDate(request.getAdmissionDate())
                .fatherName(request.getFatherName())
                .fatherPhone(request.getFatherPhone())
                .fatherOccupation(request.getFatherOccupation())
                .motherName(request.getMotherName())
                .motherPhone(request.getMotherPhone())
                .motherOccupation(request.getMotherOccupation())
                .guardianName(request.getGuardianName())
                .guardianPhone(request.getGuardianPhone())
                .guardianRelation(request.getGuardianRelation())
                .emergencyContact(request.getEmergencyContact())
                .bloodGroup(request.getBloodGroup())
                .isActive(true)
                .build();

        student = studentRepository.save(student);
        return StudentResponse.fromEntity(student);
    }

    @Transactional
    public void deleteStudent(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        studentRepository.delete(student);
    }
}