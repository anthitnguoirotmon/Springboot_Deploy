package com.theanh1301.myapp.dto.response;

import com.theanh1301.myapp.entity.Role;
import jakarta.annotation.Nonnull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
//Để service và controller trả về dto -> chú không trả về hết mội thông tin (pojo có)
public class UserResponse {

    String id;
    String username;
//    String password;  // khong tra ra password
    String firstName;
    String lastName;

    //Response là là Set<RoleResponse> nhưng request lại là Set<String> để có thể gửi key
    Set<RoleResponse> roles;
}
