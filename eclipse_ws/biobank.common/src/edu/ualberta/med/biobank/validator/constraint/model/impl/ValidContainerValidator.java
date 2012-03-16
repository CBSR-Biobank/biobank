package edu.ualberta.med.biobank.validator.constraint.model.impl;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.validator.EventSourceAwareConstraintValidator;
import edu.ualberta.med.biobank.validator.constraint.model.ValidContainer;

public class ValidContainerValidator
    extends EventSourceAwareConstraintValidator<Object>
    implements ConstraintValidator<ValidContainer, Object> {
    private static final String ILLEGAL_PARENT =
        "{edu.ualberta.med.biobank.model.Container.ValidContainer.illegalParent}";

    @Override
    public void initialize(ValidContainer annotation) {
    }

    @Override
    public boolean isValidInEventSource(Object value,
        ConstraintValidatorContext context) {
        if (value == null) return true;
        if (!(value instanceof Container)) return false;

        context.disableDefaultConstraintViolation();

        Container container = (Container) value;

        boolean isValid = true;

        isValid &= checkParent(container, context);

        return isValid;
    }

    private boolean checkParent(Container container,
        ConstraintValidatorContext context) {
        if (container.getPosition() != null
            && container.getContainerType().getTopLevel()) {
            // TODO: cannot be top-level and have a parent
            // TODO: must have a parent if not top-level
            context.buildConstraintViolationWithTemplate(ILLEGAL_PARENT)
                .addNode("position")
                .addConstraintViolation();
        }
        return true;
    }
}
