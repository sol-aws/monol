package com.example.ordersystem.common.config;

import com.example.ordersystem.common.auth.JwtAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {
    private final JwtAuthFilter authFilter;

    public SecurityConfig(JwtAuthFilter authFilter) {
        this.authFilter = authFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(a -> a
                        // 모놀리식 2차 배포: Spring Boot가 HTML/CSS/JS를 직접 서비스한다.
                        // 이 경로가 빠지면 메인 페이지(/)가 403이 되므로 반드시 permitAll 처리한다.
                        .requestMatchers(
                                "/", "/index.html",
                                "/login.html", "/signup.html", "/product-register.html",
                                "/style.css", "/common.js", "/login.js", "/signup.js",
                                "/shop.js", "/product-register.js",
                                "/favicon.ico", "/error"
                        ).permitAll()
                        // 회원가입 / 로그인 / Token 재발급 / Readiness Probe
                        .requestMatchers("/member/create", "/member/doLogin", "/member/refresh-token", "/health").permitAll()
                        // 상품 목록은 로그인하지 않아도 조회 가능
                        .requestMatchers(HttpMethod.GET, "/product/list").permitAll()
                        // 상품등록 / 주문 등 나머지 API는 JWT 인증 필요
                        .anyRequest().authenticated())
                .build();
    }

    @Bean
    public PasswordEncoder makePassword(){
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
