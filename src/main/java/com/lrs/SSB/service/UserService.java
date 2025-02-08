package com.lrs.SSB.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.lrs.SSB.repository.UserRepository;
import com.lrs.SSB.entity.User;
import com.lrs.SSB.controller.userDto;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public void saveUser(userDto userDto) {
        if ((userDto.getEmail() == null || userDto.getEmail().isBlank()) &&
                (userDto.getTelefon() == null || userDto.getTelefon().isBlank())) {
            throw new IllegalArgumentException("Either email or phone number must be provided.");
        }

        User user = new User();
        user.setNumeComplet(userDto.getNumeComplet());
        user.setEmail(userDto.getEmail());
        user.setTelefon(userDto.getTelefon());
        user.setParola(userDto.getParola());
        user.setSeriaId(userDto.getSeriaId());
        user.setNumarId(userDto.getNumarId());
        user.setDataNasterii(userDto.getDataNasterii());
        user.setJudet(userDto.getJudet());
        user.setLocalitate(userDto.getLocalitate());

        userRepository.save(user);
    }


    public boolean userExists(String contact) {
        if (contact.contains("@")) {
            return userRepository.findByEmail(contact).isPresent();
        } else {
            return userRepository.findByTelefon(contact).isPresent();
        }
    }
}
