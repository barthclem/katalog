package com.klem.catalog.validations;

import org.springframework.util.StringUtils;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;
import java.util.Objects;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ListNotEmpty.ListNotEmptyValidator.class)
public @interface ListNotEmpty {

    String message() default "{ListNotEmpty.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

     class ListNotEmptyValidator implements ConstraintValidator<ListNotEmpty, List<String>> {

        @Override
        public boolean isValid(List<String> strs, ConstraintValidatorContext context) {
            System.out.print("\n\n\nValidate List");
            System.out.print(strs);
            return !Objects.isNull(strs) &&
                    !strs.isEmpty() && strs.stream().noneMatch(StringUtils::isEmpty);
        }

    }

}


