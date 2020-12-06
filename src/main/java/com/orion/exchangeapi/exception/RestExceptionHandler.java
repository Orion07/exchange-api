package com.orion.exchangeapi.exception;

import com.orion.exchangeapi.model.BaseResponseModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class RestExceptionHandler {

    private final Logger LOG = LoggerFactory.getLogger(getClass());

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<BaseResponseModel> apiException(ApiException exception) {
        BaseResponseModel response = new BaseResponseModel();
        response.setErrorMessage(exception.getMessage());
        response.setSuccess(false);
        return new ResponseEntity<BaseResponseModel>(response, exception.getHttpStatus());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponseModel> exception(Exception exception) {
        LOG.error("::exception ", exception);
        BaseResponseModel response = new BaseResponseModel();
        response.setErrorMessage(exception.getMessage());
        response.setSuccess(false);
        return new ResponseEntity<BaseResponseModel>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    //org.springframework.web.bind.MethodArgumentNotValidException
}
