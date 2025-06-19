package com.theanh1301.myapp.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.oauth2.resource.servlet.JwkSetUriJwtDecoderBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

import javax.crypto.spec.SecretKeySpec;


@Configuration
@EnableWebSecurity // no dc enable san roi -> khong bat cung duoc
@EnableMethodSecurity // phân quyền theo method -> bằng cách đặt annotation cho method đó @PreAuthorize
public class SecurityConfig {

    private final String[] PUBLIC_ENDPOINTS = {"/api/users", "/api/auth/login","/api/auth/logout",  "/api/auth/introspect"
    ,"/api/auth/refresh"};

    @Value("${jwt.signerKey}")
    private String jwtSignerKey;



    @Autowired
    private CustomJwtDecoder customJwtDecoder;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.
                authorizeHttpRequests(request -> request.requestMatchers(HttpMethod.POST, PUBLIC_ENDPOINTS).permitAll()

//                        .requestMatchers(HttpMethod.GET, "/api/users").hasAuthority("ROLE_ADMIN") //oauth2 sẽ đọc SCOPE_
                        .anyRequest().authenticated());
        //cung cap token hop le vao header(cai nay xu ly ktra)
        httpSecurity.oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwtConfigurer -> jwtConfigurer.decoder(customJwtDecoder)   //decoder de giai ma token (cho logout mk da custom)
                                .jwtAuthenticationConverter(jwtAuthenticationConverter())//set cái custom vào đây


                        ).authenticationEntryPoint(new JwtAuthenticationEntryPoint()) ); // điều hướng hoặc in ra lỗi nếu đăng nhập fail


        httpSecurity.csrf(AbstractHttpConfigurer::disable);
        return httpSecurity.build();
    }


    //Custom các thứ của jwt oauth2
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter  jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        jwtGrantedAuthoritiesConverter.setAuthorityPrefix(""); // lúc trước mình  để như này setAuthorityPrefix("ROLE_") để nó tự gán nhưng
        //giờ đã gán bên AuthenticationService để phân biệt với Permission .
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);
        return jwtAuthenticationConverter;
    }


    //Xử lý logout
    @Bean
    public JwtDecoder jwtDecoder(){
        SecretKeySpec secretKey = new SecretKeySpec(jwtSignerKey.getBytes(), "HS512");
        return  NimbusJwtDecoder.withSecretKey(secretKey)
                .macAlgorithm(MacAlgorithm.HS512).build();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder(10);
    }

}


