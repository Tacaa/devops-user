package com.devops.devops_user.services;

import com.devops.devops_user.dto.*;
import com.devops.devops_user.model.Address;
import com.devops.devops_user.model.User;
import com.devops.devops_user.repository.AddressRepository;
import com.devops.devops_user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public User save(CreateUserDTO createUserDTO){
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
                .password(createUserDTO.getPassword())
                .rates(new HashSet<>())
                .build();

        return this.userRepository.save(user);
    }

    public User update(Integer userId, UpdateUserDTO updateUserDTO){
        Address address = addressRepository.findById(updateUserDTO.getAddress().getId()).orElse(null);

        if(address != null){
            address = AddressDTO.from(updateUserDTO.getAddress());
        }else{
            //todo: obrada greske
        }

        User user = userRepository.findById(userId).orElse(null);

        if(user != null){
            user = User.builder()
                    .firstName(updateUserDTO.getFirstName())
                    .lastName(updateUserDTO.getLastName())
                    .username(updateUserDTO.getUsername())
                    .email(updateUserDTO.getEmail())
                    .password(updateUserDTO.getPassword())
                    .address(address)
                    .build();
            return this.userRepository.save(user);
        }else{
            //todo: obrada greske
        }

        return null;
    }


    public User delete(Integer userId){
        User user = userRepository.findById(userId).orElse(null);

        if(user != null){
          userRepository.delete(user);
        }else{
            //todo: obrada greske
        }

        return null;
    }

}
