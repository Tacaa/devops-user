package com.devops.devops_user.repository;

import com.devops.devops_user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long>{


}
