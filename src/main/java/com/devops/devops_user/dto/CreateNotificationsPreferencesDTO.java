package com.devops.devops_user.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateNotificationsPreferencesDTO {
    private Integer userId;
    private boolean isGuest;
}
