package com.sts.controller;

import java.util.Random;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ForgotController 
{
@RequestMapping("/forgot")
public String openEmailForm() 
{
	
	return"forgot_email_form";
}

@PostMapping("/send-otp")
public String sendOtp(@RequestParam("email") String email)
{
	
	//Generationg OTP 4 ditit
	Random random=new Random(1000);
	int otp=random.nextInt(9999);
	System.out.println("otp" +otp);
	return"verify_otp";
}

}
