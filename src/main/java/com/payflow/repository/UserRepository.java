package com.payflow.repository;

import com.payflow.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {

    //optional to work with the case where user details doesnt exists and we dont want app to crush
    Optional<User> findByEmail(String username);
}
