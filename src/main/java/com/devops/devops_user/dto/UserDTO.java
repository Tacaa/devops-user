package com.devops.devops_user.dto;

import com.devops.devops_user.enumeration.Role;
import com.devops.devops_user.model.Address;
import com.devops.devops_user.model.User;
import lombok.*;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {
    private Integer id;
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private Role role;
    private AddressDTO address;
    private Set<RateDTO> rates;

    public static UserDTO from(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .username(user.getUsername())
                .role(user.getRole())
                .email(user.getEmail())
                .address(user.getAddress() != null ? AddressDTO.from(user.getAddress()) : null)
                .rates(user.getRates() != null ? user.getRates().stream()
                        .map(RateDTO::from)
                        .collect(Collectors.toSet()) : new HashSet<>())
                .build();
    }

}
