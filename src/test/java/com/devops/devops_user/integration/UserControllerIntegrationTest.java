package com.devops.devops_user.integration;

import com.devops.devops_user.client.AccommodationClient;
import com.devops.devops_user.client.GatewayClient;
import com.devops.devops_user.dto.AddressDTO;
import com.devops.devops_user.dto.CreateAddressDTO;
import com.devops.devops_user.dto.CreateUserDTO;
import com.devops.devops_user.dto.UpdateUserDTO;
import com.devops.devops_user.enumeration.Role;
import com.devops.devops_user.model.User;
import com.devops.devops_user.repository.AddressRepository;
import com.devops.devops_user.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private GatewayClient gatewayClient;

    @MockBean
    private AccommodationClient accommodationClient;

    private CreateUserDTO createUserDTO;
    private UpdateUserDTO updateUserDTO;

    @BeforeEach
    void setUp() {
        // Clear repositories - this is safer than deleteAll() as it happens within a transaction
        userRepository.deleteAllInBatch();
        addressRepository.deleteAll();

        // Mock the gateway client responses
        when(gatewayClient.updateUser(anyInt(), any(UpdateUserDTO.class))).thenReturn(true);
        when(gatewayClient.disableUser(anyInt())).thenReturn(true);

        // Mock the accommodation client responses
        when(accommodationClient.checkIsGuestHavingReservationAtMoment(anyInt())).thenReturn(false);
        when(accommodationClient.checkIsHostHavingReservationAtMoment(anyInt())).thenReturn(false);
        when(accommodationClient.deleteAccommodationsOfHost(anyInt())).thenReturn(true);

        // Setup test data
        CreateAddressDTO addressDTO = new CreateAddressDTO();
        addressDTO.setStreet("Test Street");
        addressDTO.setCity("Test City");
        addressDTO.setCountry("Test Country");
        addressDTO.setNumber(123);

        createUserDTO = CreateUserDTO.builder()
                .firstname("John")
                .lastname("Doe")
                .username("johndoe")
                .password("password")
                .email("john@example.com")
                .role(Role.GUEST)
                .address(addressDTO)
                .build();
    }



    @Test
    void getNonExistentUser_ReturnsNotFound() throws Exception {
        // Attempt to get a user that doesn't exist
        mockMvc.perform(get("/api/user/999"))
                .andExpect(status().isNotFound());
    }

}