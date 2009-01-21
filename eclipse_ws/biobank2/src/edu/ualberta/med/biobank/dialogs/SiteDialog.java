package edu.ualberta.med.biobank.dialogs;

import org.eclipse.core.databinding.AggregateValidationStatus;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.PojoObservables;
import org.eclipse.core.databinding.observable.ChangeEvent;
import org.eclipse.core.databinding.observable.IChangeListener;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import edu.ualberta.med.biobank.model.Address;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.validators.EmailAddress;

public class SiteDialog extends TitleAreaDialog {	
	private static final String OK_MESSAGE = "Creates a new BioBank site.";
	private static final String MESSAGE = "Site must have a name";
	private Site site;
	private Text name;
	private ControlDecoration nameDecorator;
	private Text street1;
	private Text street2;
	private Text city;
	private Combo province;
	private Text phoneNumber;
	private Text faxNumber;
	private Text email;
	private ControlDecoration emailDecorator;
		
	// used to select a list	
	private static final String[] provinces = new String[] {
			"Alberta", 
			"British Columbia",
			"Manitoba",
			"New Brunswick",
			"Newfoundland and Labrador",
			"Northwest Territories",
			"Nova Scotia",
			"Nunavut",
			"Ontario",
			"Prince Edward Island",
			"Quebec",
			"Saskatchewan",
			"Yukon"
	};	
		
	private Button okButton;
	private boolean editMode = false;

	public SiteDialog(Shell parentShell) {
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.RESIZE);
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
        setMessage(OK_MESSAGE);
        return contents;
    }
	
    protected void createButtonsForButtonBar(Composite parent) {
        okButton = createButton(parent, IDialogConstants.OK_ID,
                IDialogConstants.OK_LABEL, true);
        createButton(parent, IDialogConstants.CANCEL_ID,
                IDialogConstants.CANCEL_LABEL, false);
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
		contents.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		contents.setFont(parentComposite.getFont());
				
		Group group = new Group(contents, SWT.SHADOW_NONE);
		group.setText("Site");
		group.setLayout(new GridLayout(2, false));
		group.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
			
		Label label = new Label(group, SWT.LEFT);
		label.setText("Name:");		
		name = new Text(group, SWT.SINGLE | SWT.BORDER);
		name.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		nameDecorator = createDecorator(name, MESSAGE);
		
		group = new Group(contents, SWT.SHADOW_NONE);
		group.setText("Site Address");
		group.setLayout(new GridLayout(2, false));
		group.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		
		label = new Label(group, SWT.LEFT);
		label.setText("Street 1:");
				
		street1 = new Text(group, SWT.SINGLE | SWT.BORDER);
		street1.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		
		label = new Label(group, SWT.LEFT);
		label.setText("Street 2:");
				
		street2 = new Text(group, SWT.SINGLE | SWT.BORDER);
		street2.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		
		label = new Label(group, SWT.LEFT);
		label.setText("City:");
				
		city = new Text(group, SWT.SINGLE | SWT.BORDER);
		city.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		
		label = new Label(group, SWT.LEFT);
		label.setText("Province:");
		
		province = new Combo(group, SWT.READ_ONLY);
		province.setItems(provinces);
		province.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		
		label = new Label(group, SWT.LEFT);
		label.setText("Telephone:");
				
		phoneNumber = new Text(group, SWT.SINGLE | SWT.BORDER);
		phoneNumber.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		
		label = new Label(group, SWT.LEFT);
		label.setText("Fax Number:");
				
		faxNumber = new Text(group, SWT.SINGLE | SWT.BORDER);
		faxNumber.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		
		label = new Label(group, SWT.LEFT);
		label.setText("Email Address:");
				
		email = new Text(group, SWT.SINGLE | SWT.BORDER);
		email.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		emailDecorator = createDecorator(email, MESSAGE);
		
		site = new Site();
		site.setAddress(new Address());
		bindValues();
		
		// When adding help uncomment line below
		// PlatformUI.getWorkbench().getHelpSystem().setHelp(composite, IJavaHelpContextIds.XXXXX);
		
		return parentComposite;
	}
	
	protected void okPressed() {
		super.okPressed();
	}
    
    private ControlDecoration createDecorator(Text text, String message) {
		ControlDecoration controlDecoration = new ControlDecoration(text,
				SWT.LEFT | SWT.TOP);
		controlDecoration.setDescriptionText(message);
		FieldDecoration fieldDecoration = FieldDecorationRegistry.getDefault()
				.getFieldDecoration(FieldDecorationRegistry.DEC_ERROR);
		controlDecoration.setImage(fieldDecoration.getImage());
		return controlDecoration;
	}
    
    private void bindValues() {
    	DataBindingContext dbc = new DataBindingContext();
    	UpdateValueStrategy update = new UpdateValueStrategy();
    	
    	IObservableValue statusObservable = new WritableValue();
    	statusObservable.addChangeListener(new IChangeListener() {
			public void handleChange(ChangeEvent event) {
				IObservableValue validationStatus 
				= (IObservableValue) event.getSource(); 
				IStatus bindStatus = (IStatus) validationStatus.getValue(); 
				if (bindStatus.getSeverity() == IStatus.OK) {
					setMessage(OK_MESSAGE);
					//okButton.setEnabled(true);
				}
				else {
					setMessage(bindStatus.getMessage(), IMessageProvider.ERROR);
				}		
			}
    	}); 
    	
    	Address address = site.getAddress();

    	dbc.bindValue(SWTObservables.observeText(name, SWT.Modify),
    			PojoObservables.observeValue(site, "name"), update, null);
    	dbc.bindValue(SWTObservables.observeText(street1, SWT.Modify),
    			PojoObservables.observeValue(address, "street"), null, null);
    	// TODO add Address.street2 to model
    	dbc.bindValue(SWTObservables.observeText(city, SWT.Modify),
    			PojoObservables.observeValue(address, "city"), null, null);
    	dbc.bindValue(SWTObservables.observeSelection(province),
    			PojoObservables.observeValue(address, "province"), null, null);
    	dbc.bindValue(SWTObservables.observeText(phoneNumber, SWT.Modify),
    			PojoObservables.observeValue(address, "phoneNumber"), null, null);
    	dbc.bindValue(SWTObservables.observeText(faxNumber, SWT.Modify),
    			PojoObservables.observeValue(address, "faxNumber"), null, null);
    	dbc.bindValue(SWTObservables.observeText(email, SWT.Modify),
    			PojoObservables.observeValue(address, "email"), 
    			new UpdateValueStrategy().setAfterConvertValidator(
    					new EmailAddress(MESSAGE, emailDecorator)), 
    			null);
    	
    	dbc.bindValue(statusObservable, new AggregateValidationStatus(
    			dbc.getBindings(), AggregateValidationStatus.MAX_SEVERITY),
    			null, null); 
    }
}
