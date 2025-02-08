package com.lrs.SSB.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.lrs.SSB.entity.User;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    Optional<User> findByTelefon(String telefon);

    boolean existsByNumeComplet(String numeComplet);
}
