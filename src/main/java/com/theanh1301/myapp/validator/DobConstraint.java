package com.theanh1301.myapp.validator;

//Validate birthday -> mình tạo ra để có thể tự tạo ra annotation này (@interface)
//mình đã tham khảo cấu trúc của 1 annotation bằng cách Click vào nó ví dụ như @Size


import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

//Target là chọn validate sẽ được apply ở đâu ( field là ở biến)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME) // Annotation được xử lý lúc nào
//@Repeatable(List.class)  // không dùng
//@Documented  //không dùng
@Constraint(
        validatedBy = {DobValidator.class}
) //class chịu trách nhiệm validate cho annotation này -> lớp xử lý logic cho validator này
public @interface DobConstraint {

    String message() default "Invalid data of birth"; // điền gì điền

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    //property custom của mình

    int min();
}
