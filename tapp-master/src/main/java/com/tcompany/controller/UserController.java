package com.tcompany.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.tcompany.model.Users;
import com.tcompany.service.UserService;

@Controller
public class UserController {
	@Autowired
	private UserService userService;
	@RequestMapping(method = RequestMethod.GET, value = { "/users" })
	public ModelAndView index() {
		ModelAndView andView = new ModelAndView("/users/users");

		andView.addObject("users", userService.getAll());
		andView.addObject("userobj", new Users());
		
		return andView;
	}

	@RequestMapping(method = RequestMethod.POST, value = { "/save", "*/save" })
	public ModelAndView save(@ModelAttribute Users user)
			 throws IOException {
		
		
		ModelAndView andView =  new ModelAndView("/users/users");
		if(user.getUser_id()>= 0 &&( user.getPassword()==null || user.getPassword().trim()=="")) {
			Users user_tmp = userService.getUserById(user.getUser_id());
			user.setPassword(user_tmp.getPassword());
		}else{
			user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
		}
			Users user_saved = userService.addOrUpdateUSer(user);
			andView.addObject("users", userService.getAll());
			andView.addObject("userobj", user_saved);
		andView.addObject("msg","Saved successful");
	
		return andView;
	}
	@RequestMapping(method = RequestMethod.GET, value = { "/users/new" })
	public ModelAndView create() {
		ModelAndView andView = new ModelAndView("/users/user-form");

		andView.addObject("userobj", new Users());
		andView.addObject("title", "Add New User");
		return andView;
	}
	@RequestMapping(method = RequestMethod.GET, value = "/listusers")
	public ModelAndView users() {
		ModelAndView andView = new ModelAndView("/users/users");

		andView.addObject("users", userService.getAll());
		andView.addObject("userobj", new Users());
		
		return andView;
	}

	@GetMapping("/edituser/{id}")
	public ModelAndView edit(@PathVariable("id") Integer id) {
		ModelAndView andView = new ModelAndView("/users/user-form");
		Users user = userService.getUserById(id);
		andView.addObject("userobj", user);
		andView.addObject("title", "Edit User");
		return andView;
	}

	@RequestMapping(value = { "/delete/{id}" }, method = RequestMethod.GET)
	public ModelAndView delete(@PathVariable("id") Integer id) throws Exception {
	Users user =	userService.deleteUser(id);
	String msg = "";
	if(user==null){
		msg="Not found user to delete";
	}else{
		msg="User "+user.getFirs_name()+" deleted successful";
	}
		ModelAndView andView = new ModelAndView("/users/users");

		andView.addObject("users", userService.getAll());
		andView.addObject("userobj", new Users());
		andView.addObject("msg",msg);
		return andView;
	}

}
