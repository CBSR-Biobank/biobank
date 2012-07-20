package edu.ualberta.med.biobank.validator.constraint;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import edu.ualberta.med.biobank.validator.constraint.impl.EmptyValidator;

/**
 * Asserts that the annotated bean's given collection property is empty. This is
 * not done directly on the collection property itself in case that property is
 * not initialised. We always want this validation to be done.
 * 
 * @author Jonathan Ferland
 */
@Documented
@Constraint(validatedBy = { EmptyValidator.class })
@Target({ TYPE, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER })
@Retention(RUNTIME)
public @interface Empty {
    String message() default "";

    String property();

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * Defines several {@code @Empty} annotations on the same element.
     */
    @Target({ TYPE, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER })
    @Retention(RUNTIME)
    @Documented
    public @interface List {
        Empty[] value();
    }
}
