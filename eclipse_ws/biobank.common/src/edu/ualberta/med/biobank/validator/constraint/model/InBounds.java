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

import edu.ualberta.med.biobank.model.ContainerPosition;
import edu.ualberta.med.biobank.model.SpecimenPosition;
import edu.ualberta.med.biobank.validator.constraint.model.impl.InBoundsValidator;

/**
 * Asserts that the annotated {@link SpecimenPosition} or
 * {@link ContainerPosition} is valid. Errors on anything but those types.
 * 
 * @author Jonathan Ferland
 */
@Documented
@Constraint(validatedBy = { InBoundsValidator.class })
@Target({ TYPE, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER })
@Retention(RUNTIME)
public @interface InBounds {
    String message() default "{edu.ualberta.med.biobank.model.AbstractPosition.InBounds.illegalType}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}