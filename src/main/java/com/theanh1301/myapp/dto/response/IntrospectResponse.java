package com.theanh1301.myapp.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level= AccessLevel.PRIVATE)
@Builder
//Genarate token -> current_user
public class IntrospectResponse {
    boolean valid;
    //có thể thêm username password ... -> cho current_user
}
