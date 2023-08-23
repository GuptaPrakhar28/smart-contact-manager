package com.sts.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sts.dao.UserRepository;
import com.sts.entities.User;
import com.sts.helper.Message;
@Controller
public class HomeController {
	@Autowired
 private BCryptPasswordEncoder passwordEncoder;
	@Autowired
	private UserRepository userRepository;
	
	@GetMapping("/test")
	public String test() {
		User user=new User();
		user.setName("prakhar");
		user.setEmail("prakhar1@gmail.com");
		System.out.println("USER"+ user);
		userRepository.save(user);
		return "working";
	}
	
	@GetMapping("/")
	public String home(Model model) {
		model.addAttribute("title" ,"Home-Smart contact manager");
		return "home";
	}
	@GetMapping("/about")
	public String about(Model model) {
		model.addAttribute("title" ,"about-Smart contact manager");
		return "about";
	}
	@GetMapping("/signup")
	public String signup(Model model) {
		model.addAttribute("title" ,"sign-up-Smart contact manager");
		return "signup";
	}
	@GetMapping("/signin")
	public String Customlogin(Model model) {
		model.addAttribute("title" ,"login-Smart contact manager");
		return "login";
	}
	
	//handler for registering user
	@PostMapping("/do_register")
	public String registerUser(@ModelAttribute User user,@RequestParam(value="aggrement",defaultValue="false") boolean aggrement,Model model,HttpSession session)
	{
	try {	
		  if(!aggrement)
		  {
			System.out.println("you have not aggred");
			throw new Exception("you have not aggreed");
		   }
		 
		user.setRole("ROLE_USER");
		user.setEnabled(true);
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		System.out.println("USER"+user);
		userRepository.save(user);
		model.addAttribute("user", new User());
		session.setAttribute("message", new Message("successfully registered!!","alert-success"));
		
	}
	catch(Exception e) {
		e.printStackTrace();
		model.addAttribute("user",user);
		session.setAttribute("message", new Message("something went wrong!!"+e.getMessage(),"alert-danger"));
	}
	return "signup";
}
	//open setting handler
  
}	
