package edu.ualberta.med.biobank.dialogs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class SiteDialog extends TitleAreaDialog {
	private static final HashMap<String, List<String>> groups = 
		new HashMap<String, List<String>>() {{
		put("Site", Arrays.asList("Name"));
		put("Address", Arrays.asList(
				"Street 1", "Street 2", "City", "Province", "Telephone",
				"Fax Number", "Email Address"));
		}};
	
	private ArrayList<Text> settings;

	public SiteDialog(Shell parentShell) {
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.RESIZE);
		settings = new ArrayList<Text>();
	}
	
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setText("BioBank Site Information");
	}
	
	protected Control createContents(Composite parent) {
        Control contents = super.createContents(parent);
        setTitle("Add New Site");
        setMessage("Creates a new BioBank site.");
        return contents;
    }


	protected Control createDialogArea(Composite parent) {
		Composite parentComposite = (Composite) super.createDialogArea(parent);

        Composite contents = new Composite(parentComposite, SWT.NONE);
		GridData gd = new GridData(GridData.CENTER, GridData.CENTER, true, true);
		contents.setLayoutData(gd);
		GridLayout layout = new GridLayout(1, true);
        layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
        layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
        layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
        layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
		contents.setLayout(layout);
		contents.setFont(parentComposite.getFont());
		
		Iterator<String> it = groups.keySet().iterator();
		while (it.hasNext()) {
			String gName = it.next();
			
			Group group = new Group(contents, SWT.NONE);
			group.setText(gName);
			GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
			group.setLayoutData(gridData);
			
			for (String f : groups.get(gName)) {
				new Label(group, SWT.NONE).setText(f + ":");
				
				Text text = new Text(group, SWT.BORDER | SWT.SINGLE);
				GridData textGridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
				textGridData.widthHint = 250;
				text.setLayoutData(textGridData);
				settings.add(text);
			}
		}
		
		GridData contentsGridData = new GridData(GridData.CENTER, GridData.CENTER, true, true);
		contents.setLayoutData(contentsGridData);
		
		return contents;
	}
}
