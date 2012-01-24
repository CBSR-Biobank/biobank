package edu.ualberta.med.biobank.common.action.constraint;

import java.util.Collections;
import java.util.Set;

import edu.ualberta.med.biobank.common.action.exception.ActionException;

public class ConstraintViolationException extends ActionException {
    private static final long serialVersionUID = 1L;

    private final Set<ConstraintViolation<?>> violations;

    public ConstraintViolationException(Set<ConstraintViolation<?>> violations) {
        this.violations = Collections.unmodifiableSet(violations);
    }

    public Set<ConstraintViolation<?>> getConstraintViolations() {
        return violations;
    }
}
