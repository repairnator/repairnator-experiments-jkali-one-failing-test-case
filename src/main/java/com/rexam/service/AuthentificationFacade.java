package com.rexam.service;

import org.springframework.security.core.Authentication;

public interface AuthentificationFacade {
	Authentication getAuthentication();
}
