package com.lrs.SSB.controller;
import com.lrs.SSB.controller.userDto;
import com.lrs.SSB.entity.User;
import com.lrs.SSB.repository.UserRepository;
import com.lrs.SSB.service.UserService;
import com.lrs.SSB.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
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

        String token = jwtUtil.generateToken(contact);

        return ResponseEntity.ok(Map.of("token", token));
    }

    @PostMapping("/validate-token")
    public ResponseEntity<?> validateToken(@RequestHeader(value = "Authorization", required = false) String token) {

        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body(Map.of("message", "Invalid or missing token."));
        }
        try {
            token = token.substring(7).trim();

            if (!jwtUtil.validateToken(token)) {
                return ResponseEntity.status(401).body(Map.of("message", "Invalid or expired token."));
            }

            String contact = jwtUtil.extractContact(token);
            Optional<User> user = userService.findByContact(contact);
            if (user.isEmpty()) {
                return ResponseEntity.status(401).body(Map.of("message", "User not found."));
            }

            return ResponseEntity.ok(Map.of("message", "Token is valid.", "user", contact));

        } catch (Exception e) {
            return ResponseEntity.status(401).body(Map.of("message", "Error processing token.", "error", e.getMessage()));
        }
    }


    @PostMapping("/update-password")
    public ResponseEntity<?> updatePassword(@RequestBody Map<String, String> request) {
        String contact = request.get("contact");
        String newPassword = request.get("newPassword");

        if (contact == null || contact.isBlank() || newPassword == null || newPassword.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Contact and new password are required."));
        }

        try {
            userService.updatePassword(contact.trim(), newPassword);
            return ResponseEntity.ok(Map.of("message", "Password updated successfully."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Internal server error."));
        }
    }

    @PostMapping("/upload-profile-image")
    public ResponseEntity<?> uploadProfileImage(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestParam(value = "image", required = false) MultipartFile imageFile
    ) {
        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body("Unauthorized - Missing or Invalid Token");
        }

        if (imageFile == null) {
            return ResponseEntity.badRequest().body("No image file received.");
        }

        try {
            String jwtToken = token.substring(7).trim();
            String userContact = jwtUtil.extractContact(jwtToken);
            Optional<User> userOpt = userService.findByContact(userContact);

            if (userOpt.isEmpty()) {
                return ResponseEntity.status(404).body("User not found.");
            }

            User user = userOpt.get();

            byte[] imageBytes = imageFile.getBytes();
            if (imageBytes.length == 0) {
                return ResponseEntity.badRequest().body("Image is empty after conversion.");
            }

            user.setProfileImage(imageBytes);
            userRepository.save(user);
            return ResponseEntity.ok().body("Profile image updated successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(Map.of("error", "Internal server error.", "details", e.getMessage()));
        }
    }


    @GetMapping("/profile-image")
    public ResponseEntity<byte[]> getProfileImage(@RequestHeader("Authorization") String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body(null);
        }
        try {
            String jwtToken = token.substring(7).trim();
            String userContact = jwtUtil.extractContact(jwtToken);

            Optional<User> userOpt = userService.findByContact(userContact);
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(404).body(null);
            }

            User user = userOpt.get();
            byte[] imageData = user.getProfileImage();
            if (imageData == null || imageData.length == 0) {
                return ResponseEntity.status(404).body(null);
            }

            return ResponseEntity.ok()
                    .header("Content-Type", "image/jpeg")
                    .body(imageData);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(null);
        }
    }


    @GetMapping("/get-username")
    public ResponseEntity<?> getUsername(@RequestHeader("Authorization") String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(401)
                    .body(Map.of("message", "Invalid or missing token."));
        }
        try {
            String jwtToken = token.substring(7).trim();
            String userContact = jwtUtil.extractContact(jwtToken);

            Optional<User> userOpt = userService.findByContact(userContact);
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(404)
                        .body(Map.of("message", "User not found."));
            }

            User user = userOpt.get();
            return ResponseEntity.ok(Map.of("username", user.getNumeComplet()));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(Map.of("message", "Error processing request.", "error", e.getMessage()));
        }
    }

    @GetMapping("/get-parola")
    public ResponseEntity<?> getParola(@RequestHeader("Authorization") String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(401)
                    .body(Map.of("message", "Invalid or missing token."));
        }
        try {
            String jwtToken = token.substring(7).trim();
            String userContact = jwtUtil.extractContact(jwtToken);

            Optional<User> userOpt = userService.findByContact(userContact);
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(404)
                        .body(Map.of("message", "User not found."));
            }

            User user = userOpt.get();
            return ResponseEntity.ok(Map.of("parola", user.getParola()));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(Map.of("message", "Error processing request.", "error", e.getMessage()));
        }
    }

    @GetMapping("/get-contact")
    public ResponseEntity<?> getContact(@RequestHeader("Authorization") String token) {
        try {
            if (token == null || !token.startsWith("Bearer ")) {
                return ResponseEntity.status(401)
                        .body(Map.of("message", "Invalid or missing token."));
            }
            String jwtToken = token.substring(7).trim();
            String contact = jwtUtil.extractContact(jwtToken);
            return ResponseEntity.ok(Map.of("contact", contact));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(Map.of("message", "Error processing request.", "error", e.getMessage()));
        }
    }

    @GetMapping("/get-seria-id")
    public ResponseEntity<?> getSeriaId(@RequestHeader("Authorization") String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(401)
                    .body(Map.of("message", "Invalid or missing token."));
        }
        try {
            String jwtToken = token.substring(7).trim();
            String userContact = jwtUtil.extractContact(jwtToken);

            Optional<User> userOpt = userService.findByContact(userContact);
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(404)
                        .body(Map.of("message", "User not found."));
            }

            User user = userOpt.get();
            return ResponseEntity.ok(Map.of("seriaId", user.getSeriaId()));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(Map.of("message", "Error processing request.", "error", e.getMessage()));
        }
    }

    @GetMapping("/get-numar-id")
    public ResponseEntity<?> getNumarId(@RequestHeader("Authorization") String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(401)
                    .body(Map.of("message", "Invalid or missing token."));
        }
        try {
            String jwtToken = token.substring(7).trim();
            String userContact = jwtUtil.extractContact(jwtToken);

            Optional<User> userOpt = userService.findByContact(userContact);
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(404)
                        .body(Map.of("message", "User not found."));
            }

            User user = userOpt.get();
            return ResponseEntity.ok(Map.of("numarId", user.getNumarId()));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(Map.of("message", "Error processing request.", "error", e.getMessage()));
        }
    }

    @GetMapping("/get-data-nasterii")
    public ResponseEntity<?> getDataNasterii(@RequestHeader("Authorization") String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(401)
                    .body(Map.of("message", "Invalid or missing token."));
        }
        try {
            String jwtToken = token.substring(7).trim();
            String userContact = jwtUtil.extractContact(jwtToken);

            Optional<User> userOpt = userService.findByContact(userContact);
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(404)
                        .body(Map.of("message", "User not found."));
            }

            User user = userOpt.get();
            return ResponseEntity.ok(Map.of("dataNasterii", user.getDataNasterii() != null ? user.getDataNasterii().toString() : ""));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(Map.of("message", "Error processing request.", "error", e.getMessage()));
        }
    }

    @GetMapping("/get-judet")
    public ResponseEntity<?> getJudet(@RequestHeader("Authorization") String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(401)
                    .body(Map.of("message", "Invalid or missing token."));
        }
        try {
            String jwtToken = token.substring(7).trim();
            String userContact = jwtUtil.extractContact(jwtToken);

            Optional<User> userOpt = userService.findByContact(userContact);
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(404)
                        .body(Map.of("message", "User not found."));
            }

            User user = userOpt.get();
            return ResponseEntity.ok(Map.of("judet", user.getJudet()));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(Map.of("message", "Error processing request.", "error", e.getMessage()));
        }
    }

    @GetMapping("/get-localitate")
    public ResponseEntity<?> getLocalitate(@RequestHeader("Authorization") String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(401)
                    .body(Map.of("message", "Invalid or missing token."));
        }
        try {
            String jwtToken = token.substring(7).trim();
            String userContact = jwtUtil.extractContact(jwtToken);

            Optional<User> userOpt = userService.findByContact(userContact);
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(404)
                        .body(Map.of("message", "User not found."));
            }

            User user = userOpt.get();
            return ResponseEntity.ok(Map.of("localitate", user.getLocalitate()));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(Map.of("message", "Error processing request.", "error", e.getMessage()));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser(@RequestHeader(value = "Authorization", required = false) String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body(Map.of("message", "Token is missing or invalid."));
        }

        return ResponseEntity.ok(Map.of("message", "Logout successful."));
    }

    @PostMapping("/verify-password")
    public ResponseEntity<?> verifyPassword(
            @RequestHeader(value = "Authorization", required = true) String token,
            @RequestBody Map<String, String> request
    ) {
        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body(Map.of("message", "Invalid or missing token."));
        }

        String jwtToken = token.substring(7).trim();
        String contact;
        try {
            contact = jwtUtil.extractContact(jwtToken);
        } catch (Exception e) {
            return ResponseEntity.status(401).body(Map.of("message", "Invalid token.", "error", e.getMessage()));
        }
        String password = request.get("password");
        if (password == null || password.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Password is required."));
        }

        Optional<User> userOpt = userService.findByContact(contact);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("message", "User not found."));
        }

        User user = userOpt.get();
        boolean isValid = userService.verifyPassword(password, user.getParola());
        if (!isValid) {
            return ResponseEntity.status(401).body(Map.of("message", "Incorrect password."));
        }

        return ResponseEntity.ok(Map.of("message", "Password is correct."));
    }



}
