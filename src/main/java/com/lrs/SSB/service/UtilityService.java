package com.lrs.SSB.service;

import com.lrs.SSB.controller.UtilityDTO;
import com.lrs.SSB.entity.Utility;
import com.lrs.SSB.repository.UtilityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UtilityService {

    @Autowired
    private UtilityRepository utilityRepository;

    public List<Utility> getAllUtilities() {
        return utilityRepository.findAll();
    }

    public Optional<Utility> getUtilityById(Long id) {
        return utilityRepository.findById(id);
    }

    public Utility createUtility(Utility utility) {
        return utilityRepository.save(utility);
    }

    public Utility updateUtility(Long id, Utility updated) {
        return utilityRepository.findById(id).map(existing -> {
            existing.setServiceName(updated.getServiceName());
            existing.setIban(updated.getIban());
            existing.setPrivateKey(updated.getPrivateKey());
            existing.setLogoUrl(updated.getLogoUrl());
            existing.setCategory(updated.getCategory());
            existing.setDestinationCardId(updated.getDestinationCardId());
            return utilityRepository.save(existing);
        }).orElseThrow(() -> new RuntimeException("Utility not found with ID: " + id));
    }

    public void deleteUtility(Long id) {
        utilityRepository.deleteById(id);
    }

    public UtilityDTO convertToDTO(Utility utility) {
        return new UtilityDTO(
                utility.getId(),
                utility.getServiceName(),
                utility.getIban(),
                utility.getLogoUrl(),
                utility.getCategory(),
                utility.getDestinationCardId()
        );
    }

    public List<UtilityDTO> getAllUtilityDTOs() {
        return utilityRepository.findAll().stream()
                .map(this::convertToDTO)
                .toList();
    }

    public UtilityDTO getUtilityDTOById(Long id) {
        return utilityRepository.findById(id)
                .map(this::convertToDTO)
                .orElse(null);
    }

}
