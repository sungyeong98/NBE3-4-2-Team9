package com.backend.global.security;

import com.backend.global.redis.repository.RedisRepository;
import com.backend.global.response.GenericResponse;
import com.backend.global.security.filter.JwtAuthenticationFilter;
import com.backend.global.security.filter.JwtAuthorizationFilter;
import com.backend.global.security.handler.JwtLogoutHandler;
import com.backend.global.security.handler.JwtLogoutSuccessHandler;
import com.backend.global.security.handler.OAuth2LoginFailureHandler;
import com.backend.global.security.handler.OAuth2LoginSuccessHandler;
import com.backend.global.security.oauth.CustomOAuth2UserService;
import com.backend.standard.util.AuthResponseUtil;
import com.backend.standard.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final JwtUtil jwtUtil;
	private final ObjectMapper objectMapper;
	private final RedisRepository redisRepository;
	private final CustomOAuth2UserService customOAuth2UserService;
	private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
	private final OAuth2LoginFailureHandler oAuth2LoginFailureHandler;

	@Value("${jwt.token.access-expiration}")
	private long ACCESS_EXPIRATION;

	@Value("${jwt.token.refresh-expiration}")
	private long REFRESH_EXPIRATION;

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public AuthenticationManager authenticationManager(
		AuthenticationConfiguration authenticationConfiguration) throws Exception {
		return authenticationConfiguration.getAuthenticationManager();
	}

	private static final Map<HttpMethod, List<String>> PUBLIC_URLS = new HashMap<>();

	static {
		PUBLIC_URLS.put(HttpMethod.GET, Arrays.asList(
			"/api/v1/job-posting",
			"/h2-console/**",
			"/login/oauth2/code/kakao",
			"/oauth2/authorization/kakao",
			"/api/v1/chat/**",
			"/api/v1/recruitment",
			"/api/v1/posts",// 게시글 전체 조회에는 로그인 하지 않은 유저도 이용 가능해야 함
			"/api/v1/category",
			"/ws/**",
			"/api/v1/adm/login"
		));
    
		PUBLIC_URLS.put(HttpMethod.POST, List.of("/api/v1/adm/login"));
    
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http,
		AuthenticationConfiguration configuration) throws Exception {

		JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(
			jwtUtil, ACCESS_EXPIRATION, REFRESH_EXPIRATION, objectMapper, redisRepository,
			authenticationManager(configuration)
		);

		jwtAuthenticationFilter.setFilterProcessesUrl("/api/v1/adm/login");

		JwtAuthorizationFilter jwtAuthorizationFilter = new JwtAuthorizationFilter(
			jwtUtil, ACCESS_EXPIRATION, REFRESH_EXPIRATION, objectMapper, redisRepository
		);

		http.headers(head -> head
				.frameOptions(option -> option.sameOrigin()))
			.csrf(csrf -> csrf.disable())
			.sessionManagement(
				config -> config.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.authorizeHttpRequests(authorizeRequests -> {
				// PUBLIC_URLS에서 설정된 URL들을 permitAll로 설정
				PUBLIC_URLS.forEach((method, patterns) ->
					patterns.forEach(pattern ->
						authorizeRequests.requestMatchers(method, pattern).permitAll()
					)
				);

				// 나머지 특정 권한이 필요한 URL들
				authorizeRequests
					//전체 허용
					.requestMatchers("/h2-console/**", "/ws/**").permitAll()
					.requestMatchers(HttpMethod.POST, "/api/v1/adm/login").permitAll()

					//어드민 권한만 사용 가능
					.requestMatchers(HttpMethod.POST, "/api/v1/category").hasRole("ADMIN")
					.requestMatchers(HttpMethod.PUT, "/api/v1/category/**").hasRole("ADMIN")
					.requestMatchers(HttpMethod.PATCH, "/api/v1/category/**").hasRole("ADMIN")
					.requestMatchers(HttpMethod.DELETE, "/api/v1/category/**").hasRole("ADMIN")

					//유저, 어드민 사용 가능
					.requestMatchers(HttpMethod.GET,
						"/api/v1/job-posting/{id}", "/api/v1/job-posting/voter",
						"/api/v1/free/posts/{postId}", "/api/v1/recruitment/posts/{postId}",
						"/api/v1/posts/{postId}/comments")
					.hasAnyRole("USER", "ADMIN")

					.requestMatchers(HttpMethod.DELETE,
						"/api/v1/recruitment/**", "/api/v1/voter/*",
						"/api/v1/free/posts/{postId}", "/api/v1/recruitment/posts/{postId}",
						"/api/v1/posts/{postId}/comments/{id}")
					.hasAnyRole("USER", "ADMIN")

					.requestMatchers(HttpMethod.PATCH,
						"/api/v1/recruitment/**", "/api/v1/free/posts/{postId}",
						"/api/v1/recruitment/posts/{postId}", "/api/v1/posts/{postId}/comments/{id}")
					.hasAnyRole("USER", "ADMIN")

					.requestMatchers(HttpMethod.POST,
						"/api/v1/voter", "/api/v1/recruitment/**",
						"/api/v1/free/posts", "/api/v1/recruitment/posts", "/api/v1/posts/{postId}/comments")
					.hasAnyRole("USER", "ADMIN")
					.anyRequest().authenticated();
			})
			.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
			.addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class)
			.exceptionHandling(exception -> exception
				.authenticationEntryPoint((request, response, authException) ->
				{
					AuthResponseUtil.failLogin(
						response, GenericResponse.fail(HttpStatus.UNAUTHORIZED.value()),
						HttpServletResponse.SC_UNAUTHORIZED, objectMapper);
				}))
			.exceptionHandling(exception -> exception
				.accessDeniedHandler((request, response, authException) ->
				{
					AuthResponseUtil.failLogin(
						response, GenericResponse.fail(HttpStatus.FORBIDDEN.value()), HttpServletResponse.SC_FORBIDDEN,
						objectMapper);
				}))
			.logout(logout -> logout
				.logoutUrl("/api/v1/logout")
				.addLogoutHandler(new JwtLogoutHandler(jwtUtil, redisRepository))
				.logoutSuccessHandler(new JwtLogoutSuccessHandler(objectMapper))
			)
			.oauth2Login(oauth2 -> oauth2
					.userInfoEndpoint(userInfo -> userInfo
						.userService(customOAuth2UserService)
					)
					.successHandler(oAuth2LoginSuccessHandler)
					.failureHandler(oAuth2LoginFailureHandler)
				//                        .defaultSuccessUrl("/")
				//                        .failureUrl("/login/oauth2/code/kakao")
			);

		return http.build();
	}

	@Bean
	public UrlBasedCorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();

		// 허용할 HTTP 메서드 설정
		configuration.setAllowedMethods(
			Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
		// CORS 설정
		configuration.setAllowedOrigins(
			List.of(
				"http://localhost:3000",
				"http://localhost:8080"
			)
		);
		// 자격 증명 허용 설정
		configuration.setAllowCredentials(true);
		// 허용할 헤더 설정
		configuration.setAllowedHeaders(List.of("*"));

		configuration.setExposedHeaders(Arrays.asList("Authorization", "Set-Cookie"));

		// CORS 설정을 소스에 등록
		org.springframework.web.cors.UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);

		return source;
	}

	public static Map<HttpMethod, List<String>> getPublicUrls() {
		return PUBLIC_URLS;
	}

}