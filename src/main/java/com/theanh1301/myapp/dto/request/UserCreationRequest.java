package com.theanh1301.myapp.dto.request;

import com.theanh1301.myapp.entity.Role;
import com.theanh1301.myapp.exception.ErrorCode;
import com.theanh1301.myapp.validator.DobConstraint;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.Set;


//lombok nó lấy getter và setter cho mọi biến là private
@Data //của Lombok có sẳn getter setter contructor  toString hashCode......
//@Getter
//@Setter
@Builder // thay thế tạo obj cho set
@NoArgsConstructor // constructor không tham số  -> cần có vì @Data luôn tạo ra All tham số
@AllArgsConstructor  //constructor có all tham số
@FieldDefaults(level = AccessLevel.PRIVATE) // thay cho private
public class UserCreationRequest {
    //message = "tên của Error code"
    @Size(min = 3, message = "USERNAME_INVALID")
    String username;
//Dung validate
    @Size(min = 8, message ="PASSWORD_INVALID")
    String password;
    String firstName;
    String lastName;

    @DobConstraint(min=18 , message = "INVALID_DOB") // -> tuổi tối thiểu 18,  nhớ phải có ErrorCode riêng cho nó
    LocalDate birthday;
    Set<String> roles;


    //Dùng Builder bên service
    // Clean code
// User user = User.builder()
//                .username("theanh") // thay vì user.setUsername("theanh")
//                .password("12345678")
//                .build();





}
