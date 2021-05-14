package com.polanco.updown.filter;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

public class UserSesion {
	
	public static String getCurrentUsername() {
		
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		if(principal instanceof UserDetails) {
			
			return ((UserDetails) principal).getUsername();
		}
		else {
			
			return principal.toString();
		}
	}
}
