package edu.ualberta.med.biobank.validator.constraint.model;

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

import edu.ualberta.med.biobank.validator.constraint.model.impl.ValidContainerValidator;

/**
 * Asserts that the annotated {@link Container} is valid. Errors on anything but
 * a {@link Container}.
 * 
 * @author Jonathan Ferland
 */
@Documented
@Constraint(validatedBy = { ValidContainerValidator.class })
@Target({ TYPE, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER })
@Retention(RUNTIME)
public @interface ValidContainer {
    String message() default "{edu.ualberta.med.biobank.model.Container.ValidContainer.illegalType}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
