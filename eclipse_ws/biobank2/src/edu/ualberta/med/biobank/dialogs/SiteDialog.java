package edu.ualberta.med.biobank.dialogs;

import java.util.Iterator;

import org.eclipse.core.databinding.AggregateValidationStatus;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.ValidationStatusProvider;
import org.eclipse.core.databinding.beans.PojoObservables;
import org.eclipse.core.databinding.observable.ChangeEvent;
import org.eclipse.core.databinding.observable.IChangeListener;
import org.eclipse.core.databinding.observable.IObservable;
import org.eclipse.core.databinding.observable.IStaleListener;
import org.eclipse.core.databinding.observable.StaleEvent;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.IValueChangeListener;
import org.eclipse.core.databinding.observable.value.ValueChangeEvent;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.layout.GridLayoutFactory;
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
import edu.ualberta.med.biobank.validators.NonEmptyString;
import edu.ualberta.med.biobank.widgets.PhoneNumber;

public class SiteDialog extends TitleAreaDialog {	
	private static final String OK_MESSAGE = "Creates a new BioBank site.";
	private static final String NO_SITE_NAME_MESSAGE = "Site must have a name";
	private static final String INVALID_EMAIL_MESSAGE = "Email address is invalid";
	
	private final Site site = new Site();
	
	private Text name;
	private ControlDecoration nameDecorator;
	private Text street1;
	private Text street2;
	private Text city;
	private Combo province;
	private PhoneNumber phoneNumber;
	private PhoneNumber faxNumber;
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

	private IObservableValue aggregateStatus;
	private IStatus currentStatus;
	private boolean currentStatusStale;

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
		okButton.setEnabled(false);
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
		
		name = createLabelledText(group, "Name:", 100, null);
		nameDecorator = createDecorator(name, NO_SITE_NAME_MESSAGE);
		
		group = new Group(contents, SWT.SHADOW_NONE);
		group.setText("Address");
		group.setLayout(new GridLayout(2, false));
		group.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

		street1 = createLabelledText(group, "Street 1:", 100, null);
		street2 = createLabelledText(group, "Street 2:", 100, null);
		city = createLabelledText(group, "City:", 100, null);
		
		Label label = new Label(group, SWT.LEFT);
		label.setText("Province:");
		
		province = new Combo(group, SWT.READ_ONLY);
		province.setItems(provinces);
		province.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

        new Label(group, SWT.LEFT).setText("Phone Number:");
		phoneNumber = new PhoneNumber(group, SWT.NONE);		
        new Label(group, SWT.LEFT).setText("Fax Number:");
		faxNumber = new PhoneNumber(group, SWT.NONE);
		
		email = createLabelledText(group, "Email Address:", 100, null);
		emailDecorator = createDecorator(email, INVALID_EMAIL_MESSAGE);
		
		site.setAddress(new Address());
		bindValues();

		GridLayoutFactory.swtDefaults().applyTo(contents);
		
		// When adding help uncomment line below
		// PlatformUI.getWorkbench().getHelpSystem().setHelp(composite, IJavaHelpContextIds.XXXXX);
		
		return parentComposite;
	}
	
	protected Text createLabelledText(Composite parent, String label, int limit, String tip) {
        new Label(parent, SWT.LEFT).setText(label);
        Text text  = new Text(parent, SWT.SINGLE);
        if (limit > 0) {
            text.setTextLimit(limit);
        }
        if (tip != null) {
            text.setToolTipText(tip);
        }
        text.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
        return text;
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
    	
    	Address address = site.getAddress();

    	dbc.bindValue(SWTObservables.observeText(name, SWT.Modify),
    			PojoObservables.observeValue(site, "name"), 
    			new UpdateValueStrategy().setAfterConvertValidator(
    					new NonEmptyString(NO_SITE_NAME_MESSAGE, nameDecorator)), 
    					null);
    	dbc.bindValue(SWTObservables.observeText(street1, SWT.Modify),
    			PojoObservables.observeValue(address, "street1"), null, null);
    	dbc.bindValue(SWTObservables.observeText(street2, SWT.Modify),
    			PojoObservables.observeValue(address, "street2"), null, null);
    	dbc.bindValue(SWTObservables.observeText(city, SWT.Modify),
    			PojoObservables.observeValue(address, "city"), null, null);
    	dbc.bindValue(SWTObservables.observeSelection(province),
    			PojoObservables.observeValue(address, "province"), null, null);
    	
    	phoneNumber.bindValues(dbc);
    	faxNumber.bindValues(dbc);
    	
    	dbc.bindValue(SWTObservables.observeText(email, SWT.Modify),
    			PojoObservables.observeValue(address, "email"), 
    			new UpdateValueStrategy().setAfterConvertValidator(
    					new EmailAddress(INVALID_EMAIL_MESSAGE, emailDecorator)), 
    					null);
    	
    	aggregateStatus = new AggregateValidationStatus(
    			dbc.getBindings(), AggregateValidationStatus.MAX_SEVERITY); 
    			
    	aggregateStatus.addValueChangeListener(new IValueChangeListener() {
			public void handleValueChange(ValueChangeEvent event) {
				currentStatus = (IStatus) event.diff.getNewValue();
				currentStatusStale = aggregateStatus.isStale();
				handleStatusChanged();
			}
    	}); 
		aggregateStatus.addStaleListener(new IStaleListener() {
			public void handleStale(StaleEvent staleEvent) {
				currentStatusStale = true;
				handleStatusChanged();
			}
		});
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
}
