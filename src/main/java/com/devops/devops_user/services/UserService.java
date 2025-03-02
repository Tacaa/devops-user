package com.devops.devops_user.services;

import com.devops.devops_user.client.AccommodationClient;
import com.devops.devops_user.client.GatewayClient;
import com.devops.devops_user.dto.*;
import com.devops.devops_user.enumeration.Role;
import com.devops.devops_user.exceptions.*;
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

    @Autowired
    private AccommodationClient accommodationClient;

    @Autowired
    private GatewayClient gatewayClient;

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

        if(user == null){
            throw new UserNotFound("Given user does not exist.");
        }

        //provjera da li je guest ili host
        if(user.getRole() == Role.GUEST){
            //provjera ako nema rezervacija brise se, ukoliko ima error
            boolean guestHasReservation = accommodationClient.checkIsGuestHavingReservationAtMoment(user.getId());

            if(guestHasReservation){
                throw new UserCanNotBeDeleted("Guest has reservations in future");
            }
        }else{
            //host ukoliko nema rezervacija za svoje smjestaje brise se, u suprotnom error
            boolean hostHasReservation = accommodationClient.checkIsHostHavingReservationAtMoment(user.getId());

            if(hostHasReservation){
                throw new UserCanNotBeDeleted("Guest has reservations in future");
            }

           //brisi smjestaj
            accommodationClient.deleteAccommodationsOfHost(user.getId());
        }

        //disable user
        gatewayClient.disableUser(user.getId());

        user.setDeleted(true);
        return userRepository.save(user);

    }

}
