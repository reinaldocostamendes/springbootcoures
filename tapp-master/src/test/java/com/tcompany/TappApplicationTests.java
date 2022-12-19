package com.tcompany;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.tcompany.model.Users;
import com.tcompany.repository.UserRepositoryTests;

@SpringBootTest
class TappApplicationTests {
	@Autowired
private UserRepositoryTests userRepositoryTests;
	@Test
	void contextLoads() {
	}
@Test
void testInsertUser() {
	Users user = new Users();
	user.setEnabled(true);
	user.setFirs_name("UserName");
	user.setLast_name("LastName");
	user.setEmail("user1@mail.com");
	user.setPassword("123");
	userRepositoryTests.save(user);
	List<Users> list = (List<Users>) userRepositoryTests.findAll();
	List<String> firs_names = list.stream().map(Users::getFirs_name).collect(Collectors.toList());
	assertTrue(firs_names.contains("UserName"));
}

@Test
void testDeleteUsers() {
	Users user = new Users();
	user.setEnabled(true);
	user.setFirs_name("UserName");
	user.setLast_name("LastName");
	user.setEmail("user1@mail.com");
	user.setPassword("123");
	userRepositoryTests.save(user);
	List<Users> users_to_del =  userRepositoryTests.findUserByFirsName("UserName");
	userRepositoryTests.deleteAll(users_to_del);
	List<Users> list = (List<Users>) userRepositoryTests.findAll();
	List<String> firs_names = list.stream().map(Users::getFirs_name).collect(Collectors.toList());
	assertTrue(!firs_names.contains("UserName"));
	
}

@Test
void testRetrieveUser() {
	Users user = new Users();
	user.setEnabled(true);
	user.setFirs_name("UserName1");
	user.setLast_name("LastName1");
	user.setEmail("user1@mail.com");
	user.setPassword("123");
	userRepositoryTests.save(user);
	List<Users> list = (List<Users>) userRepositoryTests.findAll();
	List<String> firs_names = list.stream().map(Users::getFirs_name).collect(Collectors.toList());
	assertTrue(firs_names.contains("UserName1"));
	
}

@Test
void testUpdateUser() {
	Users user =userRepositoryTests.findById(1).get();
	user.setFirs_name("Firs User Updated");
	userRepositoryTests.save(user);
	Users u_updated =userRepositoryTests.findById(1).get();
	assertEquals("Firs User Updated", u_updated.getFirs_name());
	
}
}

