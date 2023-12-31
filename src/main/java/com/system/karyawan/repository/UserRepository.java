package com.system.karyawan.repository;

import com.system.karyawan.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByUsername(String username);

  Boolean existsByUsername(String username);
  Optional<User> findByOtpAndStatus(String otp, String status);

  Boolean existsByEmail(String email);

  Optional<User> findByEmail(String email);
}
