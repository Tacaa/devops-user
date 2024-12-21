package com.devops.devops_user.services;

import com.devops.devops_user.dto.*;
import com.devops.devops_user.exceptions.AddressNotFound;
import com.devops.devops_user.exceptions.AttributeNotUniqueException;
import com.devops.devops_user.exceptions.AttributeNullException;
import com.devops.devops_user.exceptions.UserNotFound;
import com.devops.devops_user.model.Address;
import com.devops.devops_user.model.User;
import com.devops.devops_user.repository.AddressRepository;
import com.devops.devops_user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.naming.directory.AttributeInUseException;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AddressRepository addressRepository;

    public User findUserById(Integer id){
        return userRepository.findById(id).orElse(null);
    }

    public List<User> findByFirstNameAndLastName(String firstName, String lastName) {
        return userRepository.findByFirstNameAndLastNameAllIgnoringCase(firstName, lastName);
    }

    public List<User> findAllUsers(){
        return userRepository.findAll();
    }

    public Page<User> findAllUsers(Pageable page) {
        return userRepository.findAll(page);
    }

    public User save(CreateUserDTO createUserDTO){
        if(createUserDTO.getFirstName() == null || createUserDTO.getLastName() == null || createUserDTO.getUsername() == null
        || createUserDTO.getPassword() == null || createUserDTO.getEmail() == null || createUserDTO.getRole() == null
        || createUserDTO.getAddress() == null){
            throw new AttributeNullException("Given attribute is null");
        }

        User notUniqueUser = userRepository.findByUsername(createUserDTO.getUsername());
        if(notUniqueUser != null){
            throw new AttributeNotUniqueException("Username not unique");
        }

        notUniqueUser = userRepository.findByEmail(createUserDTO.getEmail());
        if(notUniqueUser != null){
            throw new AttributeNotUniqueException("Email not unique");
        }

        Address address = null;
        if (createUserDTO.getAddress() != null) {
            address = CreateAddressDTO.from(createUserDTO.getAddress());
        }

        // Create the User object using the builder pattern
        User user = User.builder()
                .firstName(createUserDTO.getFirstName())
                .lastName(createUserDTO.getLastName())
                .username(createUserDTO.getUsername())
                .email(createUserDTO.getEmail())
                .role(createUserDTO.getRole())
                .address(address)
                .deleted(false)
                .password(createUserDTO.getPassword())
                .rates(new HashSet<>())
                .build();

        return this.userRepository.save(user);
    }

    public User update(Integer userId, UpdateUserDTO updateUserDTO){
        if(updateUserDTO.getFirstName() == null || updateUserDTO.getLastName() == null || updateUserDTO.getUsername() == null
                || updateUserDTO.getPassword() == null || updateUserDTO.getEmail() == null
                || updateUserDTO.getAddress() == null){
            throw new AttributeNullException("Given attribute is null");
        }

        User user = userRepository.findById(userId).orElse(null);

        if(user == null){
            throw new AddressNotFound("Given address is not correct.");
        }

        User notUniqueUser = userRepository.findByUsername(updateUserDTO.getUsername());
        if(notUniqueUser != null && notUniqueUser.getUsername().equals(user.getUsername())){
            throw new AttributeNotUniqueException("Username not unique");
        }

        notUniqueUser = userRepository.findByEmail(updateUserDTO.getEmail());
        if(notUniqueUser != null && notUniqueUser.getEmail().equals(user.getEmail())){
            throw new AttributeNotUniqueException("Email not unique");
        }


        Address address = addressRepository.findById(updateUserDTO.getAddress().getId()).orElse(null);

        if(address != null){
            address = AddressDTO.from(updateUserDTO.getAddress());
        }else{
            throw new AddressNotFound("Given address is not correct.");
        }




            user = User.builder()
                    .firstName(updateUserDTO.getFirstName())
                    .lastName(updateUserDTO.getLastName())
                    .username(updateUserDTO.getUsername())
                    .email(updateUserDTO.getEmail())
                    .deleted(user.getDeleted())
                    .role(user.getRole())
                    .password(updateUserDTO.getPassword())
                    .address(address)
                    .build();
            return this.userRepository.save(user);

    }


    public User delete(Integer userId){
        User user = userRepository.findById(userId).orElse(null);

        if(user != null){
          user.setDeleted(true);
          return userRepository.save(user);
        }else{
            throw new UserNotFound("Given user does not exist.");
        }

    }

}
