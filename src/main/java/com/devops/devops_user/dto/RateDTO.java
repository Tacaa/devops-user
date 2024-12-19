package com.devops.devops_user.dto;


import com.devops.devops_user.model.Rate;
import com.devops.devops_user.model.User;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RateDTO {
    private Integer id;
    private Integer rate;
    private LocalDateTime date;
    private GuestWhoRateDTO guestWhoRateDTO;

    public static RateDTO from(Rate rate) {
        return RateDTO.builder()
                .id(rate.getId())
                .rate(rate.getRate())
                .date(rate.getDate())
                .guestWhoRateDTO(GuestWhoRateDTO.from(rate.getGuest()))
                .build();
    }
}
