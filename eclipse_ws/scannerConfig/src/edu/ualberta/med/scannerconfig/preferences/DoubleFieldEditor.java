package edu.ualberta.med.scannerconfig.preferences;

import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

public class DoubleFieldEditor extends StringFieldEditor {
    private double minValidValue = 0;

    private double maxValidValue = Double.MAX_VALUE;

    private static final int DEFAULT_TEXT_LIMIT = 10;

    private static final I18n i18n = I18nFactory
        .getI18n(DoubleFieldEditor.class);

    /**
     * Creates a new integer field editor
     */
    protected DoubleFieldEditor() {
    }

    /**
     * Creates an integer field editor.
     * 
     * @param name the name of the preference this field editor works on
     * @param labelText the label text of the field editor
     * @param parent the parent of the field editor's control
     */
    public DoubleFieldEditor(String name, String labelText, Composite parent) {
        this(name, labelText, parent, DEFAULT_TEXT_LIMIT);
    }

    /**
     * Creates an integer field editor.
     * 
     * @param name the name of the preference this field editor works on
     * @param labelText the label text of the field editor
     * @param parent the parent of the field editor's control
     * @param textLimit the maximum number of characters in the text.
     */
    @SuppressWarnings("nls")
    public DoubleFieldEditor(String name, String labelText, Composite parent,
        int textLimit) {
        init(name, labelText);
        setTextLimit(textLimit);
        setEmptyStringAllowed(false);
        setErrorMessage(i18n.tr("Invalid value"));
        createControl(parent);
    }

    /**
     * Sets the range of valid values for this field.
     * 
     * @param min the minimum allowed value (inclusive)
     * @param max the maximum allowed value (inclusive)
     */
    @SuppressWarnings("nls")
    public void setValidRange(double min, double max) {
        minValidValue = min;
        maxValidValue = max;
        setErrorMessage(i18n.tr("Invalid value"));
    }

    /*
     * (non-Javadoc) Method declared on StringFieldEditor. Checks whether the
     * entered String is a valid integer or not.
     */
    @Override
    protected boolean checkState() {
        Text text = getTextControl();

        if (text == null) {
            return false;
        }

        String numberString = text.getText();
        try {
            double number = Double.valueOf(numberString).doubleValue();
            if (number >= minValidValue && number <= maxValidValue) {
                clearErrorMessage();
                return true;
            }

            showErrorMessage();
            return false;

        } catch (NumberFormatException e1) {
            showErrorMessage();
        }

        return false;
    }

    /*
     * (non-Javadoc) Method declared on FieldEditor.
     */
    @Override
    protected void doLoad() {
        Text text = getTextControl();
        if (text != null) {
            double value = getPreferenceStore().getDouble(getPreferenceName());
            text.setText("" + value); //$NON-NLS-1$
            oldValue = "" + value; //$NON-NLS-1$
        }

    }

    /*
     * (non-Javadoc) Method declared on FieldEditor.
     */
    @Override
    protected void doLoadDefault() {
        Text text = getTextControl();
        if (text != null) {
            double value = getPreferenceStore().getDefaultDouble(
                getPreferenceName());
            text.setText("" + value); //$NON-NLS-1$
        }
        valueChanged();
    }

    /*
     * (non-Javadoc) Method declared on FieldEditor.
     */
    @Override
    protected void doStore() {
        Text text = getTextControl();
        if (text != null) {
            Double i = new Double(text.getText());
            getPreferenceStore().setValue(getPreferenceName(), i.doubleValue());
        }
    }

    /**
     * Returns this field editor's current value as an integer.
     * 
     * @return the value
     * @exception NumberFormatException if the <code>String</code> does not
     *                contain a parsable integer
     */
    public double getDoubleValue() throws NumberFormatException {
        return new Double(getStringValue()).doubleValue();
    }
}
