package com.devops.devops_user.repository;

import com.devops.devops_user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Integer>{

    public List<User> findByFirstNameAndLastNameAllIgnoringCase(String firstName, String lastName);

    public User findByUsername(String username);

    public User findByEmail(String email);
}
