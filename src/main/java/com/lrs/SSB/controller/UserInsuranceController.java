package com.lrs.SSB.controller;
import com.lrs.SSB.controller.UserInsuranceDTO;
import com.lrs.SSB.service.UserInsuranceService;
import com.lrs.SSB.entity.UserInsurance;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user-insurances")
public class UserInsuranceController {

    private final UserInsuranceService service;

    public UserInsuranceController(UserInsuranceService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<?> saveInsurance(@RequestBody UserInsuranceDTO dto) {
        try {
            UserInsurance saved = service.save(dto);
            return ResponseEntity.ok(saved);
        } catch (IllegalArgumentException | IllegalStateException | SecurityException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", "Unexpected error", "details", e.getMessage()));
        }
    }


    @GetMapping("/{userId}")
    public ResponseEntity<List<UserInsurance>> getInsurancesByUser(@PathVariable Integer userId) {
        return ResponseEntity.ok(service.getAllForUser(userId));
    }
}
