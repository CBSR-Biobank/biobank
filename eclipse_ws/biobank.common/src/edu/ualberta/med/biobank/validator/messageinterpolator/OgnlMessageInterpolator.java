package edu.ualberta.med.biobank.validator.messageinterpolator;

import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.validation.MessageInterpolator;

import ognl.Ognl;
import ognl.OgnlException;

import org.hibernate.validator.messageinterpolation.ResourceBundleMessageInterpolator;

/**
 * Enables the OGNL evaluation of anything of the format
 * <code>$&#123;ognl_expression&#125;</code>, where the root object for
 * evaluation is a {@link RootObject}.
 * 
 * @author Jonathan Ferland
 */
public class OgnlMessageInterpolator implements MessageInterpolator {
    private static final Pattern VARIABLE_START_PATTERN = Pattern
        .compile("\\$\\{");

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
        return interpolateMessage(tmp, context, locale);
    }

    /**
     * Interpolate the validated value in the given message.
     * 
     * @param message the message where validated value have to be interpolated
     * @param context the context of the object being validated
     * @param locale the {@code Locale} to use for message interpolation
     * 
     * @return the interpolated message
     */
    private String interpolateMessage(String message, Context context,
        Locale locale) {
        String interpolatedMessage = message;
        Matcher matcher = VARIABLE_START_PATTERN.matcher(message);

        while (matcher.find()) {
            int curlyBraceOpenings = 1;
            boolean inDoubleQuotes = false;
            boolean inSingleQuotes = false;
            int lastIndex = matcher.end();

            do {
                char current = message.charAt(lastIndex);

                if (current == '\'') {
                    if (!inDoubleQuotes && !isEscaped(message, lastIndex)) {
                        inSingleQuotes = !inSingleQuotes;
                    }
                } else if (current == '"') {
                    if (!inSingleQuotes && !isEscaped(message, lastIndex)) {
                        inDoubleQuotes = !inDoubleQuotes;
                    }
                } else if (!inDoubleQuotes && !inSingleQuotes) {
                    if (current == '{') {
                        curlyBraceOpenings++;
                    } else if (current == '}') {
                        curlyBraceOpenings--;
                    }
                }

                lastIndex++;

            } while (curlyBraceOpenings > 0 && lastIndex < message.length());

            // The validated value expression seems correct
            if (curlyBraceOpenings == 0) {
                String variable = message.substring(matcher.start(), lastIndex);
                String ognlExpression = extractContents(variable);

                String evaluation = evaluateOgnl(ognlExpression, context);

                String escapedVariable = Pattern.quote(variable);
                String escapedEvaluation = Matcher.quoteReplacement(evaluation);

                interpolatedMessage = interpolatedMessage.replaceFirst(
                    escapedVariable,
                    escapedEvaluation);
            }
        }

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