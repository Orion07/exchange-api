package com.orion.exchangeapi.exception;

import com.orion.exchangeapi.model.BaseResponseModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class RestExceptionHandler {

    private final Logger LOG = LoggerFactory.getLogger(getClass());

    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponseModel> exception(Exception exception) {
        LOG.error("::exception ", exception);
        BaseResponseModel response = new BaseResponseModel();
        response.setErrorMessage(exception.getMessage());
        response.setSuccess(false);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<BaseResponseModel> apiException(ApiException exception) {
        BaseResponseModel response = new BaseResponseModel();
        response.setErrorMessage(exception.getMessage());
        response.setSuccess(false);
        return new ResponseEntity<>(response, exception.getHttpStatus());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BaseResponseModel> methodArgumentNotValidException(MethodArgumentNotValidException exception) {
        BindingResult bindingResult = exception.getBindingResult();

        BaseResponseModel response = new BaseResponseModel();
        if(bindingResult != null && !CollectionUtils.isEmpty(bindingResult.getAllErrors())) {
            ObjectError objectError = bindingResult.getAllErrors().get(0);
            response.setErrorMessage(objectError.getDefaultMessage());
        } else {
            response.setErrorMessage(exception.getMessage());
        }
        response.setSuccess(false);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
