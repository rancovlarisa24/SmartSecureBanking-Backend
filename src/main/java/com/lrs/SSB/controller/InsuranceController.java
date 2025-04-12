package com.lrs.SSB.controller;
import com.lrs.SSB.controller.InsuranceDTO;
import com.lrs.SSB.service.InsuranceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/insurances")
public class InsuranceController {

    private final InsuranceService insuranceService;

    @Autowired
    public InsuranceController(InsuranceService insuranceService) {
        this.insuranceService = insuranceService;
    }
    @GetMapping
    public List<InsuranceDTO> getAllInsurances() {
        return insuranceService.getAllInsurances();
    }

    @PostMapping
    public InsuranceDTO createInsurance(@RequestBody InsuranceDTO insuranceDTO) {
        return insuranceService.createInsurance(insuranceDTO);
    }

}
