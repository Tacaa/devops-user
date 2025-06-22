package com.devops.devops_user.client;

import com.devops.devops_user.dto.CreateNotificationsPreferencesDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(value = "notifications", url = "${notification.service.url}")
public interface NotificationClient {
    @RequestMapping(method = RequestMethod.POST, value = "/api/notifications-preferences")
    Boolean saveNotificationsPreferences(@RequestBody CreateNotificationsPreferencesDTO createNotificationsPreferencesDTO);

}
