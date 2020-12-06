package com.orion.exchangeapi.exception;

import com.orion.exchangeapi.constants.ErrorEnum;
import org.springframework.http.HttpStatus;

public class ApiException extends RuntimeException {

    private HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;

    private ErrorEnum error;

    public ApiException(String message) {
        super(message);
    }

    public ApiException(ErrorEnum errorEnum) {
        super(errorEnum.getMessage());
        this.error = errorEnum;
    }

    public ApiException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public ApiException(ErrorEnum errorEnum, HttpStatus httpStatus) {
        super(errorEnum.getMessage());
        this.httpStatus = httpStatus;
        this.error = errorEnum;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public ErrorEnum getError() {
        return error;
    }
}
