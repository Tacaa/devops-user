package  com.devops.devops_user.unit;

import com.devops.devops_user.client.GatewayClient;
import com.devops.devops_user.controllers.UserController;
import com.devops.devops_user.dto.*;
import com.devops.devops_user.enumeration.Role;
import com.devops.devops_user.exceptions.*;
import com.devops.devops_user.model.Address;
import com.devops.devops_user.model.User;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private GatewayClient gatewayClient;

    @InjectMocks
    private UserController userController;

    private User testUser;
    private List<User> userList;
    private CreateUserDTO createUserDTO;
    private UpdateUserDTO updateUserDTO;
    private Address address;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Create test data
        address = Address.builder()
                .id(1)
                .street("Test Street")
                .city("Test City")
                .country("Test Country")
                .number(123)
                .build();

        testUser = User.builder()
                .id(1)
                .firstName("John")
                .lastName("Doe")
                .username("johndoe")
                .email("john@example.com")
                .password("password")
                .role(Role.GUEST)
                .address(address)
                .deleted(false)
                .build();

        User secondUser = User.builder()
                .id(2)
                .firstName("Jane")
                .lastName("Doe")
                .username("janedoe")
                .email("jane@example.com")
                .password("password")
                .role(Role.HOST)
                .address(address)
                .deleted(false)
                .build();

        userList = new ArrayList<>();
        userList.add(testUser);
        userList.add(secondUser);

        // DTO setup
        AddressDTO addressDTO = AddressDTO.builder()
                .id(1)
                .street("Test Street")
                .city("Test City")
                .country("Test Country")
                .number(123)
                .build();

        CreateAddressDTO createAddressDTO = CreateAddressDTO.builder()
                .street("Test Street")
                .city("Test City")
                .country("Test Country")
                .number(123)
                .build();

        createUserDTO = CreateUserDTO.builder()
                .firstname("John")
                .lastname("Doe")
                .username("johndoe")
                .email("john@example.com")
                .password("password")
                .role(Role.GUEST)
                .address(createAddressDTO)
                .build();

        updateUserDTO = UpdateUserDTO.builder()
                .firstname("John")
                .lastname("Doe")
                .username("johndoe")
                .email("john@example.com")
                .password("password")
                .address(addressDTO)
                .build();
    }

    @Test
    void getUser_WhenUserExists_ReturnsUserDTO() {
        // Arrange
        when(userService.findUserById(1)).thenReturn(testUser);

        // Act
        ResponseEntity<UserDTO> response = userController.getUser(1);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(testUser.getId(), response.getBody().getId());
        assertEquals(testUser.getFirstName(), response.getBody().getFirstName());
        assertEquals(testUser.getLastName(), response.getBody().getLastName());
        assertEquals(testUser.getUsername(), response.getBody().getUsername());
        assertEquals(testUser.getEmail(), response.getBody().getEmail());
    }

    @Test
    void getUser_WhenUserDoesNotExist_ReturnsNotFound() {
        // Arrange
        when(userService.findUserById(999)).thenReturn(null);

        // Act
        ResponseEntity<UserDTO> response = userController.getUser(999);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void getAllUsers_ReturnsListOfUserDTOs() {
        // Arrange
        when(userService.findAllUsers()).thenReturn(userList);

        // Act
        ResponseEntity<List<UserDTO>> response = userController.getAllUsers();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
    }

    @Test
    void getUserPage_ReturnsPagedResponse() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> userPage = new PageImpl<>(userList, pageable, userList.size());
        when(userService.findAllUsers(pageable)).thenReturn(userPage);

        // Act
        ResponseEntity<PagedResponse<UserDTO>> response = userController.getUserPage(pageable);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().getContent().size());
        assertEquals(1, response.getBody().getTotalPages());
        assertEquals(2, response.getBody().getTotalElements());
    }

    @Test
    void filterUsers_WhenUsersFound_ReturnsUserDTOs() {
        // Arrange
        when(userService.findByFirstNameAndLastName("John", "Doe")).thenReturn(List.of(testUser));

        // Act
        ResponseEntity<List<UserDTO>> response = userController.filterUsers("John", "Doe");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void filterUsers_WhenNoUsersFound_ReturnsNotFound() {
        // Arrange
        when(userService.findByFirstNameAndLastName("Unknown", "Person")).thenReturn(new ArrayList<>());

        // Act
        ResponseEntity<List<UserDTO>> response = userController.filterUsers("Unknown", "Person");

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    void updateUser_WhenUserExists_ReturnsUpdatedUser() {
        // Arrange
        when(userService.update(1, updateUserDTO)).thenReturn(testUser);
        when(gatewayClient.updateUser(1, updateUserDTO)).thenReturn(true);

        // Act
        ResponseEntity<Map<String, Object>> response = userController.updateUser(1, updateUserDTO);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNull(response.getBody().get("message"));

        UserDTO returnedUser = (UserDTO) response.getBody().get("data");
        assertEquals(testUser.getId(), returnedUser.getId());
        assertEquals(testUser.getFirstName(), returnedUser.getFirstName());

        verify(gatewayClient, times(1)).updateUser(1, updateUserDTO);
    }

    @Test
    void updateUser_WhenUserNotFound_ReturnsNotFound() {
        // Arrange
        when(userService.update(999, updateUserDTO)).thenThrow(new UserNotFound("User does not exist"));

        // Act
        ResponseEntity<Map<String, Object>> response = userController.updateUser(999, updateUserDTO);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("User does not exist", response.getBody().get("message"));
        assertNull(response.getBody().get("data"));

        verify(gatewayClient, never()).updateUser(anyInt(), any(UpdateUserDTO.class));
    }

    @Test
    void updateUser_WhenAttributeNull_ReturnsBadRequest() {
        // Arrange
        when(userService.update(1, updateUserDTO))
                .thenThrow(new AttributeNullException("Given attribute is null"));

        // Act
        ResponseEntity<Map<String, Object>> response = userController.updateUser(1, updateUserDTO);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Given attribute is null", response.getBody().get("message"));
        assertNull(response.getBody().get("data"));
    }

    @Test
    void updateUser_WhenAttributeNotUnique_ReturnsBadRequest() {
        // Arrange
        when(userService.update(1, updateUserDTO))
                .thenThrow(new AttributeNotUniqueException("Username not unique"));

        // Act
        ResponseEntity<Map<String, Object>> response = userController.updateUser(1, updateUserDTO);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Username not unique", response.getBody().get("message"));
        assertNull(response.getBody().get("data"));
    }

    @Test
    void updateUser_WhenAddressNotFound_ReturnsNotFound() {
        // Arrange
        when(userService.update(1, updateUserDTO))
                .thenThrow(new AddressNotFound("Given address is not correct."));

        // Act
        ResponseEntity<Map<String, Object>> response = userController.updateUser(1, updateUserDTO);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Given address is not correct.", response.getBody().get("message"));
        assertNull(response.getBody().get("data"));
    }

    @Test
    void save_WhenValidInput_ReturnsTrue() {
        // Arrange
        when(userService.save(createUserDTO)).thenReturn(testUser);

        // Act
        Boolean result = userController.save(createUserDTO);

        // Assert
        assertTrue(result);
    }

    @Test
    void register_WhenValidInput_ReturnsCreatedUser() {
        // Arrange
        when(userService.save(createUserDTO)).thenReturn(testUser);

        // Act
        ResponseEntity<Map<String, Object>> response = userController.create(createUserDTO);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNull(response.getBody().get("message"));

        UserDTO returnedUser = (UserDTO) response.getBody().get("data");
        assertEquals(testUser.getId(), returnedUser.getId());
        assertEquals(testUser.getFirstName(), returnedUser.getFirstName());
    }

    @Test
    void register_WhenAttributeNull_ReturnsBadRequest() {
        // Arrange
        when(userService.save(createUserDTO))
                .thenThrow(new AttributeNullException("Given attribute is null"));

        // Act
        ResponseEntity<Map<String, Object>> response = userController.create(createUserDTO);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Given attribute is null", response.getBody().get("message"));
        assertNull(response.getBody().get("data"));
    }

    @Test
    void register_WhenAttributeNotUnique_ReturnsBadRequest() {
        // Arrange
        when(userService.save(createUserDTO))
                .thenThrow(new AttributeNotUniqueException("Username not unique"));

        // Act
        ResponseEntity<Map<String, Object>> response = userController.create(createUserDTO);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Username not unique", response.getBody().get("message"));
        assertNull(response.getBody().get("data"));
    }

    @Test
    void delete_WhenUserExists_ReturnsDeletedUser() {
        // Arrange
        when(userService.delete(1)).thenReturn(testUser);

        // Act
        ResponseEntity<Map<String, Object>> response = userController.delete(1);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNull(response.getBody().get("message"));

        UserDTO returnedUser = (UserDTO) response.getBody().get("data");
        assertEquals(testUser.getId(), returnedUser.getId());
    }

    @Test
    void delete_WhenUserNotFound_ReturnsNotFound() {
        // Arrange
        when(userService.delete(999)).thenThrow(new UserNotFound("Given user does not exist."));

        // Act
        ResponseEntity<Map<String, Object>> response = userController.delete(999);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Given user does not exist.", response.getBody().get("message"));
        assertNull(response.getBody().get("data"));
    }

    @Test
    void delete_WhenUserCannotBeDeleted_ReturnsBadRequest() {
        // Arrange
        when(userService.delete(1)).thenThrow(new UserCanNotBeDeleted("Guest has reservations in future"));

        // Act
        ResponseEntity<Map<String, Object>> response = userController.delete(1);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Guest has reservations in future", response.getBody().get("message"));
        assertNull(response.getBody().get("data"));
    }
}