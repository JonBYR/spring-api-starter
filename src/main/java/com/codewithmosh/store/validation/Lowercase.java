package com.codewithmosh.store.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD) //specify where this annotation can be applied
@Retention(RetentionPolicy.RUNTIME) //specifies when this annotation is applied, in this case it is applied at runtime
@Constraint(validatedBy = LowercaseValidator.class) //specifies the class that implements the validation logic
public @interface Lowercase {
    String message() default "must be lowercase"; //default message which can be overridden
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
