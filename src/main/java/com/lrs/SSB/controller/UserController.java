package com.lrs.SSB.controller;

import com.lrs.SSB.entity.User;
import com.lrs.SSB.repository.UserRepository;
import com.lrs.SSB.service.UserService;
import com.lrs.SSB.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody userDto userDto) {
        try {
            userService.saveUser(userDto);
            return ResponseEntity.status(201).body("User created successfully.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body("Invalid data: " + e.getMessage());
        } catch (DataIntegrityViolationException ex) {
            String errorMessage = ex.getMostSpecificCause().getMessage();

            if (errorMessage.contains("users_email_key")) {
                return ResponseEntity.status(409).body("User already exists with this email.");
            } else if (errorMessage.contains("users_telefon_key")) {
                return ResponseEntity.status(409).body("User already exists with this phone number.");
            } else {
                return ResponseEntity.status(409).body("User already exists.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Server error: " + e.getMessage());
        }
    }



    @PostMapping("/checkContact")
    public ResponseEntity<?> checkContact(@RequestBody Map<String, String> request) {
        String contact = request.get("contact");
        if (contact == null || contact.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Contact is required."));
        }

        boolean exists = userService.userExists(contact.trim());
        if (exists) {
            return ResponseEntity.status(409).body(Map.of("error", "User already exists."));
        } else {
            return ResponseEntity.ok(Map.of("message", "Contact is available."));
        }
    }

    @PostMapping("/checkNume")
    public ResponseEntity<?> checkNume(@RequestBody Map<String, String> request) {
        String numeComplet = request.get("numeComplet");

        if (numeComplet == null || numeComplet.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Name is required.");
        }

        boolean exists = userRepository.existsByNumeComplet(numeComplet.trim());

        if (exists) {
            return ResponseEntity.status(409).body("User already exists.");
        }

        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody Map<String, String> request) {
        String contact = request.get("contact");
        String password = request.get("password");

        if (contact == null || contact.isBlank() || password == null || password.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Email/phone number and password are required."));
        }

        Optional<User> userOpt = userService.findByContact(contact);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(401).body(Map.of("message", "User does not exist."));
        }

        User user = userOpt.get();
        boolean correctPassword = userService.verifyPassword(password, user.getParola());

        if (!correctPassword) {
            return ResponseEntity.status(401).body(Map.of("message", "Incorrect password."));
        }

        String token = jwtUtil.generateToken(user.getEmail());

        return ResponseEntity.ok(Map.of("token", token));
    }

    @PostMapping("/validate-token")
    public ResponseEntity<?> validateToken(@RequestHeader(value = "Authorization", required = false) String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body(Map.of("message", "Invalid or missing token."));
        }

        try {
            token = token.substring(7).trim(); // Remove "Bearer " prefix and trim spaces

            if (!jwtUtil.validateToken(token)) {
                return ResponseEntity.status(401).body(Map.of("message", "Invalid or expired token."));
            }

            String userEmail = jwtUtil.extractEmail(token);

            // 🔹 Verifică dacă utilizatorul există în baza de date
            Optional<User> user = userRepository.findByEmail(userEmail);
            if (user.isEmpty()) {
                return ResponseEntity.status(401).body(Map.of("message", "User not found."));
            }

            return ResponseEntity.ok(Map.of("message", "Token is valid.", "user", userEmail));

        } catch (Exception e) {
            return ResponseEntity.status(401).body(Map.of("message", "Error processing token.", "error", e.getMessage()));
        }
    }




}
