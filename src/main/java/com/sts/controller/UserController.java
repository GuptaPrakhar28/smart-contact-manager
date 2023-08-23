package com.sts.controller;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.sts.dao.ContactRepository;
import com.sts.dao.UserRepository;
import com.sts.entities.Contact;
import com.sts.entities.User;
import com.sts.helper.Message;

@Controller
@RequestMapping("/user")
   public class UserController {
	@Autowired
   private BCryptPasswordEncoder BcryptpasswordEncoder;
	@Autowired
    private UserRepository userRepository;
	@Autowired
	private ContactRepository contactRepository;
	//method for adding common data to response
	@ModelAttribute
	public void addCommonData(Model model,Principal principal) {
		String userName=principal.getName();
		System.out.println("USERNAME"+userName);
		//get the user using username(Email)
	User user = this.userRepository.getUserByUserName(userName);
	  System.out.println("USER"+ user);
	  model.addAttribute("user",user);
	  model.addAttribute("title" ," ");
	}
	
	// dashboard home
	@RequestMapping("/index")
	public String dashboard(Model model,Principal principal)
	{
	
		return "normal/user_dashboard";
	}
	//open add form handler
	@GetMapping("/add-contact")
	public String openAddContactForm(Model model) {
		model.addAttribute("title","Add Contact");
		model.addAttribute("contact", new Contact());
		return "normal/add_contact_form";
	}
	// processing add contact form
	@PostMapping("/process-contact")
	public String processContact(@ModelAttribute Contact contact,Principal principal,HttpSession session) {
	try
		{
			String name= principal.getName();
	   User user = userRepository.getUserByUserName(name);
	   contact.setUser(user);
		user.getContacts().add(contact);
		this.userRepository.save(user);
		System.out.println("Added to DB");
		System.out.println("DATA"+contact);
		session.setAttribute("message", new Message("Your Contact is added","success"));
	}
	catch (Exception e) {
		System.out.println("error"+e.getMessage());
		e.printStackTrace();
		session.setAttribute("message", new Message("Something went wrong ,try again","danger"));
	}
	
		return "normal/add_contact_form";
	}
	//show contacts handler
	//per page =5[n]
	//current page =0[page]
	@GetMapping("/show-contacts/{page}")
	public String showContacts(@PathVariable("page") Integer page,Model m,Principal principal) {
		m.addAttribute("title","show user contacts");
		//contact ki list bhejni h
		String userName=principal.getName();
	
		User user=this.userRepository.getUserByUserName(userName);
		Pageable pageable=PageRequest.of(page,3);
		
		Page <Contact> contacts=this.contactRepository.findContactsByUser(user.getId(),pageable);
		m.addAttribute("contacts", contacts);
		m.addAttribute("currentPage",page);
		m.addAttribute("totalPages",contacts.getTotalPages());
		return "normal/show_contacts";
	}
	@RequestMapping("contact/{cid}")
	public String showContactDetail(@PathVariable("cid") Integer cid,Model model,Principal principal)
	{
		String userName=principal.getName();
		
		User user=this.userRepository.getUserByUserName(userName);
		  Optional<Contact>contactopt=this.contactRepository.findById(cid);
		  
		  Contact contact=contactopt.get();
		  if(user.getId()==contact.getUser().getId())
		  model.addAttribute("contact", contact);
		return "normal/contact_detail";
	}
	//delete contact
	@GetMapping("/delete/{cid}")
	public String deleteContact(@PathVariable("cid") Integer cid,Model model,HttpSession session,Principal principal) {
		 Optional<Contact>contactopt=this.contactRepository.findById(cid);
		  
		  Contact contact=contactopt.get();
		 
		  String userName=principal.getName();
			
			User user=this.userRepository.getUserByUserName(userName);
			user.getContacts().remove(contact);
		   
			 this.userRepository.save(user);
			  session.setAttribute("message",new Message("contact deleted succcesfully!","success"));
		  
		return "redirect:/user/show-contacts/0";
	}
	//open update form handler
	@PostMapping("/update-contact/{cid}")
	public String updateForm(@PathVariable("cid") Integer cid,Model m) {
		m.addAttribute("title","update contact");
		Contact contact=this.contactRepository.findById(cid).get();
		 m.addAttribute("contact", contact);
		return "normal/update_form";
	}
	
	//process-update 
	@PostMapping("/process-update")
	public String processupdate(@ModelAttribute Contact contact,Principal principal)
	{
		String userName=principal.getName();
		
	User user=this.userRepository.getUserByUserName(userName);
	contact.setUser(user);
	this.contactRepository.save(contact);
	
	return "redirect:/user/contact/"+contact.getCid();
}
	//profile
	@GetMapping("/profile")
		public String profile() {
			return "normal/profile";
		}
	@GetMapping("/settings")
	public String openSettings()
	{
		return "normal/settings";
	}
	@PostMapping("/change-password")
	public String changepassword(@RequestParam("oldpassword") String oldpassword,@RequestParam("newpassword")String newpassword,Principal principal,HttpSession session)
	{
		System.out.println("OLD"+oldpassword);
		System.out.println("New"+newpassword);
		  String userName=principal.getName();
			
		User user=this.userRepository.getUserByUserName(userName);
		String curr=user.getPassword();
		if(this.BcryptpasswordEncoder.matches(oldpassword, curr)) {
			user.setPassword(this.BcryptpasswordEncoder.encode(newpassword));
		
			session.setAttribute("message",new Message("password changed succcesfully!","success"));
			this.userRepository.save(user);
		}
		
		else {
			session.setAttribute("message",new Message("pl enter old pass succcesfully!","danger"));
		}
		return "redirect:/user/index";
	}
}	
	
