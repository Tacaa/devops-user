package com.devops.devops_user.dto;

import com.devops.devops_user.model.Address;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserDTO {
    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private String email;
    private AddressDTO address;


}
