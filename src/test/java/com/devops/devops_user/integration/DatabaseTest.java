package com.devops.devops_user.integration;


import com.devops.devops_user.dto.*;
import com.devops.devops_user.enumeration.Role;
import com.devops.devops_user.exceptions.*;
import com.devops.devops_user.model.Address;
import com.devops.devops_user.model.User;
import com.devops.devops_user.repository.AddressRepository;
import com.devops.devops_user.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

@SpringBootTest
@ActiveProfiles("test") // Activates the test profile
@Transactional // Ensures the database state is rolled back after each test
public class DatabaseTest {

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
  public void testInsertAndFetchData() {
    jdbcTemplate.execute("INSERT INTO test_table (id, name) VALUES (1, 'Test User')");
    String name = jdbcTemplate.queryForObject(
        "SELECT name FROM test_table WHERE id = 1", String.class);
    assertEquals("Test User", name);
  }
}
