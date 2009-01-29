package edu.ualberta.med.biobank.forms;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.PojoObservables;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.validators.NonEmptyString;

public class SiteDialog extends AddressDialog {	
	private static final String OK_MESSAGE = "Creates a new BioBank site.";
	private static final String NO_SITE_NAME_MESSAGE = "Site must have a name";
	
	private final Site site = new Site();
	
	private String[] sessionNames;

	protected Combo session;
	private Text name;	
	private ControlDecoration nameDecorator;
	private boolean editMode = false;

	public SiteDialog(Shell parentShell, String[] sessionNames) {
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.RESIZE);
		this.sessionNames = sessionNames;
	}

	public SiteDialog(Shell parentShell, String[] sessionNames, boolean editMode) {
		this(parentShell, sessionNames);
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
        setMessage(OK_MESSAGE);
        return contents;
    }

	protected Control createDialogArea(Composite parent) {		
		Composite parentComposite = (Composite) super.createDialogArea(parent);

        Composite contents = new Composite(parentComposite, SWT.NONE);
		contents.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		contents.setFont(parentComposite.getFont());
				
		Group group = new Group(contents, SWT.SHADOW_NONE);
		group.setText("Site");
		group.setLayout(new GridLayout(2, false));
		group.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		
		if (sessionNames.length > 1) {			
			Label label = new Label(group, SWT.LEFT);
			label.setText("Session:");
			
			session = new Combo(group, SWT.READ_ONLY);
			session.setItems(sessionNames);
			session.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		}
		else {
			session = null;
		}
		
		name = createLabelledText(group, "Name:", 100, null);
		nameDecorator = createDecorator(name, NO_SITE_NAME_MESSAGE);
		
		createAddressArea(contents);
		
		bindValues();

		GridLayoutFactory.swtDefaults().applyTo(contents);
		
		
		// When adding help uncomment line below
		// PlatformUI.getWorkbench().getHelpSystem().setHelp(composite, IJavaHelpContextIds.XXXXX);
		
		return parentComposite;
	}
    
    private void bindValues() {
    	DataBindingContext dbc = new DataBindingContext();

    	dbc.bindValue(SWTObservables.observeText(name, SWT.Modify),
    			PojoObservables.observeValue(site, "name"), 
    			new UpdateValueStrategy().setAfterConvertValidator(
    					new NonEmptyString(NO_SITE_NAME_MESSAGE, nameDecorator)), 
    					null);
    	
    	super.bindValues(dbc);
    }
    
    protected void handleStatusChanged() {
    	int severity = currentStatus.getSeverity(); 
		okButton.setEnabled(severity == IStatus.OK);
		if (severity == IStatus.OK) {
			setMessage(OK_MESSAGE);
		}
		else {
			setMessage(currentStatus.getMessage(), IMessageProvider.ERROR);
		}		
    }
	
	protected void okPressed() {
		site.setAddress(address);
		String sessionName;
		if (session == null) {
			sessionName = sessionNames[0];
		}
		else {
			sessionName = session.getText();
		}
		
		try {
			BioBankPlugin.getDefault().createObject(sessionName, site);
		}
		catch (Exception exp) {
			exp.printStackTrace();
		}
		super.okPressed();
	}
}
