package edu.ualberta.med.biobank;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Path;
import javax.validation.metadata.ConstraintDescriptor;

import junit.framework.Assert;

import org.hibernate.validator.internal.engine.PathImpl;

import edu.ualberta.med.biobank.model.util.NullUtil;
import edu.ualberta.med.biobank.util.StringUtil;

public class ConstraintViolationAssertion {
    private final Map<String, Object> attrs = new HashMap<String, Object>();
    private final Option<Class<?>> annotationClass = new Option<Class<?>>();
    private final Option<Object> invalidValue = new Option<Object>();
    private final Option<Object> leafBean = new Option<Object>();
    private final Option<String> template = new Option<String>();
    private final Option<Path> propertyPath = new Option<Path>();
    private final Option<Object> rootBean = new Option<Object>();
    private final Option<Class<?>> rootBeanClass = new Option<Class<?>>();

    public ConstraintViolationAssertion withAnnotationClass(
        Class<?> annotationClass) {
        this.annotationClass.setValue(annotationClass);
        return this;
    }

    public ConstraintViolationAssertion withInvalidValue(Object invalidValue) {
        this.invalidValue.setValue(invalidValue);
        return this;
    }

    public ConstraintViolationAssertion withLeafBean(Object leafBean) {
        this.leafBean.setValue(leafBean);
        return this;
    }

    public ConstraintViolationAssertion withTemplate(String template) {
        this.template.setValue(template);
        return this;
    }

    public ConstraintViolationAssertion withPropertyPath(Path propertyPath) {
        this.propertyPath.setValue(propertyPath);
        return this;
    }

    public ConstraintViolationAssertion withPropertyPath(String propertyPath) {
        return withPropertyPath(PathImpl.createPathFromString(propertyPath));
    }

    public ConstraintViolationAssertion withRootBean(Object rootBean) {
        this.rootBean.setValue(rootBean);
        return this;
    }

    public ConstraintViolationAssertion withRootBeanClass(Class<?> rootBeanClass) {
        this.rootBeanClass.setValue(rootBeanClass);
        return this;
    }

    public ConstraintViolationAssertion withAttr(String key, Object value) {
        attrs.put(key, value);
        return this;
    }

    public void assertIn(ConstraintViolationException e) {
        assertIn(e.getConstraintViolations());
    }

    public void assertIn(Set<ConstraintViolation<?>> cvs) {
        boolean found = false;
        Collection<String> cvStrings = new ArrayList<String>();
        for (ConstraintViolation<?> cv : cvs) {
            assertTemplateFound(cv);
            found |= in(cv);

            Object annotation = cv.getConstraintDescriptor().getAnnotation();
            cvStrings.add("{ConstraintViolation=" + cv.toString()
                + ",annotation=" + annotation + "}");
        }

        if (!found) {
            Assert.fail("Cannot find a constraint violation"
                + " with the expected properties: " + this.toString()
                + ". Instead, found the constraint violation(s): "
                + StringUtil.join(cvStrings, "\r\n"));
        }
    }

    @Override
    public String toString() {
        StringBuilder string = new StringBuilder();

        string.append("attrs={");
        for (Entry<String, Object> entry : attrs.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            string.append(key);
            string.append("=");
            string.append(Arrays.deepToString(new Object[] { value }));
        }
        string.append("}");

        if (annotationClass.isSet()) {
            string.append(",annotationClass=")
                .append(annotationClass.getValue().getName());
        }

        if (invalidValue.isSet()) {
            string.append(",invalidValue=").append(invalidValue);
        }
        if (leafBean.isSet()) {
            string.append(",leafBean=").append(leafBean);
        }
        if (template.isSet()) {
            string.append(",messageTemplate=").append(template);
        }
        if (propertyPath.isSet()) {
            string.append(",propertyPath=").append(propertyPath);
        }
        if (rootBean.isSet()) {
            string.append(",rootBean=").append(rootBean);
        }
        if (rootBeanClass.isSet()) {
            string.append(",rootBeanClass=")
                .append(rootBeanClass.getValue().getName());
        }

        string.append("}");

        return string.toString();
    }

    private boolean in(ConstraintViolation<?> cv) {
        ConstraintDescriptor<?> cd = cv.getConstraintDescriptor();

        // the annotation seems to be proxy so cannot compare the classes
        // with the equals operator
        Object annotation = cd.getAnnotation();
        if (annotationClass.isSet()) {
            Class<?> expected = annotationClass.getValue();
            Class<?> actual = annotation.getClass();
            if (!expected.isAssignableFrom(actual)) {
                return false;
            }
        }
        if (!containsExpectedAttrs(cd.getAttributes())) return false;

        if (!invalidValue.matches(cv.getInvalidValue())) return false;
        if (!leafBean.matches(cv.getLeafBean())) return false;
        if (!template.matches(cv.getMessageTemplate())) return false;
        if (!propertyPath.matches(cv.getPropertyPath())) return false;
        if (!rootBean.matches(cv.getRootBean())) return false;
        if (!rootBeanClass.matches(cv.getRootBeanClass())) return false;

        return true;
    }

    private boolean containsExpectedAttrs(Map<String, Object> actual) {
        for (Entry<String, Object> expectedAttr : attrs.entrySet()) {
            String expectedKey = expectedAttr.getKey();
            Object expectedValue = expectedAttr.getValue();

            if (!actual.containsKey(expectedKey) ||
                !arrayWiseEq(actual.get(expectedKey), expectedValue)) {
                return false;
            }
        }
        return true;
    }

    private static boolean arrayWiseEq(Object a, Object b) {
        // because new String[] { "a" }.equals(new String[] { "a" }) is false.
        return Arrays.deepEquals(new Object[] { a }, new Object[] { b });
    }

    /**
     * Asserts a failure if the given {@link ConstraintViolation} has a missing
     * value for its message template, i.e. if its
     * {@link ConstraintViolation#getMessage()} is equal to its
     * {@link ConstraintViolation#getMessageTemplate()}.
     * 
     */
    private static void assertTemplateFound(ConstraintViolation<?> cv) {
        if (cv.getMessageTemplate().equals(cv.getMessage())) {
            Assert.fail("It seems the message template for " + cv
                + " could not be found: " + cv.getMessageTemplate());
        }
    }

    private static class Option<T> {
        private T value;
        private boolean set = false;

        public void setValue(T value) {
            this.value = value;
            this.set = true;
        }

        public T getValue() {
            return value;
        }

        public boolean isSet() {
            return set;
        }

        public boolean matches(T that) {
            return !set || NullUtil.eq(value, that);
        }

        @Override
        public String toString() {
            return value.toString();
        }
    }
}