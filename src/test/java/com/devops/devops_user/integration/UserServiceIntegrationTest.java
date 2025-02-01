package com.devops.devops_user.integration;

import com.devops.devops_user.dto.CreateAddressDTO;
import com.devops.devops_user.dto.CreateUserDTO;
import com.devops.devops_user.enumeration.Role;
import com.devops.devops_user.model.User;
import com.devops.devops_user.repository.UserRepository;
import com.devops.devops_user.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class UserServiceIntegrationTest extends BaseIntegrationTest {

  @Autowired
  private UserService userService;

  @Autowired
  private UserRepository userRepository;

  private CreateUserDTO createUserDTO;

  @BeforeEach
  void setUp() {
    userRepository.deleteAll();

    CreateAddressDTO addressDTO = new CreateAddressDTO();
    addressDTO.setStreet("Test Street");
    addressDTO.setNumber(123);
    addressDTO.setCity("Test City");
    addressDTO.setCountry("Test Country");

    createUserDTO = new CreateUserDTO();
    createUserDTO.setFirstName("John");
    createUserDTO.setLastName("Doe");
    createUserDTO.setUsername("johndoe");
    createUserDTO.setPassword("password");
    createUserDTO.setEmail("john@example.com");
    createUserDTO.setRole(Role.GUEST);
    createUserDTO.setAddress(addressDTO);
  }

  @Test
  void createAndFindUser() {
    // Create user
    User savedUser = userService.save(createUserDTO);
    assertNotNull(savedUser);
    assertNotNull(savedUser.getId());
    assertEquals("John", savedUser.getFirstName());
    assertEquals("johndoe", savedUser.getUsername());

    // Find user
    User foundUser = userService.findUserById(savedUser.getId());
    assertNotNull(foundUser);
    assertEquals(savedUser.getId(), foundUser.getId());
    assertEquals(savedUser.getUsername(), foundUser.getUsername());
  }

  @Test
  void findByFirstNameAndLastName() {
    User savedUser = userService.save(createUserDTO);
    assertNotNull(savedUser);

    var users = userService.findByFirstNameAndLastName("John", "Doe");
    assertFalse(users.isEmpty());
    assertEquals(1, users.size());
    assertEquals("johndoe", users.get(0).getUsername());
  }

  @Test
  void deleteUser() {
    User savedUser = userService.save(createUserDTO);
    assertNotNull(savedUser);

    User deletedUser = userService.delete(savedUser.getId());
    assertTrue(deletedUser.getDeleted());

    User foundUser = userService.findUserById(savedUser.getId());
    assertNotNull(foundUser);
    assertTrue(foundUser.getDeleted());
  }
}