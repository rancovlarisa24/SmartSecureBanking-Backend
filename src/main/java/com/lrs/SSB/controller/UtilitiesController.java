package com.lrs.SSB.controller;

import com.lrs.SSB.entity.Utility;
import com.lrs.SSB.service.UtilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/utilities")
public class UtilitiesController {

    @Autowired
    private UtilityService utilityService;

    @GetMapping
    public ResponseEntity<List<UtilityDTO>> getAllUtilities() {
        return ResponseEntity.ok(utilityService.getAllUtilityDTOs());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UtilityDTO> getUtilityById(@PathVariable Long id) {
        UtilityDTO dto = utilityService.getUtilityDTOById(id);
        return dto != null ? ResponseEntity.ok(dto) : ResponseEntity.notFound().build();
    }


    @PostMapping
    public ResponseEntity<Utility> createUtility(@RequestBody Utility utility) {
        return ResponseEntity.ok(utilityService.createUtility(utility));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Utility> updateUtility(@PathVariable Long id, @RequestBody Utility utility) {
        return ResponseEntity.ok(utilityService.updateUtility(id, utility));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUtility(@PathVariable Long id) {
        utilityService.deleteUtility(id);
        return ResponseEntity.noContent().build();
    }
}
