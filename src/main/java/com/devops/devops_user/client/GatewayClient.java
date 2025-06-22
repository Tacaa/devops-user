package com.devops.devops_user.client;

import com.devops.devops_user.dto.UpdateUserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(value = "gateway", url = "${gateway.service.url}")
public interface GatewayClient {
    @RequestMapping(method = RequestMethod.PUT, value = "/api/gateway/update-user/{id}")
    Boolean updateUser(@PathVariable("id") Integer id, @RequestBody UpdateUserDTO updateUserDTO);

    @RequestMapping(method = RequestMethod.PUT, value = "/api/gateway/disable-user/{id}")
    Boolean disableUser(@PathVariable("id") Integer id);

}




