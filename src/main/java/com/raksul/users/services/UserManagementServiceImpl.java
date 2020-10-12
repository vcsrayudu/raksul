package com.raksul.users.services;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;


import com.raksul.users.exceptions.ResourceNotFoundException;
import com.raksul.users.model.RkUser;

import com.raksul.users.repository.RkUserRepository;

@Service(value = "userService")
public class UserManagementServiceImpl implements UserManagementService {
	@Autowired
	private RkUserRepository userRepository;
	@Autowired
	private static final Logger LOG = Logger.getLogger(UserManagementServiceImpl.class.getName());
	private static final int CREATE_UPDATE_USER = 1;
	private static final int GENERATE_TOKEN = 2;


	
	@Override
	public long getUser(String secret) throws ResourceNotFoundException {
		// TODO Auto-generated method stub
		RkUser user = userRepository.findBySecret(secret);
		Optional.ofNullable(user).orElseThrow(() -> new ResourceNotFoundException("token invalid"));
		long expire = ZonedDateTime.now().toInstant().toEpochMilli();
		if ((expire - user.getTokenCreationTime()) > 3000) {
			Optional.ofNullable(null).orElseThrow(() -> new ResourceNotFoundException("token expired"));
		}
		return user.getId();
	}

	// Login validation
	@Override
	public Map<String, String> login(String email, String password) throws ResourceNotFoundException {

		String token = null;
		// check whether the user is there or not
		RkUser existUser = userRepository.findByEmail(email);
		// If Not throw the error

		Optional.ofNullable(existUser)
				.orElseThrow(() -> new ResourceNotFoundException("Invalid creadentials: " + email));
		LOG.log(Level.INFO, "existUser.getPassword() :"+existUser.getPassword());
		// If yes check the password validation
		String passwordEncrypted=getEncryptedCode(password);
		if (passwordEncrypted.equals(existUser.getPassword())) {
			String message = existUser.getEmail() + existUser.getId() + password
					+ ZonedDateTime.now().toInstant().toEpochMilli();
			//Generating token
			token=getEncryptedCode(message);
			
			existUser.setSecret(token);
			LOG.log(Level.INFO, "Setting updation time");

			existUser.setTokenCreationTime(ZonedDateTime.now().toInstant().toEpochMilli());
			existUser = userRepository.save(existUser);
		} else {
			Optional.ofNullable(null)
					.orElseThrow(() -> new ResourceNotFoundException("Invalid creadentials: " + email));
		}

		return returnMessage(existUser, GENERATE_TOKEN);
	}

//Creating new user
	@Override
	public Map<String, String> createUser(RkUser user) throws ResourceNotFoundException {
		isValidEmail(user.getEmail());
		isValidPassword(user.getPassword());
		LOG.log(Level.INFO, "Retriving user from datatbase: " + user.getEmail());
		RkUser existUser = userRepository.findByEmail(user.getEmail());
		if (existUser == null) {

			LOG.log(Level.INFO, "Setting Creation time");
			user.setCreationTime(getCurrentTime());
			user.setUpdationTime(null);
			LOG.log(Level.INFO, "Encoding password");
			
			String encodedPassword=getEncryptedCode(user.getPassword());
			LOG.log(Level.INFO, "Password: "+encodedPassword);
			user.setPassword(encodedPassword);
			LOG.log(Level.INFO, "Saving the user");
			existUser = userRepository.save(user);
			Optional.ofNullable(existUser).orElseThrow(
					() -> new ResourceNotFoundException("Unable to create user for user Email: " + user.getEmail()));
			LOG.log(Level.INFO, "User created successfully");
		} else {
			LOG.log(Level.INFO, "User already exist with email: " + user.getEmail());
			Optional.ofNullable(null).orElseThrow(
					() -> new ResourceNotFoundException("User already exist with User email: " + user.getEmail()));
		}
		return returnMessage(existUser, CREATE_UPDATE_USER);
	}

	@Override
	public Map<String, String> updateUser(long id, RkUser inputUser, String token) throws ResourceNotFoundException {
		LOG.log(Level.INFO, "Retrieve user with Id: " + id);
		RkUser user = userRepository.findById(id);
		boolean updated=false;
		//LOG.log(Level.INFO, "Token : "+user.getSecret());
		Optional.ofNullable(user).orElseThrow(() -> new ResourceNotFoundException("User not found for this id: " + id));
		if (token == null || !token.equals(user.getSecret())) {
			Optional.ofNullable(null)
					.orElseThrow(() -> new ResourceNotFoundException("Authentication failed for user: " + id));
		}
		long expire = ZonedDateTime.now().toInstant().toEpochMilli();
	//	LOG.log(Level.INFO, "expire: " + (expire - user.getTokenCreationTime()));
		if ((expire - user.getTokenCreationTime()) > 3000) {
			Optional.ofNullable(null).orElseThrow(() -> new ResourceNotFoundException("token expired"));
		}
		if (inputUser.getEmail() != null) {
			isValidEmail(inputUser.getEmail());
			LOG.log(Level.INFO, "Updating email address for user" + id);
			user.setEmail(inputUser.getEmail());
			updated=true;
		}
		if (inputUser.getPassword() != null) {
			isValidPassword(inputUser.getPassword());
			LOG.log(Level.INFO, "Encoding password");
			String encodedPassword=getEncryptedCode(inputUser.getPassword());
			LOG.log(Level.INFO, "Password: "+encodedPassword);
			user.setPassword(encodedPassword);
			LOG.log(Level.INFO, "Password updated for the user: " + id);
			user.setSecret(null);
			updated=true;
		}
		LOG.log(Level.INFO, "Setting updation time for user" + id);
		user.setUpdationTime(getCurrentTime());

		final RkUser updatedUser = userRepository.save(user);
		Optional.ofNullable(updatedUser).orElseThrow(
				() -> new ResourceNotFoundException("User update failed with internal error for user : " + id));
		LOG.log(Level.INFO, "Update user successfully");
		return returnMessage(updatedUser, CREATE_UPDATE_USER);
	}

	// Validating email format
	public boolean isValidEmail(String email) throws ResourceNotFoundException {

		String regex = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
		if (email == null || !email.matches(regex)) {
			Optional.ofNullable(null)
					.orElseThrow(() -> new ResourceNotFoundException("Invalid email format: " + email));
		}
		return true;
	}

	public boolean isValidPassword(String password) throws ResourceNotFoundException {
		

		if (password == null || password.length() < 8) {
			Optional.ofNullable(null).orElseThrow(
					() -> new ResourceNotFoundException("User password should not less than 8 char"));
		}
		return true;

	}

	public Map<String, String> returnMessage(RkUser user, int type) {
		HashMap<String, String> map = new HashMap<>();
		switch (type) {
		case CREATE_UPDATE_USER:
			map.put("id", "" + user.getId());
			map.put("email", user.getEmail());
			map.put("created_at", "" + user.getCreationTime());
			map.put("update_at", "" + user.getUpdationTime());
			break;
		case GENERATE_TOKEN:
			map.put("token", user.getSecret());
			break;
		}

		return map;
	}

	public String getCurrentTime() {

		DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssxxx");
		ZonedDateTime zonedDateTime = ZonedDateTime.now();
		LOG.log(Level.INFO, "Formated Time: " + zonedDateTime.format(format));
		return zonedDateTime.format(format);
	}
	
	public String getEncryptedCode(String message) throws ResourceNotFoundException
	{
		String token=null;
		try {
			// Generating token with MD5
			MessageDigest md = MessageDigest.getInstance("MD5");
			
			byte[] messageDigest = md.digest(message.getBytes());
			BigInteger no = new BigInteger(1, messageDigest);
			token = no.toString(16);
			while (token.length() < 32) {
				token = "0" + token;
			}

		}

		// For specifying wrong message digest algorithms
		catch (NoSuchAlgorithmException e) {

			Optional.ofNullable(null).orElseThrow(
					() -> new ResourceNotFoundException("Internal error while generating token: "));
		}
		return token;
	}

	

}
