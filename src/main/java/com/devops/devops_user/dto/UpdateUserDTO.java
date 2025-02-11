package com.devops.devops_user.dto;
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
