package edu.ualberta.med.biobank.test;

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
import edu.ualberta.med.biobank.util.NullUtil;

public class AssertConstraintViolation {
    private final Class<?> annotationClass;
    private final Map<String, Object> attrs = new HashMap<String, Object>();
    private final Option<Object> invalidValue = new Option<Object>();
    private final Option<Object> leafBean = new Option<Object>();
    private final Option<String> template = new Option<String>();
    private final Option<Path> propertyPath = new Option<Path>();
    private final Option<Object> rootBean = new Option<Object>();
    private final Option<Class<?>> rootBeanClass = new Option<Class<?>>();

    private AssertConstraintViolation(Class<?> annotationClass) {
        this.annotationClass = annotationClass;
    }

    public static AssertConstraintViolation onAnnotation(
        Class<?> annotationClass) {
        return new AssertConstraintViolation(annotationClass);
    }

    public AssertConstraintViolation withInvalidValue(Object invalidValue) {
        this.invalidValue.setValue(invalidValue);
        return this;
    }

    public AssertConstraintViolation withLeafBean(Object leafBean) {
        this.leafBean.setValue(leafBean);
        return this;
    }

    public AssertConstraintViolation withMessageTemplate(String template) {
        this.template.setValue(template);
        return this;
    }

    public AssertConstraintViolation withPropertyPath(Path propertyPath) {
        this.propertyPath.setValue(propertyPath);
        return this;
    }

    public AssertConstraintViolation withRootBean(Object rootBean) {
        this.rootBean.setValue(rootBean);
        return this;
    }

    public AssertConstraintViolation withRootBeanClass(Class<?> rootBeanClass) {
        this.rootBeanClass.setValue(rootBeanClass);
        return this;
    }

    public AssertConstraintViolation withAttribute(String key, Object value) {
        attrs.put(key, value);
        return this;
    }

    public void assertIn(ConstraintViolationException e) {
        assertIn(e.getConstraintViolations());
    }

    public void assertIn(Set<ConstraintViolation<?>> cvs) {
        boolean found = false;
        Collection<String> annotations = new ArrayList<String>();
        for (ConstraintViolation<?> cv : cvs) {
            assertTemplateFound(cv);
            found |= in(cv);

            Object annotation = cv.getConstraintDescriptor().getAnnotation();
            annotations.add(annotation.toString());
        }

        if (!found) {
            Assert.fail(ConstraintViolationException.class.getSimpleName()
                + " does not contain an expected "
                + ConstraintDescriptor.class.getSimpleName()
                + " with an annotation with properties: " + this.toString()
                + ". Instead, found the annotation(s): "
                + Arrays.toString(annotations.toArray()));
        }
    }

    @Override
    public String toString() {
        StringBuilder string = new StringBuilder();

        string.append("{annotationClass=");
        string.append(annotationClass.getName());

        string.append(",attrs={");
        for (Entry<String, Object> entry : attrs.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            string.append(key);
            string.append("=");
            string.append(Arrays.deepToString(new Object[] { value }));
        }
        string.append("}");

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
            string.append(",rootBeanClass=").append(rootBeanClass);
        }

        string.append("}");

        return string.toString();
    }

    private boolean in(ConstraintViolation<?> cv) {
        ConstraintDescriptor<?> cd = cv.getConstraintDescriptor();

        // the annotation seems to be proxy so cannot compare the classes
        // with the equals operator
        Object annotation = cd.getAnnotation();
        if (!annotationClass.isAssignableFrom(annotation.getClass())) {
            return false;
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