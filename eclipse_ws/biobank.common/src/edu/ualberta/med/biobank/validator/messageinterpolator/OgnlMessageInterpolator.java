package edu.ualberta.med.biobank.validator.messageinterpolator;

import java.util.Locale;
import java.util.Map;

import javax.validation.MessageInterpolator;

import org.hibernate.validator.messageinterpolation.ResourceBundleMessageInterpolator;

import edu.ualberta.med.biobank.i18n.OgnlMessageFormatter;

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

    /**
     * 
     * @param variable a string matching <code>$&#123;contents&#125;</code>
     * @return contents
     */
    private String extractContents(String variable) {
        int start = 2;
        int end = variable.length() - 1;

        if (end < start) {
            throw new IndexOutOfBoundsException("");
        }

        return variable.substring(start, end);
    }

    /**
     * Return true if the char at the given index is preceded by a backslash in
     * the containing String.
     * 
     * @param string the containing string
     * @param charIndex the index of the character
     * 
     * @return true if the given character is escaped, otherwise false
     */
    private boolean isEscaped(String string, int charIndex) {
        if (charIndex < 0 || charIndex > string.length()) {
            throw new IndexOutOfBoundsException(
                "Index must be between 0 and string.length() - 1.");
        }
        return charIndex > 0 && string.charAt(charIndex - 1) == '\\';
    }

    private String evaluateOgnl(String expression, Context context) {
        String result = null;

        try {
            RootObject root = new RootObject();
            root.validatedValue = context.getValidatedValue();
            root.attributes = context.getConstraintDescriptor().getAttributes();

            Object value = Ognl.getValue(expression, root);

            result = String.valueOf(value);
        } catch (OgnlException e) {
            // TODO: something better?
            throw new RuntimeException(e);
        }

        return result;
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