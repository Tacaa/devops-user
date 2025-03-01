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
        Address address = null;
        if (createUserDTO.getAddress() != null) {
            address = CreateAddressDTO.from(createUserDTO.getAddress());
        }

        User user = User.builder()
                .firstName(createUserDTO.getFirstname())
                .lastName(createUserDTO.getLastname())
                .username(createUserDTO.getUsername())
                .email(createUserDTO.getEmail())
                .role(createUserDTO.getRole())
                .address(address)
                .deleted(false)
                .password(createUserDTO.getPassword())
                .build();

        return this.userRepository.save(user);
    }

    public User update(Integer userId, UpdateUserDTO updateUserDTO){
        System.out.println(updateUserDTO.getFirstname());
        System.out.println(updateUserDTO.getLastname());
        System.out.println(updateUserDTO.getUsername());
        System.out.println(updateUserDTO.getPassword());
        System.out.println(updateUserDTO.getEmail());
        System.out.println(updateUserDTO.getAddress());

        if(updateUserDTO.getFirstname() == null || updateUserDTO.getLastname() == null || updateUserDTO.getUsername() == null
                || updateUserDTO.getPassword() == null || updateUserDTO.getEmail() == null
                || updateUserDTO.getAddress() == null){
            throw new AttributeNullException("Given attribute is null");
        }

        User user = userRepository.findById(userId).orElse(null);

        if(user == null){
            throw new UserNotFound("User does not exist");
        }

        User notUniqueUser = userRepository.findByUsername(updateUserDTO.getUsername());
        if(notUniqueUser != null) {
            System.out.println(notUniqueUser.getId());
            System.out.println(user.getId());
            if (notUniqueUser.getId() != user.getId()) {
                throw new AttributeNotUniqueException("Username not unique");
            }
        }

        notUniqueUser = userRepository.findByEmail(updateUserDTO.getEmail());

        if(notUniqueUser != null){
            if(notUniqueUser.getId() != user.getId()) {
                throw new AttributeNotUniqueException("Username not unique");
            }
        }

        Address address = addressRepository.findById(updateUserDTO.getAddress().getId()).orElse(null);

        if(address == null){
            throw new AddressNotFound("Given address is not correct.");
        }

        address.setStreet(updateUserDTO.getAddress().getStreet());
        address.setCity(updateUserDTO.getAddress().getCity());
        address.setCountry(updateUserDTO.getAddress().getCountry());
        address.setNumber(updateUserDTO.getAddress().getNumber());

        user.setFirstName(updateUserDTO.getFirstname());
        user.setLastName(updateUserDTO.getLastname());
        user.setUsername(updateUserDTO.getUsername());
        user.setPassword(updateUserDTO.getPassword());
        user.setEmail(updateUserDTO.getEmail());

        user.setAddress(address);

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
