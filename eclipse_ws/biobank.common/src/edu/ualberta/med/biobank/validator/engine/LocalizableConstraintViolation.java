package edu.ualberta.med.biobank.validator.engine;

import javax.validation.ConstraintViolation;
import javax.validation.MessageInterpolator.Context;
import javax.validation.metadata.ConstraintDescriptor;

import org.hibernate.validator.engine.MessageInterpolatorContext;

import edu.ualberta.med.biobank.i18n.AbstractLocalizable;
import edu.ualberta.med.biobank.validator.messageinterpolator.OgnlMessageInterpolator;

public class LocalizableConstraintViolation extends AbstractLocalizable {
    private static final long serialVersionUID = 1L;
    private static final OgnlMessageInterpolator interpolator =
        new OgnlMessageInterpolator();

    private final ConstraintViolation<?> cv;

    protected LocalizableConstraintViolation(ConstraintViolation<?> cv) {
        super(cv.getMessageTemplate());

        this.cv = cv;
    }

    @Override
    public String getString() {
        String template = cv.getMessageTemplate();

        ConstraintDescriptor<?> descriptor = cv.getConstraintDescriptor();
        Object rootBean = cv.getRootBean();
        Context context = new MessageInterpolatorContext(descriptor, rootBean);

        String string = interpolator.interpolate(template, context);

        return string;
    }
}
