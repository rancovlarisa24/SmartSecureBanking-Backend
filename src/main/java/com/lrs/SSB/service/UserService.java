package com.lrs.SSB.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import com.lrs.SSB.repository.UserRepository;
import com.lrs.SSB.entity.User;
import com.lrs.SSB.controller.userDto;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public void saveUser(userDto userDto) {
        if ((userDto.getEmail() == null || userDto.getEmail().isBlank()) &&
                (userDto.getTelefon() == null || userDto.getTelefon().isBlank())) {
            throw new IllegalArgumentException("Either email or phone number must be provided.");
        }

        User user = new User();
        user.setNumeComplet(userDto.getNumeComplet());
        user.setEmail(userDto.getEmail());
        user.setTelefon(userDto.getTelefon());

        String hashedPassword = passwordEncoder.encode(userDto.getParola());
        user.setParola(hashedPassword);

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

    public Optional<User> findByContact(String contact) {
        if (contact.contains("@")) {
            return userRepository.findByEmail(contact);
        } else {
            return userRepository.findByTelefon(contact);
        }
    }

    public boolean verifyPassword(String plainPassword, String hashedPassword) {
        return passwordEncoder.matches(plainPassword, hashedPassword);
    }

    public void updatePassword(String contact, String newPassword) {
        Optional<User> userOpt = findByContact(contact);
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("User not found.");
        }

        User user = userOpt.get();
        String hashedPassword = passwordEncoder.encode(newPassword);
        user.setParola(hashedPassword);
        userRepository.save(user);
    }

}
