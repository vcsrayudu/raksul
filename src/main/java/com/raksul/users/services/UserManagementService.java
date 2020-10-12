package com.raksul.users.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.raksul.users.exceptions.ResourceNotFoundException;
import com.raksul.users.model.RkUser;

public interface UserManagementService {

public long getUser(String secret) throws ResourceNotFoundException ;
public Map<String,String> login(String email, String password)  throws ResourceNotFoundException ;
public Map<String,String> createUser(RkUser user) throws ResourceNotFoundException ;
public Map<String,String> updateUser(long id,RkUser user, String token) throws ResourceNotFoundException;



}
