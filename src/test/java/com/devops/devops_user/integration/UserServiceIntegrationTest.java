package com.devops.devops_user.integration;

import com.devops.devops_user.client.AccommodationClient;
import com.devops.devops_user.client.GatewayClient;
import com.devops.devops_user.client.NotificationClient;
import com.devops.devops_user.dto.CreateAddressDTO;
import com.devops.devops_user.dto.CreateUserDTO;
import com.devops.devops_user.dto.AddressDTO;
import com.devops.devops_user.dto.UpdateUserDTO;
import com.devops.devops_user.enumeration.Role;
import com.devops.devops_user.exceptions.*;
import com.devops.devops_user.model.Address;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceIntegrationTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private AddressRepository addressRepository;

  @Mock
  private NotificationClient notificationClient;

  @Mock
  private AccommodationClient accommodationClient;

  @Mock
  private GatewayClient gatewayClient;

  @InjectMocks
  private UserService userService;

  private User testUser;
  private Address testAddress;
  private CreateUserDTO createUserDTO;
  private UpdateUserDTO updateUserDTO;

  @BeforeEach
  void setUp() {
    // Setup test address
    testAddress = Address.builder()
            .id(1)
            .street("Test Street")
            .number(123)
            .city("Test City")
            .country("Test Country")
            .build();

    // Setup test user
    testUser = User.builder()
            .id(1)
            .firstName("John")
            .lastName("Doe")
            .username("johndoe")
            .email("john@example.com")
            .password("password123")
            .role(Role.GUEST)
            .address(testAddress)
            .deleted(false)
            .build();

    // Setup CreateUserDTO
    CreateAddressDTO createAddressDTO = new CreateAddressDTO();
    createAddressDTO.setStreet("Test Street");
    createAddressDTO.setNumber(123);
    createAddressDTO.setCity("Test City");
    createAddressDTO.setCountry("Test Country");

    createUserDTO = new CreateUserDTO();
    createUserDTO.setFirstname("John");
    createUserDTO.setLastname("Doe");
    createUserDTO.setUsername("johndoe");
    createUserDTO.setEmail("john@example.com");
    createUserDTO.setPassword("password123");
    createUserDTO.setRole(Role.GUEST);
    createUserDTO.setAddress(createAddressDTO);

    // Setup UpdateUserDTO
    AddressDTO updateAddressDTO = new AddressDTO();
    updateAddressDTO.setId(1);
    updateAddressDTO.setStreet("New Street");
    updateAddressDTO.setNumber(456);
    updateAddressDTO.setCity("New City");
    updateAddressDTO.setCountry("New Country");

    updateUserDTO = new UpdateUserDTO();
    updateUserDTO.setFirstname("John");
    updateUserDTO.setLastname("Smith");
    updateUserDTO.setUsername("johnsmith");
    updateUserDTO.setEmail("john.smith@example.com");
    updateUserDTO.setPassword("newpassword123");
    updateUserDTO.setAddress(updateAddressDTO);
  }

  @Test
  void findUserById_ExistingUser_ReturnsUser() {
    // Arrange
    when(userRepository.findById(1)).thenReturn(Optional.of(testUser));

    // Act
    User result = userService.findUserById(1);

    // Assert
    assertNotNull(result);
    assertEquals(testUser.getId(), result.getId());
    assertEquals(testUser.getUsername(), result.getUsername());
  }

  @Test
  void findUserById_NonExistingUser_ReturnsNull() {
    // Arrange
    when(userRepository.findById(999)).thenReturn(Optional.empty());

    // Act
    User result = userService.findUserById(999);

    // Assert
    assertNull(result);
  }

  @Test
  void findByFirstNameAndLastName_ExistingUsers_ReturnsUserList() {
    // Arrange
    List<User> users = Arrays.asList(testUser);
    when(userRepository.findByFirstNameAndLastNameAllIgnoringCase("John", "Doe")).thenReturn(users);

    // Act
    List<User> result = userService.findByFirstNameAndLastName("John", "Doe");

    // Assert
    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals("John", result.get(0).getFirstName());
    assertEquals("Doe", result.get(0).getLastName());
  }

  @Test
  void findAllUsers_ReturnsAllUsers() {
    // Arrange
    List<User> users = Arrays.asList(testUser);
    when(userRepository.findAll()).thenReturn(users);

    // Act
    List<User> result = userService.findAllUsers();

    // Assert
    assertNotNull(result);
    assertEquals(1, result.size());
  }

  @Test
  void findAllUsers_WithPagination_ReturnsPageOfUsers() {
    // Arrange
    List<User> users = Arrays.asList(testUser);
    Page<User> userPage = new PageImpl<>(users);
    Pageable pageable = PageRequest.of(0, 10);
    when(userRepository.findAll(pageable)).thenReturn(userPage);

    // Act
    Page<User> result = userService.findAllUsers(pageable);

    // Assert
    assertNotNull(result);
    assertEquals(1, result.getTotalElements());
  }

  @Test
  void save_ValidUserDTO_ReturnsCreatedUser() {
    // Arrange
    when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
      User savedUser = invocation.getArgument(0);
      savedUser.setId(1);
      return savedUser;
    });

    // ✅ Use thenReturn instead of doNothing
    when(notificationClient.saveNotificationsPreferences(any()))
            .thenReturn(null); // or a dummy response object if needed

    // Act
    User result = userService.save(createUserDTO);

    // Assert
    assertNotNull(result);
    assertEquals(createUserDTO.getFirstname(), result.getFirstName());
    assertEquals(createUserDTO.getLastname(), result.getLastName());
    assertEquals(createUserDTO.getUsername(), result.getUsername());
    assertEquals(createUserDTO.getEmail(), result.getEmail());
    assertEquals(createUserDTO.getPassword(), result.getPassword());
    assertEquals(createUserDTO.getRole(), result.getRole());
    assertFalse(result.getDeleted());

    verify(notificationClient).saveNotificationsPreferences(any());
  }


  @Test
  void update_ValidUserDTO_ReturnsUpdatedUser() {
    // Arrange
    when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
    when(userRepository.findByUsername(updateUserDTO.getUsername())).thenReturn(null);
    when(userRepository.findByEmail(updateUserDTO.getEmail())).thenReturn(null);
    when(addressRepository.findById(1)).thenReturn(Optional.of(testAddress));
    when(userRepository.save(any(User.class))).thenReturn(testUser);

    // Act
    User result = userService.update(1, updateUserDTO);

    // Assert
    assertNotNull(result);
    assertEquals(updateUserDTO.getFirstname(), result.getFirstName());
    assertEquals(updateUserDTO.getLastname(), result.getLastName());
    assertEquals(updateUserDTO.getUsername(), result.getUsername());
    assertEquals(updateUserDTO.getEmail(), result.getEmail());
    assertEquals(updateUserDTO.getPassword(), result.getPassword());
  }

  @Test
  void update_NullAttribute_ThrowsAttributeNullException() {
    // Arrange
    updateUserDTO.setFirstname(null);

    // Act & Assert
    assertThrows(AttributeNullException.class, () -> userService.update(1, updateUserDTO));
  }

  @Test
  void update_NonExistingUser_ThrowsUserNotFound() {
    // Arrange
    when(userRepository.findById(999)).thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(UserNotFound.class, () -> userService.update(999, updateUserDTO));
  }

  @Test
  void update_NonUniqueUsername_ThrowsAttributeNotUniqueException() {
    // Arrange
    User existingUser = User.builder()
            .id(2)
            .username("johnsmith")
            .build();
    when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
    when(userRepository.findByUsername("johnsmith")).thenReturn(existingUser);

    // Act & Assert
    assertThrows(AttributeNotUniqueException.class, () -> userService.update(1, updateUserDTO));
  }

  @Test
  void update_NonUniqueEmail_ThrowsAttributeNotUniqueException() {
    // Arrange
    User existingUser = User.builder()
            .id(2)
            .email("john.smith@example.com")
            .build();
    when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
    when(userRepository.findByUsername("johnsmith")).thenReturn(null);
    when(userRepository.findByEmail("john.smith@example.com")).thenReturn(existingUser);

    // Act & Assert
    assertThrows(AttributeNotUniqueException.class, () -> userService.update(1, updateUserDTO));
  }

  @Test
  void update_NonExistingAddress_ThrowsAddressNotFound() {
    // Arrange
    when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
    when(userRepository.findByUsername("johnsmith")).thenReturn(null);
    when(userRepository.findByEmail("john.smith@example.com")).thenReturn(null);
    when(addressRepository.findById(1)).thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(AddressNotFound.class, () -> userService.update(1, updateUserDTO));
  }

  @Test
  void delete_GuestWithoutReservations_ReturnsDeletedUser() {
    // Arrange
    when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
    when(accommodationClient.checkIsGuestHavingReservationAtMoment(1)).thenReturn(false);
    when(gatewayClient.disableUser(1)).thenReturn(true);
    when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
      User user = invocation.getArgument(0);
      user.setDeleted(true);
      return user;
    });

    // Act
    User result = userService.delete(1);

    // Assert
    assertNotNull(result);
    assertTrue(result.getDeleted());
    verify(gatewayClient, times(1)).disableUser(1);
  }

  @Test
  void delete_GuestWithReservations_ThrowsUserCanNotBeDeleted() {
    // Arrange
    when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
    when(accommodationClient.checkIsGuestHavingReservationAtMoment(1)).thenReturn(true);

    // Act & Assert
    assertThrows(UserCanNotBeDeleted.class, () -> userService.delete(1));
    verify(gatewayClient, never()).disableUser(anyInt());
  }

  @Test
  void delete_HostWithoutReservations_ReturnsDeletedUser() {
    // Arrange
    testUser.setRole(Role.HOST);
    when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
    when(accommodationClient.checkIsHostHavingReservationAtMoment(1)).thenReturn(false);
    when(accommodationClient.deleteAccommodationsOfHost(1)).thenReturn(true);
    when(gatewayClient.disableUser(1)).thenReturn(true);
    when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
      User user = invocation.getArgument(0);
      user.setDeleted(true);
      return user;
    });

    // Act
    User result = userService.delete(1);

    // Assert
    assertNotNull(result);
    assertTrue(result.getDeleted());
    verify(accommodationClient, times(1)).deleteAccommodationsOfHost(1);
    verify(gatewayClient, times(1)).disableUser(1);
  }

  @Test
  void delete_HostWithReservations_ThrowsUserCanNotBeDeleted() {
    // Arrange
    testUser.setRole(Role.HOST);
    when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
    when(accommodationClient.checkIsHostHavingReservationAtMoment(1)).thenReturn(true);

    // Act & Assert
    assertThrows(UserCanNotBeDeleted.class, () -> userService.delete(1));
    verify(accommodationClient, never()).deleteAccommodationsOfHost(anyInt());
    verify(gatewayClient, never()).disableUser(anyInt());
  }

  @Test
  void delete_NonExistingUser_ThrowsUserNotFound() {
    // Arrange
    when(userRepository.findById(999)).thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(UserNotFound.class, () -> userService.delete(999));
  }
}