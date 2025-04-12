package com.lrs.SSB.service;
import com.lrs.SSB.controller.InsuranceDTO;
import com.lrs.SSB.entity.Insurance;
import com.lrs.SSB.repository.InsuranceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class InsuranceService {

    private final InsuranceRepository insuranceRepository;

    @Autowired
    public InsuranceService(InsuranceRepository insuranceRepository) {
        this.insuranceRepository = insuranceRepository;
    }
    private InsuranceDTO mapToDTO(Insurance insurance) {
        return new InsuranceDTO(
                insurance.getId(),
                insurance.getTitle(),
                insurance.getCategory(),
                insurance.getPrice(),
                insurance.getCoverage(),
                insurance.getBenefits(),
                insurance.getProvider()
        );
    }

    public List<InsuranceDTO> getAllInsurances() {
        List<Insurance> insurances = insuranceRepository.findAll();
        return insurances.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public InsuranceDTO createInsurance(InsuranceDTO insuranceDTO) {
        Insurance insurance = new Insurance(
                insuranceDTO.getTitle(),
                insuranceDTO.getCategory(),
                insuranceDTO.getPrice(),
                insuranceDTO.getCoverage(),
                insuranceDTO.getBenefits(),
                insuranceDTO.getProvider()
        );
        Insurance savedInsurance = insuranceRepository.save(insurance);
        return mapToDTO(savedInsurance);
    }
    public Optional<Insurance> findById(Long id) {
        return insuranceRepository.findById(id);
    }

}
