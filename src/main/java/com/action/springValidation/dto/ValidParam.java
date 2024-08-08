package com.action.springValidation.dto;

import com.action.springValidation.annotation.ValidCustom;
import com.action.springValidation.utils.AddValidationGroup;
import com.action.springValidation.utils.EditValidationGroup;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.*;

/**
 * 请求参数校验模拟实体类
 */
@Data
public class ValidParam {

    private static final long serialVersionUID = 1L;

    @NotEmpty(message = "could not be empty",groups = {EditValidationGroup.class}) //这里定为空，对于addUser时是不合适的（更新不可以为空）
    private String userId;

    @NotEmpty(message = "could not be empty",groups = {EditValidationGroup.class, AddValidationGroup.class})
    @Email(message = "invalid email")
    private String email;

    @NotEmpty(message = "could not be empty" ,groups = {EditValidationGroup.class, AddValidationGroup.class})
    @Pattern(regexp = "^(\\d{6})(\\d{4})(\\d{2})(\\d{2})(\\d{3})([0-9]|X)$", message = "invalid ID")
    private String cardNo;

    @NotEmpty(message = "could not be empty" ,groups = {EditValidationGroup.class, AddValidationGroup.class})
    @Length(min = 1, max = 10, message = "nick name should be 1-10")
    private String nickName;

    @NotNull(message = "could not be empty",groups = {EditValidationGroup.class, AddValidationGroup.class})
    @Range(min = 0, max = 1, message = "sex should be 0-1")
    private Integer sex;

    @Max(value = 100, message = "Please input valid age",groups = {EditValidationGroup.class, AddValidationGroup.class})
    private Integer age;

    /**
     * address是user的一个嵌套属性, 只能用@Valid
     */
//    @Valid
//    private AddressParam address;

    @ValidCustom(allowNull = false)
    private String message;


}
