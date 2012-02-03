package edu.ualberta.med.biobank.validator.messageinterpolator;

import java.awt.Dimension;
import java.util.IllegalFormatException;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.validation.MessageInterpolator;
import javax.validation.ValidationException;
import javax.validation.MessageInterpolator.Context;

import ognl.Ognl;
import ognl.OgnlContext;
import ognl.OgnlException;

import org.hibernate.validator.messageinterpolation.ResourceBundleMessageInterpolator;

/**
 * Adds custom
 * 
 * @author Jonathan Ferland
 * 
 */
public class OgnlMessageInterpolator implements MessageInterpolator {
    public static final String VALIDATED_VALUE_KEYWORD = "validatedValue";

    private static final Pattern EXPRESSION_PATTERN = Pattern
        .compile("(\\{[^\\}]+?\\})");
    private final MessageInterpolator delegate;
    private final Locale defaultLocale;

    public OgnlMessageInterpolator() {
        this(null);
    }

    public OgnlMessageInterpolator(MessageInterpolator userMessageInterpolator) {
        defaultLocale = Locale.getDefault();
        if (userMessageInterpolator == null) {
            this.delegate = new ResourceBundleMessageInterpolator();
        }
        else {
            this.delegate = userMessageInterpolator;
        }
    }

    @Override
    public String interpolate(String message, Context context) {
        return interpolate(message, context, defaultLocale);
    }

    @Override
    public String interpolate(String message, Context context, Locale locale) {
        String tmp = delegate.interpolate(message, context, locale);
        return interpolateMessage(tmp, context.getValidatedValue(), locale);
    }

    /**
     * Interpolate the validated value in the given message.
     * 
     * @param message the message where validated value have to be interpolated
     * @param validatedValue the value of the object being validated
     * @param locale the {@code Locale} to use for message interpolation
     * 
     * @return the interpolated message
     */
    private String interpolateMessage(String message, Object validatedValue,
        Locale locale) {
        String interpolatedMessage =
            replaceOgnlExpressions(message, validatedValue);
        return interpolatedMessage;
    }

    private String replaceOgnlExpressions(String message, Object validatedValue) {
        Matcher matcher = EXPRESSION_PATTERN.matcher(message);
        StringBuffer sb = new StringBuffer();

        while (matcher.find()) {
            String parameter = matcher.group(1);

            try {
                Object expr = Ognl.parseExpression(parameter);
                OgnlContext ctx = new OgnlContext();
                Object value = Ognl.getValue(parameter, validatedValue);

                matcher.appendReplacement(sb, value.toString());

                System.out.println(value);
            } catch (OgnlException e) {
                // TODO: throw?
                System.err.println("TROUBBBBLLLLEEEEEEEEE!");
            }
        }

        matcher.appendTail(sb);

        return sb.toString();
    }
}