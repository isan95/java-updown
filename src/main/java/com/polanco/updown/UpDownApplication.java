package com.polanco.updown;

import com.polanco.updown.entity.User;
import com.polanco.updown.repository.UserRepository;
import com.polanco.updown.service.StorageServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SpringBootApplication
public class UpDownApplication implements CommandLineRunner{
	
	@Resource
	StorageServiceImpl storageService;
	/*
    @Autowired
    private UserRepository repository;

    @PostConstruct
    public void initUsers() {
        List<User> users = Stream.of(
                new User("ismael", "password" , "ismael@gmail.com"),
                new User("user1",  "pwd1", "user1@gmail.com"),
                new User("user2", "pwd2", "user2@gmail.com"),
                new User("user3",  "pwd3", "user3@gmail.com")
        ).collect(Collectors.toList());
        
		users.forEach(i->{
			String pass = new BCryptPasswordEncoder().encode(i.getPassword());
			i.setPassword(pass);
		});
        repository.saveAll(users);
    }*/
    
  

    public static void main(String[] args) {
        SpringApplication.run(UpDownApplication.class, args);
    }
    
    @Override
    public void run(String...arg) throws Exception {
      storageService.deleteAll();
      storageService.init();
    }

}
