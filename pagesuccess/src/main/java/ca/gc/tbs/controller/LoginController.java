package ca.gc.tbs.controller;

import ca.gc.tbs.domain.User;
import ca.gc.tbs.service.CustomUserDetailsService;

import java.text.SimpleDateFormat;

import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class LoginController {
	public static final String DATE_FORMAT = "yyyy-MM-dd";
	public static final SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT);

	@Autowired
	private CustomUserDetailsService userService;

	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public ModelAndView login() {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("login");
		return modelAndView;
	}

	@RequestMapping(value = "/signup", method = RequestMethod.GET)
	public ModelAndView signup() {
		ModelAndView modelAndView = new ModelAndView();
		User user = new User();
		modelAndView.addObject("user", user);
		modelAndView.setViewName("signup");
		return modelAndView;
	}

	@GetMapping("/checkExists")
	public @ResponseBody String checkExists(@RequestParam String email) {
		// verify that one has not already been created.
		User userExists = userService.findUserByEmail(email);
		if (userExists != null) {
			return "true";
		} else {
			return "false";
		}
	}

	@RequestMapping(value = "/signup", method = RequestMethod.POST)
	public ModelAndView createNewUser(@Valid User user) {
		ModelAndView modelAndView = new ModelAndView();
		userService.saveUser(user);
		modelAndView.addObject("successMessage", "User has been registered successfully");
		modelAndView.addObject("user", new User());
		modelAndView.setViewName("login");
		return modelAndView;
	}

	@RequestMapping(value = { "/", "/home" }, method = RequestMethod.GET)
	public ModelAndView home() {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("home");
		return modelAndView;
	}

}