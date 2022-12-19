package com.tcompany.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tcompany.model.Users;
import com.tcompany.repository.UserRepository;
import com.tcompany.service.UserService;
@Service
public class UserServiceImpl implements UserService {
	@Autowired
private UserRepository userRepository;
	@Override
	public List<Users> getAll() {
		
		return (List<Users>) userRepository.findAll();
	}

	@Override
	public Users getUserById(int userId) {
		
		return userRepository.findById(userId).orElse(null);
	}

	@Override
	public Users addOrUpdateUSer(Users user) {
		
		return userRepository.save(user);
	}

	@Override
	public Users deleteUser(int id) throws Exception {
		Users deletedUser = null;
		try {
			deletedUser = userRepository.findById(id).orElse(null);
			if(deletedUser==null) {
				throw new Exception("User not found!");
			}else {
				userRepository.deleteById(id);
			}
		}catch(Exception e) {
			throw e;	
		}
		return deletedUser;

	}

}
