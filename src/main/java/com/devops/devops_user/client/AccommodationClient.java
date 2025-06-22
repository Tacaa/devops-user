package com.devops.devops_user.client;

import com.devops.devops_user.dto.UpdateUserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(value = "accommodation", url = "${accommodation.service.url}")
public interface AccommodationClient {
    @RequestMapping(method = RequestMethod.GET, value = "/api/reservation/is-guest-having-reservation-at-moment")
    Boolean checkIsGuestHavingReservationAtMoment(@RequestParam Integer guestId);

    @RequestMapping(method = RequestMethod.GET, value = "/api/reservation/is-host-having-reservation-at-moment")
    Boolean checkIsHostHavingReservationAtMoment(@RequestParam Integer hostId);

    @RequestMapping(method = RequestMethod.DELETE, value = "/api/accommodation/delete-accommodations-of-host")
    Boolean deleteAccommodationsOfHost(@RequestParam Integer hostId);

}
