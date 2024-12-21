package com.devops.devops_user.controllers;


import com.devops.devops_user.dto.CreateUserDTO;
import com.devops.devops_user.dto.PagedResponse;
import com.devops.devops_user.dto.UpdateUserDTO;
import com.devops.devops_user.dto.UserDTO;
import com.devops.devops_user.exceptions.AddressNotFound;
import com.devops.devops_user.exceptions.AttributeNotUniqueException;
import com.devops.devops_user.exceptions.AttributeNullException;
import com.devops.devops_user.exceptions.UserNotFound;
import com.devops.devops_user.model.User;
import com.devops.devops_user.services.UserService;
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

@RestController
@RequestMapping(value = "/api/devops-user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping(value = "/{id}")
    public ResponseEntity<UserDTO> getUser(@PathVariable Integer id) {
        User user = userService.findUserById(id);

        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        UserDTO userDTO = UserDTO.from(user);
        return new ResponseEntity<>(userDTO, HttpStatus.OK);
    }


    @GetMapping(value = "/all")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<User> users = userService.findAllUsers();

        List<UserDTO> userDTOS = new ArrayList<>();
        for (User user : users) {
            userDTOS.add(UserDTO.from(user));
        }

        return new ResponseEntity<>(userDTOS, HttpStatus.OK);
    }



    // /api/users?page=0&size=5&sort=firstName,DESC
    @GetMapping
    public ResponseEntity<PagedResponse<UserDTO>> getUserPage(Pageable page) {
        Page<User> users = userService.findAllUsers(page);

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


    // /api/users/search?firstName=Fiona&lastName=Gray
    @GetMapping(value = "/search")
    public ResponseEntity<List<UserDTO>> filterUsers(@RequestParam(required = false) String firstName,
                                                              @RequestParam(required = false) String lastName)
    {
        List<User> users = userService.findByFirstNameAndLastName(firstName,lastName);

        List<UserDTO> userDTOS = new ArrayList<>();
        for (User user : users) {
            userDTOS.add(UserDTO.from(user));
        }

        return new ResponseEntity<>(userDTOS, HttpStatus.OK);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<Map<String, Object>> updateUser(@PathVariable Integer id, @RequestBody UpdateUserDTO updateUserDTO){
        try {
            User user = userService.update(id, updateUserDTO);
            Map<String, Object> response = new HashMap<>();
            response.put("message", null);
            response.put("data", UserDTO.from(user));
            return new ResponseEntity<>(response, HttpStatus.OK);

        }catch (UserNotFound | AddressNotFound e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", e.getMessage());
            response.put("data", null);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);

        } catch (AttributeNotUniqueException | AttributeNullException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", e.getMessage());
            response.put("data", null);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(value = "/{id}")
    public ResponseEntity<Map<String, Object>> create(@RequestBody CreateUserDTO createUserDTO){
        try {
            User user = userService.save(createUserDTO);
            Map<String, Object> response = new HashMap<>();
            response.put("message", null);
            response.put("data", UserDTO.from(user));
            return new ResponseEntity<>(response, HttpStatus.CREATED);

        } catch (AttributeNotUniqueException | AttributeNullException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", e.getMessage());
            response.put("data", null);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable Integer id){
        try {
            User user = userService.delete(id);
            Map<String, Object> response = new HashMap<>();
            response.put("message", null);
            response.put("data", UserDTO.from(user));
            return new ResponseEntity<>(response, HttpStatus.OK);

        }catch (UserNotFound e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", e.getMessage());
            response.put("data", null);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);

        }
    }

}
