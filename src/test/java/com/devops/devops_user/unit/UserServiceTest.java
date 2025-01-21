package com.devops.devops_user.unit;


import com.devops.devops_user.dto.*;
import com.devops.devops_user.enumeration.Role;
import com.devops.devops_user.exceptions.*;
import com.devops.devops_user.model.Address;
import com.devops.devops_user.model.User;
import com.devops.devops_user.repository.AddressRepository;
import com.devops.devops_user.repository.UserRepository;
import com.devops.devops_user.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

  @InjectMocks
  private UserService userService;

  @Mock
  private UserRepository userRepository;

  @Mock
  private AddressRepository addressRepository;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void findUserById_ShouldReturnUser_WhenUserExists() {
    User user = new User();
    user.setId(1);
    when(userRepository.findById(1)).thenReturn(Optional.of(user));

    User result = userService.findUserById(1);

    assertNotNull(result);
    assertEquals(1, result.getId());
  }

  @Test
  void findUserById_ShouldReturnNull_WhenUserDoesNotExist() {
    when(userRepository.findById(1)).thenReturn(Optional.empty());

    User result = userService.findUserById(1);

    assertNull(result);
  }

  @Test
  void findByFirstNameAndLastName_ShouldReturnUsers_WhenUsersExist() {
    User user = new User();
    user.setFirstName("John");
    user.setLastName("Doe");
    when(userRepository.findByFirstNameAndLastNameAllIgnoringCase("John", "Doe"))
        .thenReturn(Collections.singletonList(user));

    var result = userService.findByFirstNameAndLastName("John", "Doe");

    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals("John", result.get(0).getFirstName());
  }

  @Test
  void findAllUsers_ShouldReturnAllUsers() {
    User user = new User();
    when(userRepository.findAll()).thenReturn(Collections.singletonList(user));

    var result = userService.findAllUsers();

    assertNotNull(result);
    assertEquals(1, result.size());
  }

  @Test
  void findAllUsersPaged_ShouldReturnPagedUsers() {
    User user = new User();
    Page<User> userPage = new PageImpl<>(Collections.singletonList(user));
    Pageable pageable = PageRequest.of(0, 1);
    when(userRepository.findAll(pageable)).thenReturn(userPage);

    Page<User> result = userService.findAllUsers(pageable);

    assertNotNull(result);
    assertEquals(1, result.getTotalElements());
  }

  @Test
  void save_ShouldThrowException_WhenAttributesAreNull() {
    CreateUserDTO createUserDTO = new CreateUserDTO();

    assertThrows(AttributeNullException.class, () -> userService.save(createUserDTO));
  }

  @Test
  void save_ShouldThrowException_WhenUsernameIsNotUnique() {
    CreateUserDTO createUserDTO = new CreateUserDTO();
    createUserDTO.setUsername("test");
    createUserDTO.setEmail("test@test.com");
    createUserDTO.setFirstName("John");
    createUserDTO.setLastName("Doe");
    createUserDTO.setPassword("password");
    createUserDTO.setRole(Role.HOST);
    createUserDTO.setAddress(new CreateAddressDTO());

    when(userRepository.findByUsername("test")).thenReturn(new User());

    assertThrows(AttributeNotUniqueException.class, () -> userService.save(createUserDTO));
  }

  @Test
  void update_ShouldThrowException_WhenUserNotFound() {
    when(userRepository.findById(1)).thenReturn(Optional.empty());

    UpdateUserDTO updateUserDTO = new UpdateUserDTO();
    updateUserDTO.setFirstName("John");
    updateUserDTO.setLastName("Doe");
    updateUserDTO.setUsername("john.doe");
    updateUserDTO.setPassword("password");
    updateUserDTO.setEmail("john.doe@test.com");
    updateUserDTO.setAddress(new AddressDTO());

    assertThrows(AddressNotFound.class, () -> userService.update(1, updateUserDTO));
  }

  @Test
  void delete_ShouldMarkUserAsDeleted_WhenUserExists() {
    User user = new User();
    user.setId(1);
    user.setDeleted(false);
    when(userRepository.findById(1)).thenReturn(Optional.of(user));
    when(userRepository.save(any(User.class))).thenReturn(user);

    User result = userService.delete(1);

    assertNotNull(result);
    assertTrue(result.getDeleted());
  }

  @Test
  void delete_ShouldThrowException_WhenUserDoesNotExist() {
    when(userRepository.findById(1)).thenReturn(Optional.empty());

    assertThrows(UserNotFound.class, () -> userService.delete(1));
  }
}
