package com.action.springValidation.utils;

import com.action.springValidation.annotation.ValidCustom;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class CustomValidator implements ConstraintValidator<ValidCustom,Object> {


    private boolean allowNull;

    @Override
    public void initialize(ValidCustom constraintAnnotation) {
        this.allowNull = constraintAnnotation.allowNull();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext constraintValidatorContext) {
        if (value == null) {
            return allowNull;  // 可以选择是否允许 null 值
        }

        // 获取对象的类型
        Class<?> valueType = value.getClass();

        // 根据对象的类型进行不同的校验逻辑
        if (valueType.equals(String.class)) {
            // 校验字符串的示例
            return isValidString((String) value);
        } else if (valueType.equals(Integer.class)) {
            // 校验整数的示例
            return isValidInteger((Integer) value);
        }

        // 默认返回 false，表示类型不支持校验
        return false;
    }


    private boolean isValidString(String value) {
        // 例如，校验字符串的长度
        return value.length() > 5;
    }

    private boolean isValidInteger(Integer value) {
        // 例如，校验整数的范围
        return value >= 0 && value <= 100;
    }

}
