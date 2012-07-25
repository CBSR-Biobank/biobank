package edu.ualberta.med.biobank.i18n;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * Functions as a {@link Serializable} wrapper for {@link LString}s to use so
 * the string translation can be looked up in any environment, after
 * deserialization. As such, this class facilitates the generation of
 * {@link LString}s and {@link Template}s.
 * <p>
 * Abstract to encourage a constant {@link #bundleName}, which is then
 * referenced through the derived class, for example:
 * 
 * <pre>
 * public class Bundle extends AbstractBundle {
 *     public Bundle() {
 *         super(&quot;edu.ualberta.med.biobank.common.Messages&quot;);
 *     }
 * }
 * 
 * public class A {
 *     public static final Bundle bundle = new Bundle();
 *     public static final Tr MSG = bundle.tr(&quot;Hello, {0}&quot;);
 * }
 * </pre>
 * 
 * @author Jonathan Ferland
 */
public abstract class Bundle implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String bundleName;
    transient I18n i18n;

    protected Bundle(String bundleName) {
        this.bundleName = bundleName;
        this.i18n = getI18n();
    }

    public Tr tr(String text) {
        return new Tr(this, text);
    }

    public Trn trn(String text, String pluralText) {
        return new Trn(this, text, pluralText);
    }

    public Trc trc(String context, String text) {
        return new Trc(this, context, text);
    }

    public Trnc trnc(String context, String text, String pluralText) {
        return new Trnc(this, context, text, pluralText);
    }

    @SuppressWarnings("nls")
    @Override
    public String toString() {
        return "Bundle [bundleName=" + bundleName + "]";
    }

    private void readObject(ObjectInputStream in)
        throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        i18n = getI18n();
    }

    private I18n getI18n() {
        return I18nFactory.getI18n(getClass(), bundleName);
    }
}
