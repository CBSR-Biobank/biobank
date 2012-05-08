package edu.ualberta.med.biobank.validator.constraint.model.impl;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import edu.ualberta.med.biobank.model.AbstractPosition;
import edu.ualberta.med.biobank.model.ContainerType;
import edu.ualberta.med.biobank.validator.EventSourceAwareConstraintValidator;
import edu.ualberta.med.biobank.validator.constraint.model.InBounds;

@SuppressWarnings("nls")
public class InBoundsValidator
    extends EventSourceAwareConstraintValidator<Object>
    implements ConstraintValidator<InBounds, Object> {
    public static final String OUT_OF_BOUNDS =
        "{edu.ualberta.med.biobank.model.AbstractPosition.InBounds.outOfBounds}";

    @Override
    public void initialize(InBounds annotation) {
    }

    @Override
    public boolean isValidInEventSource(Object value,
        ConstraintValidatorContext context) {
        if (value == null) return true;
        if (!(value instanceof AbstractPosition)) return false;

        // TODO: note that this type of validation should probably be enforced
        // through foreign key constraints. Then the capacity and labeling
        // scheme can be changed?

        context.disableDefaultConstraintViolation();

        AbstractPosition position = (AbstractPosition) value;

        boolean isValid = checkBounds(position, context);
        return isValid;
    }

    private boolean checkBounds(AbstractPosition position,
        ConstraintValidatorContext context) {
        ContainerType ct = position.getHoldingContainer().getContainerType();

        Integer maxRow = ct.getRowCapacity();
        Integer maxCol = ct.getColCapacity();
        Integer row = position.getRow();
        Integer col = position.getCol();

        // extensive null checking here because nulls should be accounted for
        // elsewhere, validation should only be done on non-null values
        if (row != null && col != null && maxRow != null && maxCol != null
            && (row < 0 || row >= maxRow || col < 0 || col >= maxCol)) {
            context.buildConstraintViolationWithTemplate(OUT_OF_BOUNDS)
                .addNode("row")
                .addNode("col")
                .addConstraintViolation();
            return false;
        }
        return true;
    }
}
