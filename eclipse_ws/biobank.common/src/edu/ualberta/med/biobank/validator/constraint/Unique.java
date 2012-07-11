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

import edu.ualberta.med.biobank.validator.constraint.impl.UniqueValidator;

/**
 * Asserts that the annotated bean has a unique set of the given properties.
 * 
 * @author Jonathan Ferland
 */
@Documented
@Constraint(validatedBy = { UniqueValidator.class })
@Target({ TYPE, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER })
@Retention(RUNTIME)
public @interface Unique {
    String message() default "";

    String[] properties();

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * Defines several {@code @Unique} annotations on the same element.
     */
    @Target({ TYPE, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER })
    @Retention(RUNTIME)
    @Documented
    public @interface List {
        Unique[] value();
    }
}
