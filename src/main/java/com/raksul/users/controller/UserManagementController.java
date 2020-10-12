package com.raksul.users.controller;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.raksul.users.exceptions.ResourceNotFoundException;
import com.raksul.users.model.RkUser;
import com.raksul.users.repository.RkUserRepository;
import com.raksul.users.services.UserManagementService;

@RestController
public class UserManagementController {
	@Autowired
	private RkUserRepository userRepository;
	@Autowired
	private UserManagementService userService;
	private static final Logger LOG = Logger.getLogger(UserManagementController.class.getName());

	@RequestMapping(value = { "/users" }, method = { RequestMethod.POST }, headers = {
			"Accept=application/json,application/xml,text/plain" })
	public Map createUser(@RequestBody RkUser user) throws ResourceNotFoundException {
		LOG.log(Level.INFO, "Creating user: " + user);

		Map createdUser = userService.createUser(user);

		LOG.log(Level.INFO, "User created successfully: " + user.getId());
		return createdUser;
	}

	// Login service
	@RequestMapping(value = { "/login" }, method = { RequestMethod.POST }, headers = {
			"Accept=application/json,application/xml,text/plain" })
	public Map<String, String> login(@RequestBody RkUser user) throws ResourceNotFoundException {
		LOG.log(Level.INFO, "Try to login with user: " + user.getEmail());
		Map loginToken = userService.login(user.getEmail(), user.getPassword());
		LOG.log(Level.INFO, "Login successfull for user: " + user.getEmail());
		return loginToken;

	}

//Update the user

	@RequestMapping(value = { "/users/{userId}" }, method = { RequestMethod.PATCH })
	public Map<String, String> updateUser(@PathVariable(value = "userId") long userId,
			@Valid @RequestBody RkUser inputUser, @RequestHeader("Authorization") String authHeader) throws ResourceNotFoundException {
		Map<String, String> updatedUser = userService.updateUser(userId, inputUser, authHeader.substring(7));
		return updatedUser;
	}

	// Get user if the secret matches
	@RequestMapping(value = { "/secret" }, method = { RequestMethod.GET })
	public ResponseEntity<Map> getSecret(@RequestHeader("Authorization") String authHeader)
			throws ResourceNotFoundException {

		long uid = userService.getUser(authHeader.substring(7));
		HashMap<String, String> map = new HashMap<>();
		map.put("user_id", "" + uid);
		map.put("secret", "All your base are belong to us");

		return ResponseEntity.ok(map);
	}

}
