package edu.ualberta.med.biobank.dialogs;

import java.lang.reflect.Field;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import edu.ualberta.med.biobank.model.Address;

public class SiteDialog extends Dialog {

	public SiteDialog(Shell parentShell) {
		super(parentShell);
	}

	protected Control createDialogArea(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));
		
		Label nameLabel = new Label(composite, SWT.NONE);
		nameLabel.setText("Site Name:");
		nameLabel.setLayoutData(new GridData(GridData.END, GridData.CENTER,
				false, false));
		
		final Text nameText = new Text(composite, SWT.BORDER | SWT.SINGLE);
		nameText.setLayoutData(new GridData(GridData.FILL, GridData.FILL,
				true, false));
		
		createAddressControls(parent, Address.class);
		
		return composite;
	}
	
	protected void createAddressControls(Composite parent, Class<?> c) {
		String name = c.getName();
		Group classGroup = new Group(parent, SWT.SHADOW_IN);
		classGroup.setLayout(new GridLayout(2, false));
		classGroup.setText(name.substring(name.lastIndexOf(".") + 1, name.length()));
		
		for (Field f : c.getDeclaredFields()) {
			String fname = f.getName(); 
			if (fname.equals("id") || fname.equals("serialVersionUID")) continue;
			
			Label label = new Label(classGroup, SWT.NONE);
			label.setText(toTitleCase(f.getName()) + ":");
			label.setLayoutData(new GridData(GridData.END, GridData.CENTER,
					false, false));
			
			final Text text = new Text(classGroup, SWT.BORDER | SWT.SINGLE);
			text.setLayoutData(new GridData(GridData.FILL, GridData.FILL,
					true, false));
		}
	}
	
	/**
     * Converts the string with a camel case or with underscores and replace it 
     * with spaces between each word, and underscores replaced by spaces.
     * For example "javaProgramming" and "JAVA_PROGRAMMING"
     * would both become Java Programming".
     * @param str The String to convert
     * @return The converted String
     */
    public static String toTitleCase(String str) {
        if ((str == null) || (str.length() == 0)) return str;
 
        StringBuffer result = new StringBuffer(); 
        // Pretend space before first character
        char prevChar = ' ';
 
        // Change underscore to space, insert space before capitals
        for (int i = 0; i < str.length(); i++ ) {
            char c = str.charAt(i);
            if (c == '_') {
                result.append(' ');
            }
            else if ((prevChar == ' ') || (prevChar == '_')) {
                result.append( Character.toUpperCase( c ) );
            }
            else if (Character.isUpperCase(c) && !Character.isUpperCase(prevChar)) {
                // Insert space before start of word if camel case
                result.append(' ');
                result.append(Character.toUpperCase(c));
            }
            else {
                result.append(c);
            }
 
            prevChar = c;
        }
 
        return result.toString();
    }

}
