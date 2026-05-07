package com.movie.api;

import com.movie.api.dto.UserRequest;
import com.movie.api.repository.*;
import com.movie.api.service.AccountService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class MovieApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(MovieApiApplication.class, args);
	}

	@Bean
	CommandLineRunner seedData(AccountService accountService, 
                                AccountRepository accountRepository, 
                                FavoriteRepository favoriteRepository,
                                RatingRepository ratingRepository,
                                UserRepository userRepository) {
		return args -> {
            System.out.println("Checking database seed...");
			if (accountRepository.count() == 0) {
                System.out.println("Seeding new accounts...");
                favoriteRepository.deleteAll();
                ratingRepository.deleteAll();
                userRepository.deleteAll();
                accountRepository.deleteAll();

                UserRequest u1 = new UserRequest();
                u1.setName("Blina");
                u1.setEmail("blina@example.com");
                u1.setPassword("123456");
                u1.setAvatarUrl("https://upload.wikimedia.org/wikipedia/commons/0/0b/Netflix-avatar.png");
                accountService.register(u1);

                UserRequest u2 = new UserRequest();
                u2.setName("Ardi");
                u2.setEmail("ardi@example.com");
                u2.setPassword("123456");
                u2.setAvatarUrl("https://loodibee.com/wp-content/uploads/Netflix-Avatar-2.png");
                accountService.register(u2);
			}
		};
	}
}