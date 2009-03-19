package edu.ualberta.med.biobank.forms;

import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;

public class FormUtils {
    
    public static Label createLabelledField(FormToolkit toolkit, Composite parent, 
            String label) {
        toolkit.createLabel(parent, label, SWT.LEFT);
        Label field = toolkit.createLabel(parent, "", SWT.SINGLE);
        field.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
        return field;
    }
	
	public static Text createLabelledText(FormToolkit toolkit, Composite parent, 
			String label, int limit, String tip) {
		toolkit.createLabel(parent, label, SWT.LEFT);
        Text text  = toolkit.createText(parent, "", SWT.SINGLE);
        if (limit > 0) {
            text.setTextLimit(limit);
        }
        if (tip != null) {
            text.setToolTipText(tip);
        }
        text.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
        return text;
    }
    
    public static ControlDecoration createDecorator(Text text, String message) {
		ControlDecoration controlDecoration = new ControlDecoration(text,
				SWT.LEFT | SWT.TOP);
		controlDecoration.setDescriptionText(message);
		FieldDecoration fieldDecoration = FieldDecorationRegistry.getDefault()
				.getFieldDecoration(FieldDecorationRegistry.DEC_ERROR);
		controlDecoration.setImage(fieldDecoration.getImage());
		
		// make room for the decorator
		((GridData) text.getLayoutData()).horizontalIndent 
			= controlDecoration.getMarginWidth() 
			+ fieldDecoration.getImage().getBounds().width;		
		return controlDecoration;
	}
    
    public static Font getSectionFont() {
    	return new Font (null, "sans-serif", 9, SWT.BOLD);
    }
    
    public static Font getHeadingFont() {
    	return new Font (null, "sans-serif", 8, SWT.BOLD);
    }

}
