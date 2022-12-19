package com.tcompany.service;

import java.util.List;

import com.tcompany.model.Users;

public interface UserService {
public List<Users> getAll();
public Users getUserById(int userId);
public Users addOrUpdateUSer(Users user);
public Users deleteUser(int id) throws Exception;
}