package com.theanh1301.myapp.dto.request;




import lombok.*;
import lombok.experimental.FieldDefaults;

//Ben client phai xu ly viec nhan token nay va xu ly
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level= AccessLevel.PRIVATE)
@Builder
public class RefreshTokenRequest {
    String token;
}
