package com.devops.devops_user.integration;

import com.devops.devops_user.controllers.UserController;
import com.devops.devops_user.dto.*;
import com.devops.devops_user.enumeration.Role;
import com.devops.devops_user.model.Address;
import com.devops.devops_user.model.User;
import com.devops.devops_user.services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
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

  private CreateAddressDTO createTestAddressDTO() {
    return CreateAddressDTO.builder()
        .street("Street 123")
        .number(10)
        .city("City")
        .country("Country")
        .build();
  }

  private AddressDTO createTestAddressDTOForUpdate() {
    return new AddressDTO(1, "Street 456", 42, "New City", "New Country");
  }

  private User createTestUser() {
    User user = new User();
    user.setId(1);
    user.setFirstName("Nemanja");
    user.setLastName("Vojinovic");
    user.setEmail("email@gmail.com");
    user.setUsername("vojin");
    user.setPassword("password");
    user.setRole(Role.HOST);
    user.setDeleted(false);
    return user;
  }

  private Address createTestAddress() {
    return Address.builder()
        .street("Street 456")
        .number(42)
        .city("New City")
        .country("New Country")
        .build();
  }

  @Test
  void testCreateUser() throws Exception {
    CreateUserDTO createUserDTO = new CreateUserDTO(
        "Nemanja", "Vojinovic", "vojin", "password", "email@gmail.com", Role.HOST, createTestAddressDTO()
    );
    User user = createTestUser();

    when(userService.save(any(CreateUserDTO.class))).thenReturn(user);

    mockMvc.perform(post("/api/user")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(createUserDTO)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.data.firstName").value("Nemanja"))
        .andExpect(jsonPath("$.data.role").value("HOST"));
  }

  @Test
  void testUpdateUser() throws Exception {
    UpdateUserDTO updateUserDTO = new UpdateUserDTO(
        "Updated", "User", "updateduser", "newpassword", "updated.email@gmail.com", createTestAddressDTOForUpdate()
    );

    // Create a test User object with a populated Address
    User updatedUser = createTestUser();
    updatedUser.setFirstName("Updated");
    updatedUser.setLastName("User");
    updatedUser.setAddress(createTestAddress()); // Ensure the address is not null

    when(userService.update(Mockito.eq(1), any(UpdateUserDTO.class))).thenReturn(updatedUser);

    mockMvc.perform(put("/api/user/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updateUserDTO)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.firstName").value("Updated"))
        .andExpect(jsonPath("$.data.address.street").value("Street 456"))
        .andExpect(jsonPath("$.data.address.number").value(42))
        .andExpect(jsonPath("$.data.address.city").value("New City"))
        .andExpect(jsonPath("$.data.address.country").value("New Country"));
  }


}
