package edu.ualberta.med.biobank.validator.messageinterpolator;

import java.util.Locale;
import java.util.Map;

import javax.validation.MessageInterpolator;

import org.hibernate.validator.messageinterpolation.ResourceBundleMessageInterpolator;


/**
 * Enables the OGNL evaluation of anything of the format
 * <code>$&#123;ognl_expression&#125;</code>, where the root object for
 * evaluation is a {@link RootObject}.
 * 
 * @author Jonathan Ferland
 */
public class OgnlMessageInterpolator implements MessageInterpolator {
    private final MessageInterpolator delegate;
    private final Locale defaultLocale;

    public OgnlMessageInterpolator() {
        this(null);
    }

    public OgnlMessageInterpolator(MessageInterpolator delegate) {
        if (delegate == null) {
            this.delegate = new ResourceBundleMessageInterpolator();
        } else {
            this.delegate = delegate;
        }

        defaultLocale = Locale.getDefault();
    }

    @Override
    public String interpolate(String message, Context context) {
        return interpolate(message, context, defaultLocale);
    }

    @Override
    public String interpolate(String message, Context context, Locale locale) {
        String tmp = delegate.interpolate(message, context, locale);
        return interpolateMessage(tmp, context);
    }

    private String interpolateMessage(String message, Context context) {
        RootObject root = new RootObject();
        root.validatedValue = context.getValidatedValue();
        root.attributes = context.getConstraintDescriptor().getAttributes();

        String interpolatedMessage = OgnlMessageFormatter.format(message, root);
        return interpolatedMessage;
    }

    static class RootObject {
        private Object validatedValue;
        private Map<String, Object> attributes;

        public Object getValidatedValue() {
            return validatedValue;
        }

        public Map<String, Object> getAttributes() {
            return attributes;
        }
    }
}