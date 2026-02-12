package com.schoolmanagement.dto;

import com.schoolmanagement.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;

@Data
public class RegisterRequest {

    @NotNull(message = "School ID is required")
    private Long schoolId;

    @NotBlank(message = "Username is required")
    private String username;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;

    @NotBlank(message = "First name is required")
    private String firstName;

    private String lastName;

    private LocalDate dateOfBirth;

    private User.Gender gender;

    private String phone;

    private String address;

    private String city;

    private String state;

    private String pincode;

    @NotNull(message = "Role is required")
    private User.Role role;
}