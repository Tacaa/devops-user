package com.devops.devops_user.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.devops.devops_user.enumeration.Role;
import com.devops.devops_user.model.User;
import com.devops.devops_user.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import javax.sql.DataSource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@Transactional // Ensures the database state is rolled back after each test
public class UserServiceIntegrationTest {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private DataSource dataSource;

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Test
  public void testDatabaseConnection() throws SQLException {
    try (Connection connection = dataSource.getConnection()) {
      assertNotNull(connection);
      System.out.println("Successfully connected to the database!");
    }
  }

  @Test
  public void testSaveAndRetrieveUser() {
    User user = new User();
    user.setFirstName("Nemanja");
    user.setLastName("Vojinovic");
    user.setEmail("email@gmail.com");
    user.setUsername("vojin");
    user.setPassword("password");
    user.setRole(Role.HOST);
    user.setDeleted(false);

    userRepository.save(user);

    User expectedUser = userRepository.findById(user.getId()).orElse(null);

    assertNotNull(expectedUser);
    assertEquals("Nemanja", expectedUser.getFirstName());
    assertEquals("Vojinovic", expectedUser.getLastName());
    assertEquals("email@gmail.com", expectedUser.getEmail());
    assertEquals(Role.HOST, expectedUser.getRole());
    assertFalse(expectedUser.getDeleted());
  }

  @Test
  public void testFindAllUsers() {
    User user1 = new User();
    user1.setFirstName("John");
    user1.setLastName("Doe");
    user1.setEmail("john.doe@example.com");
    user1.setUsername("johndoe");
    user1.setPassword("password123");
    user1.setRole(Role.HOST);
    user1.setDeleted(false);

    User user2 = new User();
    user2.setFirstName("Jane");
    user2.setLastName("Smith");
    user2.setEmail("jane.smith@example.com");
    user2.setUsername("janesmith");
    user2.setPassword("password456");
    user2.setRole(Role.GUEST);
    user2.setDeleted(false);

    userRepository.save(user1);
    userRepository.save(user2);

    List<User> users = userRepository.findAll();

    assertNotNull(users);
    assertEquals(2, users.size());
  }

  @Test
  public void testUpdateUser() {
    User user = new User();
    user.setFirstName("Initial");
    user.setLastName("User");
    user.setEmail("initial.user@example.com");
    user.setUsername("initialuser");
    user.setPassword("password");
    user.setRole(Role.HOST);
    user.setDeleted(false);

    userRepository.save(user);

    user.setFirstName("Updated");
    user.setLastName("User");
    userRepository.save(user);

    User updatedUser = userRepository.findById(user.getId()).orElse(null);

    assertNotNull(updatedUser);
    assertEquals("Updated", updatedUser.getFirstName());
    assertEquals("User", updatedUser.getLastName());
  }

  @Test
  public void testDeleteUser() {
    User user = new User();
    user.setFirstName("Delete");
    user.setLastName("Me");
    user.setEmail("delete.me@example.com");
    user.setUsername("deleteme");
    user.setPassword("password");
    user.setRole(Role.GUEST);
    user.setDeleted(false);

    userRepository.save(user);

    userRepository.deleteById(user.getId());

    User deletedUser = userRepository.findById(user.getId()).orElse(null);

    assertNull(deletedUser);
  }
}
