package com.theanh1301.myapp.config;


import com.theanh1301.myapp.entity.User;
import com.theanh1301.myapp.enums.Role;
import com.theanh1301.myapp.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;

@Configuration
@RequiredArgsConstructor // thay cho autowride(tạo contructor cho mọi attribute final)
@FieldDefaults(level = AccessLevel.PRIVATE ,makeFinal = true)// private final -> cho tất cả biến
@Slf4j //log của lombok
public class ApplicationInitConfig {


    PasswordEncoder passwordEncoder;


    //Tạo amdin lúc RunApp(nếu chưa có )
    @Bean
    ApplicationRunner applicationRunner(UserRepository userRepository) {
       return args ->  {
           //Chưa có thì tạo
           if(userRepository.findByUsername("admin").isEmpty()){
               var role = new HashSet<String>();
               role.add(Role.ADMIN.name());
               User user = User.builder()
                               .username("admin")
                                .password(passwordEncoder.encode("12345678"))
                       //.roles(role).
                       .build();
               userRepository.save(user);
               log.warn("Admin has been created with default password");
           }
       };
    }
}
