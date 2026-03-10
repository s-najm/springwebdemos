package com.example.web;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import java.lang.annotation.*;
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
		HttpServiceProxyFactory build(@Secured RestClient http){
			return HttpServiceProxyFactory
					.builder()
					.exchangeAdapter(RestClientAdapter.create(http))
					.build();
		}
		@Bean
		DeclarativeUsersClient declarativeUsersClient(HttpServiceProxyFactory h){
			return h.createClient(DeclarativeUsersClient.class);
		}

		@Bean@Secured
		RestClient securedRestClient(RestClient.Builder builder){
			return builder.build();
		}
		@Bean
		RestClient restClient(RestClient.Builder builder) {
			return builder.build();
		}
		@Bean
		ApplicationRunner runner(DeclarativeUsersClient usersClient){
			return args ->{
				usersClient.users().forEach(System.out::println);
				System.out.println(usersClient.user(1));
			} ;
		}
	}
	interface DeclarativeUsersClient{
		static final String BASE_URL = "https://jsonplaceholder.typicode.com";
		@GetExchange(BASE_URL+"/users/{id}")
		User user(@PathVariable  int id);
		@GetExchange(BASE_URL+"/users")
		Collection<User> users();
	}
	@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.TYPE, ElementType.ANNOTATION_TYPE})
	@Retention(RetentionPolicy.RUNTIME)
	@Inherited
	@Documented
	@Qualifier( "secured")
	 @interface Secured {
		String value() default "";
	}
	/*
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

	 */
}
