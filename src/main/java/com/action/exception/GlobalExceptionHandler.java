package com.action.exception;

import com.action.response.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import java.util.ArrayList;
import java.util.List;

/**
 * 全局异常处理
 */

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ResponseBody
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = { BindException.class, ValidationException.class, MethodArgumentNotValidException.class })
    public ResponseResult<List> handleParameterVerificationException(@NonNull Exception e) {
        List<String> list = new ArrayList<>();
        log.warn("Exception: {}", e.getMessage());
        if (e instanceof BindException) {
            BindingResult bindingResult = ((MethodArgumentNotValidException) e).getBindingResult();
            bindingResult.getAllErrors()
                    .forEach(a -> list.add(((FieldError) a).getField() + ": " + a.getDefaultMessage()));
        }else if (e instanceof ConstraintViolationException) {
            if (e.getMessage() != null) {
                list.add(e.getMessage());
            }
        } else {
            list.add("invalid parameter");
        }
        return ResponseResult.fail(null,String.join("; ", list));
    }
}
