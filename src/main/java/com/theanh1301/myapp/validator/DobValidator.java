package com.theanh1301.myapp.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

//Annotation ta validate , và kiểu dữ liệu của field ta validate
public class DobValidator implements ConstraintValidator<DobConstraint, LocalDate> {


    //Nhắn Ctrl + I để thấy các method cần implement
    private int min;

    //Kiểm tra data có đúng hay không
    @Override
    public boolean isValid(LocalDate localDate, ConstraintValidatorContext constraintValidatorContext) {
        if (localDate == null) {
            return true;
        }
        //so sánh vói ngày hiện tại đã qua nhiêu năm
        long  years = ChronoUnit.YEARS.between(localDate, LocalDate.now());

        return years >= min; // true false tự nó xử .

    }


    //Khởi tạo mỗi khi constraint được khởi tạo -> nghĩa là nó lấy giá trị
    @Override
    public void initialize(DobConstraint constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
        min = constraintAnnotation.min();  //constraintAnnotation.min(); này là min lấy từ DobConstraint
    }
}
