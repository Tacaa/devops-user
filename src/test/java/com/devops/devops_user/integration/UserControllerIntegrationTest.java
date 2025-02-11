package com.devops.devops_user.integration;

import com.devops.devops_user.dto.AddressDTO;
import com.devops.devops_user.dto.CreateAddressDTO;
import com.devops.devops_user.dto.CreateUserDTO;
import com.devops.devops_user.dto.UpdateUserDTO;
import com.devops.devops_user.enumeration.Role;
import com.devops.devops_user.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private CreateUserDTO createUserDTO;
    private UpdateUserDTO updateUserDTO;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

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
    }

    @Test
    void createAndGetUser() throws Exception {
        // Create user
        String createResponse = mockMvc.perform(post("/api/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createUserDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.firstName").value("John"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Extract ID from response
        Integer userId = objectMapper.readTree(createResponse)
                .get("data")
                .get("id")
                .asInt();

        // Get created user
        mockMvc.perform(get("/api/user/" + userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"));
    }

    @Test
    void updateUser() throws Exception {
        // First create a user
        String createResponse = mockMvc.perform(post("/api/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createUserDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Integer userId = objectMapper.readTree(createResponse)
                .get("data")
                .get("id")
                .asInt();

        Integer addressId = objectMapper.readTree(createResponse)
                .get("data")
                .get("address")
                .get("id")
                .asInt();

        updateUserDTO = new UpdateUserDTO();
        updateUserDTO.setLastName(createUserDTO.getLastName());
        updateUserDTO.setPassword(createUserDTO.getPassword());
        updateUserDTO.setUsername(createUserDTO.getUsername());
        updateUserDTO.setEmail(createUserDTO.getEmail());

        AddressDTO updateAddress = new AddressDTO();
        updateAddress.setId(addressId);
        updateAddress.setCity(createUserDTO.getAddress().getCity());
        updateAddress.setCountry(createUserDTO.getAddress().getCountry());
        updateAddress.setNumber(createUserDTO.getAddress().getNumber());
        updateAddress.setStreet(createUserDTO.getAddress().getStreet());
        updateUserDTO.setAddress(updateAddress);

        updateUserDTO.setFirstName("Jane");
        mockMvc.perform(put("/api/user/" + userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUserDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.firstName").value("Jane"));
    }

    @Test
    void deleteUser() throws Exception {
        // First create a user
        String createResponse = mockMvc.perform(post("/api/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createUserDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Integer userId = objectMapper.readTree(createResponse)
                .get("data")
                .get("id")
                .asInt();

        // Delete the user
        mockMvc.perform(delete("/api/user/" + userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.deleted").value(true));
    }

    @Test
    void getAllUsers() throws Exception {
        // Create a user first
        mockMvc.perform(post("/api/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createUserDTO)))
                .andExpect(status().isCreated());

        // Get all users
        mockMvc.perform(get("/api/user/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].firstName").value("John"))
                .andExpect(jsonPath("$[0].lastName").value("Doe"));
    }

    @Test
    void getPaginatedUsers() throws Exception {
        // Create a user first
        mockMvc.perform(post("/api/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createUserDTO)))
                .andExpect(status().isCreated());

        // Get paginated users
        mockMvc.perform(get("/api/user?page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].firstName").value("John"))
                .andExpect(jsonPath("$.totalPages").exists())
                .andExpect(jsonPath("$.totalElements").exists());
    }
}