package com.devops.devops_user.services;

import com.devops.devops_user.client.AccommodationClient;
import com.devops.devops_user.client.GatewayClient;
import com.devops.devops_user.client.NotificationClient;
import com.devops.devops_user.dto.*;
import com.devops.devops_user.enumeration.Role;
import com.devops.devops_user.exceptions.*;
import com.devops.devops_user.model.Address;
import com.devops.devops_user.model.User;
import com.devops.devops_user.repository.AddressRepository;
import com.devops.devops_user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
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

    @Autowired
    private NotificationClient notificationClient;

    public User findUserById(Integer id){
        log.debug("Looking up user by id: {}", id);
        return userRepository.findById(id).orElse(null);
    }

    public List<User> findByFirstNameAndLastName(String firstName, String lastName) {
        log.debug("Searching users by firstName='{}' and lastName='{}'", firstName, lastName);
        return userRepository.findByFirstNameAndLastNameAllIgnoringCase(firstName, lastName);
    }

    public List<User> findAllUsers(){
        log.info("Fetching all users");
        return userRepository.findAll();
    }

    public Page<User> findAllUsers(Pageable page) {
        log.info("Fetching users page - pageNumber: {}, size: {}, sort: {}",
                page.getPageNumber(), page.getPageSize(), page.getSort());
        return userRepository.findAll(page);
    }

    public User save(CreateUserDTO createUserDTO){
        log.info("Saving new user with username: {}", createUserDTO.getUsername());
        Address address = null;
        if (createUserDTO.getAddress() != null) {
            log.debug("Creating address from DTO");
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

        user = this.userRepository.save(user);
        log.info("User saved with id: {}", user.getId());

        //kreiraj preference za notifikacije
        if(user.getRole().equals(Role.GUEST)){
            CreateNotificationsPreferencesDTO preferencesDTO = CreateNotificationsPreferencesDTO.builder().userId(user.getId()).isGuest(true).build();
            notificationClient.saveNotificationsPreferences(preferencesDTO);
        }else{
            CreateNotificationsPreferencesDTO preferencesDTO = CreateNotificationsPreferencesDTO.builder().userId(user.getId()).isGuest(false).build();
            notificationClient.saveNotificationsPreferences(preferencesDTO);
        }
        log.debug("Notification preferences saved for user id: {}", user.getId());

        return user;
    }

    public User update(Integer userId, UpdateUserDTO updateUserDTO){
        log.info("Updating user with id: {}", userId);

        if(updateUserDTO.getFirstname() == null || updateUserDTO.getLastname() == null || updateUserDTO.getUsername() == null
                || updateUserDTO.getPassword() == null || updateUserDTO.getEmail() == null
                || updateUserDTO.getAddress() == null){
            log.warn("Validation failed: some fields in UpdateUserDTO are null");
            throw new AttributeNullException("Given attribute is null");
        }

        User user = userRepository.findById(userId).orElse(null);

        if(user == null){
            log.warn("User with id {} not found", userId);
            throw new UserNotFound("User does not exist");
        }

        User notUniqueUser = userRepository.findByUsername(updateUserDTO.getUsername());
        if(notUniqueUser != null) {
            log.warn("Username '{}' already taken by user id: {}", updateUserDTO.getUsername(), notUniqueUser.getId());
            if (notUniqueUser.getId() != user.getId()) {
                throw new AttributeNotUniqueException("Username not unique");
            }
        }

        notUniqueUser = userRepository.findByEmail(updateUserDTO.getEmail());

        if(notUniqueUser != null){
            if(notUniqueUser.getId() != user.getId()) {
                log.warn("Email '{}' already taken by user id: {}", updateUserDTO.getEmail(), notUniqueUser.getId());
                throw new AttributeNotUniqueException("Email not unique");
            }
        }

        Address address = addressRepository.findById(updateUserDTO.getAddress().getId()).orElse(null);

        if(address == null){
            log.warn("Address with id {} not found", updateUserDTO.getAddress().getId());
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
        log.info("User updated successfully");
        return this.userRepository.save(user);

    }


    public User delete(Integer userId){
        log.info("Attempting to delete user with id: {}", userId);
        User user = userRepository.findById(userId).orElse(null);

        if(user == null){
            log.warn("User with id {} not found", userId);
            throw new UserNotFound("Given user does not exist.");
        }

        //provjera da li je guest ili host
        if(user.getRole() == Role.GUEST){
            //provjera ako nema rezervacija brise se, ukoliko ima error
            boolean guestHasReservation = accommodationClient.checkIsGuestHavingReservationAtMoment(user.getId());

            if(guestHasReservation){
                log.warn("Guest with id {} has future reservations", userId);
                throw new UserCanNotBeDeleted("Guest has reservations in future");
            }
        }else{
            //host ukoliko nema rezervacija za svoje smjestaje brise se, u suprotnom error
            boolean hostHasReservation = accommodationClient.checkIsHostHavingReservationAtMoment(user.getId());

            if(hostHasReservation){
                log.warn("Host with id {} has future reservations", userId);
                throw new UserCanNotBeDeleted("Host has reservations in future");
            }

           //brisi smjestaj
            accommodationClient.deleteAccommodationsOfHost(user.getId());
            log.info("Accommodations of host id {} deleted", userId);
        }

        //disable user
        gatewayClient.disableUser(user.getId());
        log.info("Disable user with id {} in gateway service", userId);

        user.setDeleted(true);
        log.info("User with id {} marked as deleted", userId);
        return userRepository.save(user);

    }

}
