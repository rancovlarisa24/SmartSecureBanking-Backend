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
        User user = new User();
        user.setNumeComplet(userDto.getNumeComplet());
        user.setEmail(userDto.getEmail());
        user.setTelefon(userDto.getTelefon());
        user.setParola(userDto.getParola());
        user.setActivated(false);
        user.setCodActivare(userDto.getCodActivare());
        user.setSeriaId(userDto.getSeriaId());
        user.setNumarId(userDto.getNumarId());
        user.setDataNasterii(userDto.getDataNasterii());
        user.setJudet(userDto.getJudet());
        user.setLocalitate(userDto.getLocalitate());

        userRepository.save(user);
    }
}
