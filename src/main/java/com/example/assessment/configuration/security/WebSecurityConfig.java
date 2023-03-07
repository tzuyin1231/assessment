//package com.example.assessment.configuration.security;
//
//import com.example.assessment.configuration.security.oauth.CustomOAuth2User;
//import com.example.assessment.configuration.security.oauth.CustomOAuth2UserService;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
//
//import java.io.IOException;
//
//@Configuration
//@EnableWebSecurity
//public class WebSecurityConfig {
//
//	@Autowired
//	private CustomOAuth2UserService oauth2UserService;
//
//
//
//	@Bean
//	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//		http.authorizeHttpRequests()
//				.requestMatchers("/", "/login").permitAll()
////				有任何要求，都要經過authenticated才能做事，否則會跳出去
//				.anyRequest().authenticated()
//				.and()
//				.formLogin().permitAll()
//				.loginPage("/login")
//				.usernameParameter("email")
//				.passwordParameter("pass")
//
//				.defaultSuccessUrl("/list")
//				.and()
////				可以串接第三方的login
//				.oauth2Login()
//				.loginPage("/login")
//				.userInfoEndpoint()
//				.userService(oauth2UserService)
//				.and()
//				.successHandler(new AuthenticationSuccessHandler() {
//
//					@Override
//					public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
//														Authentication authentication) throws IOException, ServletException {
//						CustomOAuth2User oauthUser = (CustomOAuth2User) authentication.getPrincipal();
//						response.sendRedirect("/list");
//					}
//				})
//				.and()
//				.logout().logoutSuccessUrl("/").permitAll()
//				.and()
//				.exceptionHandling().accessDeniedPage("/403");
//
//		return http.build();
//	}
//
//}
