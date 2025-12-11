package com.ehr.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.ehr.dto.*;
import com.ehr.models.User;
import com.ehr.models.Staff;
import com.ehr.service.UserService;
import com.ehr.service.StaffService;
import com.ehr.service.CustomUserDetailsService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/auth")
@Validated
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private StaffService staffService;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @GetMapping("")
    public String hello(){
        return "Hello from spring boot";
    }
    @PostMapping("/users/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserRegistrationDto dto) {
        try {
            User user = userService.registerUser(dto);
            return ResponseEntity.ok(new AuthResponse(
                    "User registered successfully",
                    null,
                    "USER"
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                    new AuthResponse(e.getMessage(), null, "ERROR")
            );
        }
    }

    @PostMapping("/users/login")
    public ResponseEntity<?> loginUser(@Valid @RequestBody UserLoginRequestDto req,
                                       HttpServletRequest request) {
        try {
            // Determine the actual username based on identifier
            String username = userService.findByEmailOrPhone(req.getIdentifier())
                    .map(User::getEmail)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));

            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(username, req.getPassword());

            Authentication auth = authenticationManager.authenticate(authToken);
            SecurityContextHolder.getContext().setAuthentication(auth);

            HttpSession session = request.getSession(true);
            User user = userService.findByEmailOrPhone(req.getIdentifier()).get();

            return ResponseEntity.ok(new AuthResponse(
                    "Login successful",
                    session.getId(),
                    "USER",
                    user.getId(),
                    user.getFullName(),
                    user.getRole()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(401).body(
                    new AuthResponse("Login failed: " + e.getMessage(), null, "ERROR")
            );
        }
    }

    @PostMapping("/staff/create")
    public ResponseEntity<?> createStaff(@RequestBody StaffCreateDto dto) {
        Staff staff = staffService.createStaff(dto);
        return ResponseEntity.ok(new AuthResponse(
                "Staff created successfully",
                null,
                "STAFF"
        ));
    }

    @PostMapping("/staff/login")
    public ResponseEntity<?> loginStaff(@RequestBody StaffLoginRequest req,
                                        HttpServletRequest request) {
        try {
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(req.getWorkId(), req.getPassword());

            Authentication auth = authenticationManager.authenticate(authToken);
            SecurityContextHolder.getContext().setAuthentication(auth);

            HttpSession session = request.getSession(true);
            Optional<Staff> staff = staffService.findByWorkId(req.getWorkId());

            return staff.map(value -> ResponseEntity.ok(new AuthResponse(
                    "Login successful",
                    session.getId(),
                    "STAFF",
                    value.getWorkId(),
                    value.getFullName(),
                    value.getRole().toString()
            ))).orElseGet(() -> ResponseEntity.status(401).body(
                    new AuthResponse("Check credentials and Try again", null, "ERROR")
            ));

        } catch (Exception e) {
            return ResponseEntity.status(401).body(
                    new AuthResponse("Login failed: " + e.getMessage(), null, "ERROR")
            );
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        request.getSession().invalidate();
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok(new AuthResponse("Logout successful", null, null));
    }
}