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
	private boolean editMode = false;

	public SiteDialog(Shell parentShell) {
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.RESIZE);
		settings = new ArrayList<Text>();
	}

	public SiteDialog(Shell parentShell, boolean editMode) {
		this(parentShell);
		this.editMode = editMode;
	}
	
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setText("BioBank Site Information");
	}
	
	protected Control createContents(Composite parent) {
        Control contents = super.createContents(parent);
        if (editMode) {
        	setTitle("Edit Site Information");
        }
        else {
        	setTitle("Add New Site");
        }
        setMessage("Creates a new BioBank site.");
        return contents;
    }


	protected Control createDialogArea(Composite parent) {
		GridLayout layout;
		
		Composite parentComposite = (Composite) super.createDialogArea(parent);

        Composite contents = new Composite(parentComposite, SWT.NONE);
		layout = new GridLayout(1, false);
        layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
        layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
        layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
        layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
		contents.setLayout(layout);
		contents.setLayoutData(new GridData(GridData.FILL_BOTH));
		contents.setFont(parentComposite.getFont());
		
		Iterator<String> it = groups.keySet().iterator();
		while (it.hasNext()) {
			String gName = it.next();
			
			Group group = new Group(contents, SWT.SHADOW_NONE);
			group.setText(gName);
			group.setLayout(new GridLayout(2, false));
			group.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
			
			for (String f : groups.get(gName)) {
				Label label = new Label(group, SWT.LEFT);
				label.setText(f + ":");
				
				Text text = new Text(group, SWT.SINGLE | SWT.BORDER);
				text.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
				settings.add(text);
			}
		}
		
		//GridData contentsGridData = new GridData(GridData.CENTER, GridData.CENTER, true, true);
		//contents.setLayoutData(contentsGridData);
		
		return contents;
	}
}
