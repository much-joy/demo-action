package com.action.annotation;

import com.action.springValidation.utils.CustomValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义校验器
 */

@Constraint(validatedBy = CustomValidator.class)// 关联到 CustomValidator 校验器
@Target({ ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidCustom {
    String message() default "Invalid param"; //校验失败时的默认错误消息
    Class<?>[] groups() default {}; //注解的分组，校验分组相关的功能
    Class<? extends Payload>[] payload() default {};//校验的负载信息，不常用
    boolean allowNull() default true;  // 新增属性，用于控制是否允许 null 值
}
