package edu.ualberta.med.biobank.gui.common.forms;

import org.eclipse.swt.widgets.Widget;

import edu.ualberta.med.biobank.gui.common.validators.AbstractValidator;

/**
 * Stores information for displaying a field from one of the attributes of a
 * class in the ORM model.
 */
public class FieldInfo {
    public String label;
    public Class<? extends Widget> widgetClass;
    public int widgetOptions;
    public String[] widgetValues;
    public Class<? extends AbstractValidator> validatorClass;
    public String errMsg;

    public FieldInfo(String l, Class<? extends Widget> w, int options,
        String[] values, Class<? extends AbstractValidator> v, String msg) {
        label = l;
        widgetClass = w;
        widgetOptions = options;
        widgetValues = values;
        validatorClass = v;
        errMsg = msg;
    }
}
