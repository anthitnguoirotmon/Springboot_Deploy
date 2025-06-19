package com.theanh1301.myapp.exception;


import com.theanh1301.myapp.dto.request.NormalizeApiRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintViolation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

//Quan ly exception
@Slf4j
@ControllerAdvice // dung chung
public class GlobalExceptionHandler {

    private static final String MIN_ATTRIBUTE = "min"; // phải đúng với trong message ,
    //nếu có max thêm max vào vì mình mới xu lý min
    //Các exception không phải các loại đã bắt bên dưới
    @ExceptionHandler(value =Exception.class)
    public ResponseEntity<NormalizeApiRequest> handleException(Exception ex){

        NormalizeApiRequest response = new NormalizeApiRequest();
        response.setCode(ErrorCode.UNCATEGORIZED_EXCEPTION.getCode());
        response.setMessage(ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response); // STATUS mình tự fix
    }

    //Exception trong GlobalException mình bắt
    @ExceptionHandler(value = AppException.class)
    public ResponseEntity<NormalizeApiRequest> handleAppException(AppException myexception){

        ErrorCode errorCode = myexception.getErrorCode(); // lấy cái EnumError ra
        NormalizeApiRequest response = new NormalizeApiRequest();
        response.setCode(errorCode.getCode()); //code này là lỗi mình tự quy định
        response.setMessage(errorCode.getMessage());
        return ResponseEntity.status(errorCode.getStatusCode()).body(response);
        //Sẽ in ra nhưng lỗi theo trong message của runtimeexception không cần
        //try catch
    }


    //Phần exception cho dto.request  ->MethodArgumentNotValidException do mình in ra trước khi có lỗi này nên
    //biet duoc la exception nay
    @ExceptionHandler(value= MethodArgumentNotValidException.class)
    public ResponseEntity<NormalizeApiRequest> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception){
        String enumKey = exception.getFieldError().getDefaultMessage();//lấy key "USERNAME_INVALID"
        ErrorCode errorCode = ErrorCode.INVALID_KEY;
        Map<String,Object> attributes = null;
        //Nếu lỡ mình nhập key có bị sai (thiếu sót sai chính tả bên dto.request) thì bắt ở đây
        try{
            errorCode = ErrorCode.valueOf(enumKey);
            //Xử lý viec lay params trong exception
            var constrainViolation = exception.getBindingResult().getAllErrors().getFirst().unwrap(ConstraintViolation.class);

            attributes = constrainViolation.getConstraintDescriptor().getAttributes();

            log.info(attributes.toString());

        }catch (IllegalArgumentException e){

        }
        NormalizeApiRequest response = new NormalizeApiRequest();
        response.setCode(errorCode.getCode()); //code này là lỗi mình tự quy định
        response.setMessage(Objects.nonNull(attributes) ?
                mapAttribute(errorCode.getMessage(),attributes):
                errorCode.getMessage());
        return ResponseEntity.badRequest().body(response); // lay mesaage tren vailidation
    }
    //Hàm xử lý map min của tuổi 18 vào message -> đơn giản là replace
    private String mapAttribute(String message, Map<String,Object> attributes){

        String minValue = attributes.get(MIN_ATTRIBUTE).toString();
        //thay thế {min} trong message thành value trong attribute  ({groups=[Ljava.lang.Class;@651a13b8, min=18, message=INVALID_DOB, payload=[Ljava.lang.Class;@6c526bda})
        return message.replace("{"+ MIN_ATTRIBUTE +"}", minValue);
    }


    //Phần exception này là 403 cho cái api lấy tất cả người dùng -> admin chỉ có quyền (user sẽ bị 403
    @ExceptionHandler(value= AccessDeniedException.class)
    public ResponseEntity<NormalizeApiRequest> handleAccessDeniedException(AccessDeniedException exception){
        ErrorCode errorCode = ErrorCode.UNAUTHORIZED; // lấy cái EnumError ra
        NormalizeApiRequest response = new NormalizeApiRequest();
        response.setCode(errorCode.getCode()); //code này là lỗi mình tự quy định
        response.setMessage(errorCode.getMessage());
        return ResponseEntity.status(errorCode.getStatusCode()).body(response);
    }

}
