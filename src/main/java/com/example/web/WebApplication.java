package com.example.web;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.List;

@SpringBootApplication
public class WebApplication {

	public static void main(String[] args) {
		SpringApplication.run(WebApplication.class, args);
	}



	//https://jsonplaceholder.typicode.com/users
	record User(int id, String name, String username, String email, Address address) {
	}

	record Address(String street, String suite, String city, String zipcode, Geo geo) {
	}

	record Geo(float lat, float lng) {
	}
	@Configuration
	class WebConfiguration{
		@Bean
		RestClient restClient(RestClient.Builder builder) {
			return builder.baseUrl("https://jsonplaceholder.typicode.com/").build();
		}
		@Bean
		ApplicationRunner runner(SimpleUsersClient client){
			return args -> client.users().forEach(System.out::println);
		}
	}
	@Component
	class SimpleUsersClient{
       private final RestClient http;
	   SimpleUsersClient(RestClient http) {
		   this.http = http;
	   }
	   private final ParameterizedTypeReference<List<User>> typeRef = new ParameterizedTypeReference<>() {};
	   Collection<User>users(){
		   return this.http
				   .get()
				   .uri("/users")
				   .retrieve()
				   .body(typeRef);
	   }
	}
}
