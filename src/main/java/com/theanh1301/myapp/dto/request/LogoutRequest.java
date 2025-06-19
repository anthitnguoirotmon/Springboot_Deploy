package com.theanh1301.myapp.dto.request;




import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level= AccessLevel.PRIVATE)
@Builder
public class LogoutRequest {
    String token;
}
