package com.devops.devops_user.dto;

import com.devops.devops_user.model.Address;
import com.devops.devops_user.model.User;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GuestWhoRateDTO {
    private Integer id;
    private String firstName;
    private String lastName;

    public static GuestWhoRateDTO from(User user) {
        return GuestWhoRateDTO.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .build();
    }
}
