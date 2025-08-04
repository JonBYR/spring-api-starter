package com.codewithmosh.store.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class LowercaseValidator implements ConstraintValidator<Lowercase, String> { //class implements validation logic for annotation
    @Override
    public boolean isValid(String s, ConstraintValidatorContext context) {
        if(s == null) return true;
        else {
            return s.equals(s.toLowerCase());
        }
    }
}
