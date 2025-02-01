package com.devops.devops_user.unit;

import com.devops.devops_user.dto.CreateAddressDTO;
import com.devops.devops_user.dto.CreateUserDTO;
import com.devops.devops_user.enumeration.Role;
import com.devops.devops_user.exceptions.AttributeNotUniqueException;
import com.devops.devops_user.exceptions.AttributeNullException;
import com.devops.devops_user.model.User;
import com.devops.devops_user.repository.AddressRepository;
import com.devops.devops_user.repository.UserRepository;
import com.devops.devops_user.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private AddressRepository addressRepository;

  @InjectMocks
  private UserService userService;

  private CreateUserDTO createUserDTO;
  private User user;

  @BeforeEach
  void setUp() {
    CreateAddressDTO addressDTO = new CreateAddressDTO();
    addressDTO.setStreet("Test Street");
    addressDTO.setNumber(123);
    addressDTO.setCity("Test City");
    addressDTO.setCountry("Test Country");

    createUserDTO = new CreateUserDTO();
    createUserDTO.setFirstName("John Test");
    createUserDTO.setLastName("Doe Test");
    createUserDTO.setUsername("johndoetest");
    createUserDTO.setPassword("password");
    createUserDTO.setEmail("johntest@example.com");
    createUserDTO.setRole(Role.GUEST);
    createUserDTO.setAddress(addressDTO);

    user = User.builder()
            .id(1)
            .firstName("John Test")
            .lastName("Doe Test")
            .username("johndoetest")
            .email("johntest@example.com")
            .role(Role.GUEST)
            .deleted(false)
            .build();
  }

  @Test
  void createUser_Success() {
    when(userRepository.findByUsername(any())).thenReturn(null);
    when(userRepository.findByEmail(any())).thenReturn(null);
    when(userRepository.save(any())).thenReturn(user);

    User result = userService.save(createUserDTO);

    assertNotNull(result);
    assertEquals("John Test", result.getFirstName());
    assertEquals("Doe Test", result.getLastName());
    verify(userRepository).save(any());
  }

  @Test
  void createUser_DuplicateUsername() {
    when(userRepository.findByUsername("johndoetest")).thenReturn(user);

    assertThrows(AttributeNotUniqueException.class, () -> {
      userService.save(createUserDTO);
    });
  }

  @Test
  void createUser_NullAttributes() {
    createUserDTO.setFirstName(null);

    assertThrows(AttributeNullException.class, () -> {
      userService.save(createUserDTO);
    });
  }

  @Test
  void findUserById_Success() {
    when(userRepository.findById(1)).thenReturn(Optional.of(user));

    User result = userService.findUserById(1);

    assertNotNull(result);
    assertEquals(1, result.getId());
    assertEquals("John Test", result.getFirstName());
  }

  @Test
  void findUserById_NotFound() {
    when(userRepository.findById(999)).thenReturn(Optional.empty());

    User result = userService.findUserById(999);

    assertNull(result);
  }
}