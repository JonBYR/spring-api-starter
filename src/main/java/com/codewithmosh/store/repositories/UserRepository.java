package com.codewithmosh.store.repositories;

import com.codewithmosh.store.entities.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
   boolean existsByEmail(String email);
   Optional<User> findByEmail(String email);
   @Query(value = "select u from User u where u.email = :email and u.password = :password")
   Optional<User> findByEmailAndPassword(@Param("email") String email, @Param("password") String password);
}
