package com.devops.devops_user.dto;

import com.devops.devops_user.enumeration.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserDTO {
    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private String email;
    private Role role;
    private CreateAddressDTO address;
}
