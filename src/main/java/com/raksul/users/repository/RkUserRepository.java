package com.raksul.users.repository;


import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;

import com.raksul.users.model.RkUser;
@Transactional
public interface RkUserRepository extends PagingAndSortingRepository<RkUser, Long> {
	
	
	  //public User findByDeparture(String departure);
	  public RkUser findById(long uid);
	  public RkUser findByEmail(String email);
	  public RkUser findBySecret(String email);
	 


}

