package com.tcompany.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.tcompany.model.Users;
import com.tcompany.service.UserService;

@Controller
public class MainController {
	@Autowired
	private UserService userService;
	@RequestMapping(value = "/")
	public  ModelAndView index() {
		ModelAndView andView = new ModelAndView("index");

		andView.addObject("users", userService.getAll());
		andView.addObject("userobj", new Users());
		
		return andView;
	}

}
