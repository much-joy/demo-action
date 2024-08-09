package com.action.springValidation.controller;

import com.action.response.ResponseResult;
import com.action.annotation.ValidCustom;
import com.action.springValidation.dto.ValidParam;
import com.action.springValidation.utils.AddValidationGroup;
import com.action.springValidation.utils.EditValidationGroup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/v1/valid")
public class ValidController {


    /***
     * 校验掩饰接口
     *在添加记录的时候，userId 不能参与不能为空的校验，所以使用分组校验com.action.springValidation.controller.ValidController#add(com.action.springValidation.dto.ValidParam, org.springframework.validation.BindingResult)
     */
    @PostMapping("/oldAdd")
    public ResponseResult<String> oldAdd(@Valid @RequestBody ValidParam param, BindingResult bindingResult){

        if (bindingResult.hasErrors()){
            List<ObjectError> errors = bindingResult.getAllErrors();
            errors.forEach(p -> {
                FieldError fieldError = (FieldError) p;
                log.error("Invalid Parameter : object - {},field - {},errorMessage - {}", fieldError.getObjectName(), fieldError.getField(), fieldError.getDefaultMessage());
            });
            return ResponseResult.fail("invalid parameter");
        }

        return ResponseResult.success("success");
    }

    @PostMapping("/add")
    public ResponseResult<String> add(@Validated(AddValidationGroup.class) @RequestBody ValidParam param, BindingResult bindingResult){
        if (bindingResult.hasErrors()){
            List<ObjectError> errors = bindingResult.getAllErrors();
            errors.forEach(p -> {
                FieldError fieldError = (FieldError) p;
                log.error("Invalid Parameter : object - {},field - {},errorMessage - {}", fieldError.getObjectName(), fieldError.getField(), fieldError.getDefaultMessage());
            });
            return ResponseResult.fail("invalid parameter");
        }
        return ResponseResult.success("success");
    }

    @PostMapping("/edit")
    public ResponseResult<String> edit(@Validated(EditValidationGroup.class) @RequestBody ValidParam param, BindingResult bindingResult){

        if (bindingResult.hasErrors()){
            List<ObjectError> errors = bindingResult.getAllErrors();
            errors.forEach(p -> {
                FieldError fieldError = (FieldError) p;
                log.error("Invalid Parameter : object - {},field - {},errorMessage - {}", fieldError.getObjectName(), fieldError.getField(), fieldError.getDefaultMessage());
            });
            return ResponseResult.fail("invalid parameter");
        }
        return ResponseResult.success("success");
    }


    @PostMapping("/customValid1")
    public ResponseResult<String> customValid1(@ValidCustom @RequestBody ValidParam param, BindingResult bindingResult){

        if (bindingResult.hasErrors()){
            List<ObjectError> errors = bindingResult.getAllErrors();
            errors.forEach(p -> {
                FieldError fieldError = (FieldError) p;
                log.error("Invalid Parameter : object - {},field - {},errorMessage - {}", fieldError.getObjectName(), fieldError.getField(), fieldError.getDefaultMessage());
            });
            return ResponseResult.fail("invalid parameter");
        }
        return ResponseResult.success("success");
    }

    @PostMapping("/customValid2")
    public ResponseResult<String> customValid2(@ValidCustom @RequestBody ValidParam param){

        return ResponseResult.success("success");
    }
}
