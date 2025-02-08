package com.lrs.SSB.controller;

import com.lrs.SSB.repository.UserRepository;
import com.lrs.SSB.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

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

}
