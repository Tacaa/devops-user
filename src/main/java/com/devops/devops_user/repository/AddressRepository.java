package com.devops.devops_user.repository;

import com.devops.devops_user.model.Address;
import io.micrometer.observation.annotation.Observed;
import org.springframework.data.repository.CrudRepository;

@Observed
public interface AddressRepository extends CrudRepository<Address, Integer> {
}
