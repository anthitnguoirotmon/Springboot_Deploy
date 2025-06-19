package com.theanh1301.myapp.dto.request;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

//Chuẩn hóa response cho tất cả api trong hệ thống thay vì chỉ in ra dòng lỗi
//-> Nữa đổi tên class này thành ApiResponse
@JsonInclude(JsonInclude.Include.NON_NULL) //cái nào null bỏ khỏi json
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NormalizeApiRequest<T> {

    int code = 1000; //1000 là thành công mình tự quy định trong tài liệu
    String message;
    T result ;


}
