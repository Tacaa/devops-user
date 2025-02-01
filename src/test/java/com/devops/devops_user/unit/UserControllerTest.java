package com.devops.devops_user.unit;

import com.devops.devops_user.controllers.UserController;
import com.devops.devops_user.dto.*;
import com.devops.devops_user.enumeration.Role;
import com.devops.devops_user.exceptions.UserNotFound;
import com.devops.devops_user.model.User;
import com.devops.devops_user.services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;
    private CreateUserDTO createUserDTO;
    private UpdateUserDTO updateUserDTO;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1)
                .firstName("John")
                .lastName("Doe")
                .username("johndoe")
                .email("john@example.com")
                .role(Role.GUEST)
                .deleted(false)
                .build();

        CreateAddressDTO addressDTO = new CreateAddressDTO();
        addressDTO.setStreet("Test Street");
        addressDTO.setCity("Test City");
        addressDTO.setCountry("Test Country");
        addressDTO.setNumber(123);

        createUserDTO = new CreateUserDTO();
        createUserDTO.setFirstName("John");
        createUserDTO.setLastName("Doe");
        createUserDTO.setUsername("johndoe");
        createUserDTO.setPassword("password");
        createUserDTO.setEmail("john@example.com");
        createUserDTO.setRole(Role.GUEST);
        createUserDTO.setAddress(addressDTO);

        AddressDTO updateAddressDTO = new AddressDTO();
        updateAddressDTO.setId(1);
        updateAddressDTO.setStreet("Test Street");
        updateAddressDTO.setCity("Test City");
        updateAddressDTO.setCountry("Test Country");
        updateAddressDTO.setNumber(123);

        updateUserDTO = new UpdateUserDTO();
        updateUserDTO.setFirstName("John");
        updateUserDTO.setLastName("Doe");
        updateUserDTO.setUsername("johndoe");
        updateUserDTO.setPassword("password");
        updateUserDTO.setEmail("john@example.com");
        updateUserDTO.setAddress(updateAddressDTO);
    }

    @Test
    void getUserById_Success() throws Exception {
        when(userService.findUserById(1)).thenReturn(testUser);

        mockMvc.perform(get("/api/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"));
    }

    @Test
    void getUserById_NotFound() throws Exception {
        when(userService.findUserById(999)).thenReturn(null);

        mockMvc.perform(get("/api/user/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllUsers_Success() throws Exception {
        List<User> users = Arrays.asList(testUser);
        when(userService.findAllUsers()).thenReturn(users);

        mockMvc.perform(get("/api/user/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].firstName").value("John"))
                .andExpect(jsonPath("$[0].lastName").value("Doe"));
    }

    @Test
    void createUser_Success() throws Exception {
        when(userService.save(any(CreateUserDTO.class))).thenReturn(testUser);

        mockMvc.perform(post("/api/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createUserDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.firstName").value("John"))
                .andExpect(jsonPath("$.data.lastName").value("Doe"));
    }

    @Test
    void updateUser_Success() throws Exception {
        when(userService.update(eq(1), any(UpdateUserDTO.class))).thenReturn(testUser);

        mockMvc.perform(put("/api/user/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUserDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.firstName").value("John"))
                .andExpect(jsonPath("$.data.lastName").value("Doe"));
    }

    @Test
    void updateUser_NotFound() throws Exception {
        when(userService.update(eq(999), any(UpdateUserDTO.class)))
                .thenThrow(new UserNotFound("User not found"));

        mockMvc.perform(put("/api/user/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUserDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User not found"));
    }

    @Test
    void deleteUser_Success() throws Exception {
        testUser.setDeleted(true);
        when(userService.delete(1)).thenReturn(testUser);

        mockMvc.perform(delete("/api/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.deleted").value(true));
    }

    @Test
    void getUserPage_Success() throws Exception {
        List<User> users = Arrays.asList(testUser);
        Page<User> page = new PageImpl<>(users);
        when(userService.findAllUsers(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/user?page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].firstName").value("John"))
                .andExpect(jsonPath("$.totalPages").value(1));
    }
}