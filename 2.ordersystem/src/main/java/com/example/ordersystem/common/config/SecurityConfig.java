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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableMethodSecurity //PreAuthorize사용시 해당 어노테이션 선언필요.
public class SecurityConfig {
    private final JwtAuthFilter authFilter;
    public SecurityConfig(JwtAuthFilter authFilter) {
        this.authFilter = authFilter;
    }
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
         return httpSecurity
//                 spring security에서 cors 정책 지정
                 .cors(c -> c.configurationSource(corsConfiguration()))
                 .csrf(AbstractHttpConfigurer::disable) //csrf(보안공격중하나) 비활성화
                 .httpBasic(AbstractHttpConfigurer::disable) // http basic 보안방식 비활성화
//                 세션로그인 방식 사용하지 않는다는것을 의미
                 .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//                 token을 검증하고, token을 통해 Authentication객체생성
                 .addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class)
//                 회원가입/로그인/상품목록은 로그인 없이 접근 가능
//                 상품등록과 주문은 JWT 인증이 필요
                 .authorizeHttpRequests(a -> a
                         .requestMatchers("/member/create", "/member/doLogin", "/member/refresh-token", "/health").permitAll()
                         .requestMatchers(HttpMethod.GET, "/product/list").permitAll()
                         .anyRequest().authenticated())
                 .build();
    }

    private CorsConfigurationSource corsConfiguration(){
        CorsConfiguration configuration = new CorsConfiguration();
        // 실제 배포에서는 Frontend와 Backend를 같은 Ingress 도메인으로 서비스한다.
        // localhost는 개발 중 직접 Frontend를 실행할 때 사용한다.
        configuration.setAllowedOriginPatterns(Arrays.asList("http://localhost:*", "https://server.aws-esk.com", "http://server.aws-esk.com"));
        configuration.setAllowedMethods(Arrays.asList("*")); // 모든 HTTP(get, post 등) 메서드 허용
        configuration.setAllowedHeaders(Arrays.asList("*")); // 모든 헤더 허용
        configuration.setAllowCredentials(true); // 자격 증명 허용
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); //모든 url패턴에 대해 cors설정 적용
        return source;
    }

    @Bean
    public PasswordEncoder makePassword(){
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
