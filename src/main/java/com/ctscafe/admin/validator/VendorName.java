package com.ctscafe.admin.validator;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Documented
@Constraint(validatedBy = VendorNameValidator.class)
@Target( { ElementType.METHOD, ElementType.FIELD, ElementType.TYPE, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface VendorName {

                String message() default "{VendorName}";          
                
                Class<?>[] groups() default {};

                Class<? extends Payload>[] payload() default {};

}

