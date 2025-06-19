package com.theanh1301.myapp.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level= AccessLevel.PRIVATE)
@Entity
public class InvalidatedToken {
    @Id
    String id; // chính là jwtId
    Date expiryTime; // thời hạn -> để có thể quét và xóa di token het han tránh bị phình
}
