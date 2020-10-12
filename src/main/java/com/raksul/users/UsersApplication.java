package com.raksul.users;



import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class UsersApplication {
	private static final Logger LOG = Logger.getLogger(UsersApplication.class.getName());

	public static void main(String[] args) {
		SpringApplication.run(UsersApplication.class, args);
		LOG.log(Level.INFO, "Application started");
		
		DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssxxx");
		ZonedDateTime z2 = ZonedDateTime.now();
		LOG.log(Level.INFO,"Time =======> " + z2.format(format));
	}

	

}


