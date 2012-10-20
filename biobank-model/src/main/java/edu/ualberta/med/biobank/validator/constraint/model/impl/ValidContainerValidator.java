package edu.ualberta.med.biobank.validator.constraint.model.impl;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import edu.ualberta.med.biobank.model.center.Container;
import edu.ualberta.med.biobank.model.center.ContainerType;
import edu.ualberta.med.biobank.model.center.StorageContainerType;
import edu.ualberta.med.biobank.validator.EventSourceAwareConstraintValidator;
import edu.ualberta.med.biobank.validator.constraint.model.ValidContainer;

@SuppressWarnings("nls")
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

        Container<?> container = (Container<?>) value;

        boolean isValid = true;

        isValid &= checkParent(container, context);

        return isValid;
    }

    private boolean checkParent(Container<?> container,
        ConstraintValidatorContext context) {
        ContainerType ct = container.getContainerType();
        if (ct instanceof StorageContainerType) {
            StorageContainerType sct = (StorageContainerType) ct;
            if (sct.isTopLevel() && container.getParent() != null) {
                context.buildConstraintViolationWithTemplate(ILLEGAL_PARENT)
                    .addNode("parent")
                    .addConstraintViolation();
                return false;
            }
        }
        return true;
    }
}
