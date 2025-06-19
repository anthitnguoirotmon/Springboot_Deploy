package com.theanh1301.myapp.exception;


//Thay thế RuntimeException thành cái AppException (mình tự định nghĩa)
public class AppException extends RuntimeException {

    private ErrorCode errorCode;

    public AppException(ErrorCode errorCode) {
        super(errorCode.getMessage()); // đè lên getMessage() của RuntimeExcepion
        this.errorCode = errorCode; // là Errorcode enum sẽ truyền vào contructor
    }

    //Alt + Insert  -> tạo cho nhanh
    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }
}
