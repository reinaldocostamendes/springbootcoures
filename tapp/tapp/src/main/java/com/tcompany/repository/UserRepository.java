package com.tcompany.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tcompany.model.Users;
@Repository
public interface UserRepository extends CrudRepository<Users, Integer> {

}