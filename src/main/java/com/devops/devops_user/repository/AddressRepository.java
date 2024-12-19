package com.devops.devops_user.repository;

import com.devops.devops_user.model.Address;
import org.springframework.data.repository.CrudRepository;

public interface AddressRepository extends CrudRepository<Address, Integer> {
}
