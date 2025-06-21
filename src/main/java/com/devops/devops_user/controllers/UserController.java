package com.devops.devops_user.controllers;


import com.devops.devops_user.client.GatewayClient;
import com.devops.devops_user.dto.CreateUserDTO;
import com.devops.devops_user.dto.PagedResponse;
import com.devops.devops_user.dto.UpdateUserDTO;
import com.devops.devops_user.dto.UserDTO;
import com.devops.devops_user.exceptions.*;
import com.devops.devops_user.model.User;
import com.devops.devops_user.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping(value = "/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private GatewayClient gatewayClient;


    @GetMapping(value = "/{id}")
    public ResponseEntity<UserDTO> getUser(@PathVariable Integer id) {
        log.info("Received GET request for user with id: {}", id);
        User user = userService.findUserById(id);

        if (user == null) {
            log.warn("User with id {} not found", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        UserDTO userDTO = UserDTO.from(user);
        log.info("Successfully retrieved user with id: {}", id);
        return new ResponseEntity<>(userDTO, HttpStatus.OK);
    }


    @GetMapping(value = "/all")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        log.info("Received GET request to fetch all users");
        List<User> users = userService.findAllUsers();

        log.info("Found {} users in total", users.size());
        List<UserDTO> userDTOS = new ArrayList<>();
        for (User user : users) {
            userDTOS.add(UserDTO.from(user));
        }

        return new ResponseEntity<>(userDTOS, HttpStatus.OK);
    }



    // /api/user?page=0&size=5&sort=firstName
    @GetMapping
    public ResponseEntity<PagedResponse<UserDTO>> getUserPage(Pageable page) {
        log.info("Received GET request for paged users - page: {}, size: {}, sort: {}",
                page.getPageNumber(), page.getPageSize(), page.getSort());
        Page<User> users = userService.findAllUsers(page);

        log.info("Fetched {} users on page {}", users.getNumberOfElements(), page.getPageNumber());
        List<UserDTO> usersContent = new ArrayList<>();
        for (User user : users) {
            usersContent.add(UserDTO.from(user));
        }

        PagedResponse<UserDTO> response = new PagedResponse<>(
                usersContent,
                users.getTotalPages(),
                users.getTotalElements()
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    // /api/user/search?firstName=John&lastName=Doe
    @GetMapping(value = "/search")
    public ResponseEntity<List<UserDTO>> filterUsers(@RequestParam(required = false) String firstName,
                                                              @RequestParam(required = false) String lastName)
    {
        log.info("Received GET request to filter users - firstName: {}, lastName: {}", firstName, lastName);
        List<User> users = userService.findByFirstNameAndLastName(firstName,lastName);

        List<UserDTO> userDTOS = new ArrayList<>();
        for (User user : users) {
            userDTOS.add(UserDTO.from(user));
        }

        if(userDTOS.isEmpty()){
            log.warn("No users found for search criteria: firstName={}, lastName={}", firstName, lastName);
            return new ResponseEntity<>(userDTOS, HttpStatus.NOT_FOUND);
        }

        log.info("Found {} users matching search criteria", userDTOS.size());
        return new ResponseEntity<>(userDTOS, HttpStatus.OK);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<Map<String, Object>> updateUser(@PathVariable("id") Integer id, @RequestBody UpdateUserDTO updateUserDTO){
        try {
            log.info("Received PUT request to update user with id: {}", id);
            User user = userService.update(id, updateUserDTO);

            //ovdje treba pozvati gateway
            gatewayClient.updateUser(id, updateUserDTO);

            Map<String, Object> response = new HashMap<>();
            response.put("message", null);
            response.put("data", UserDTO.from(user));
            log.info("Successfully updated user with id: {}", id);
            return new ResponseEntity<>(response, HttpStatus.OK);

        }catch (UserNotFound | AddressNotFound e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", e.getMessage());
            response.put("data", null);
            log.warn("User update failed - {}: {}", e.getClass().getSimpleName(), e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);

        } catch (AttributeNotUniqueException | AttributeNullException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", e.getMessage());
            response.put("data", null);
            log.warn("User update validation error - {}: {}", e.getClass().getSimpleName(), e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping({"/save"})
    public Boolean save(@RequestBody CreateUserDTO createUserDTO){
        log.info("Received POST request to save new user");
        return userService.save(createUserDTO).getId() != null;
    }

    @PostMapping({"/register"})
    public ResponseEntity<Map<String, Object>> create(@RequestBody CreateUserDTO createUserDTO){
        log.info("Received POST request to register new user");
        try {
            User user = userService.save(createUserDTO);
            log.info("User registered successfully with id: {}", user.getId());
            Map<String, Object> response = new HashMap<>();
            response.put("message", null);
            response.put("data", UserDTO.from(user));
            return new ResponseEntity<>(response, HttpStatus.CREATED);

        } catch (AttributeNotUniqueException | AttributeNullException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", e.getMessage());
            response.put("data", null);
            log.warn("User registration failed - {}: {}", e.getClass().getSimpleName(), e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable Integer id){
        log.info("Received DELETE request for user with id: {}", id);
        try {
            User user = userService.delete(id);
            log.info("User with id {} deleted successfully", id);
            Map<String, Object> response = new HashMap<>();
            response.put("message", null);
            response.put("data", UserDTO.from(user));
            return new ResponseEntity<>(response, HttpStatus.OK);

        }catch (UserNotFound e) {
            log.warn("Delete failed - User not found with id: {}", id);
            Map<String, Object> response = new HashMap<>();
            response.put("message", e.getMessage());
            response.put("data", null);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);

        }catch (UserCanNotBeDeleted e) {
            log.warn("Delete failed - User cannot be deleted: {}", e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("message", e.getMessage());
            response.put("data", null);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);

        }
    }

}
