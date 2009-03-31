package edu.ualberta.med.biobank.forms;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.PojoObservables;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.Section;
import org.springframework.remoting.RemoteAccessException;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.forms.input.FormInput;
import edu.ualberta.med.biobank.model.Address;
import edu.ualberta.med.biobank.model.Clinic;
import edu.ualberta.med.biobank.treeview.ClinicAdapter;
import edu.ualberta.med.biobank.treeview.SiteAdapter;
import edu.ualberta.med.biobank.validators.NonEmptyString;
import gov.nih.nci.system.query.SDKQuery;
import gov.nih.nci.system.query.SDKQueryResult;
import gov.nih.nci.system.query.example.InsertExampleQuery;
import gov.nih.nci.system.query.example.UpdateExampleQuery;

public class ClinicEntryForm extends AddressEntryForm {	
	public static final String ID =
	      "edu.ualberta.med.biobank.forms.ClinicEntryForm";
	
	private static final String NEW_CLINIC_OK_MESSAGE = "New clinic information.";
	private static final String CLINIC_OK_MESSAGE = "Clinic information.";
	private static final String NO_CLINIC_NAME_MESSAGE = "Clinic must have a name";
	
	private ClinicAdapter clinicAdapter;
	private Clinic clinic;
	
	protected Combo session;
	private Text name;	
	private ControlDecoration nameDecorator;
	private Button submit;

	public void init(IEditorSite editorSite, IEditorInput input) throws PartInitException {
		super.init(editorSite, input);
		if ( !(input instanceof FormInput)) 
			throw new PartInitException("Invalid editor input");
		
		FormInput clinicInput = (FormInput) input;
		
		clinicAdapter = (ClinicAdapter) clinicInput.getNode();
		clinic = clinicAdapter.getClinic();		
		setAppService(clinicAdapter.getAppService());
		
		address = clinic.getAddress();
		if (address == null) {
		    address = new Address();
			clinic.setAddress(address);
		}
		
		if (clinic.getId() == null) {
			setPartName("New Clinic");
		}
		else {
			setPartName("Clinic " + clinic.getName());
		}
	}
	
	private String getOkMessage() {
		if (clinic.getId() == null) {
			return NEW_CLINIC_OK_MESSAGE;
		}
		return CLINIC_OK_MESSAGE;
	}

	protected void createFormContent() {			
		form.setText("Clinic Information");
		
		GridLayout layout = new GridLayout(1, false);
		form.getBody().setLayout(layout);
		
		toolkit.createLabel(form.getBody(), 
				"Clinics can be associated with studies after submitting this initial information.", 
				SWT.LEFT);
		
		Section section = toolkit.createSection(form.getBody(), 
				ExpandableComposite.TITLE_BAR
				| ExpandableComposite.EXPANDED);
		section.setText("Clinic");
		Composite client = toolkit.createComposite(section);
		section.setClient(client);
		section.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		layout = new GridLayout(2, false);
		layout.horizontalSpacing = 10;
		client.setLayout(layout);
		toolkit.paintBordersFor(client);		

        Label label = toolkit.createLabel(client, "Name:", SWT.LEFT);
        label.setLayoutData(new GridData());
        name = toolkit.createText(client, "", SWT.SINGLE);
        name.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        name.addKeyListener(keyListener);
		nameDecorator = FormUtils.createDecorator(label, NO_CLINIC_NAME_MESSAGE);
		
		createAddressArea();

		section = toolkit.createSection(form.getBody(), SWT.NONE);
		client = toolkit.createComposite(section);
		section.setClient(client);
		section.setLayoutData(new GridData(GridData.FILL_BOTH));
		layout = new GridLayout(2, false);
		client.setLayout(layout);
		toolkit.paintBordersFor(client);

		submit = toolkit.createButton(client, "Submit", SWT.PUSH);
		submit.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				PlatformUI.getWorkbench().getActiveWorkbenchWindow()
				.getActivePage().saveEditor(ClinicEntryForm.this, false);
			}
		});		
		
		bindValues();
		
		// When adding help uncomment line below
		// PlatformUI.getWorkbench().getHelpSystem().setHelp(composite, IJavaHelpContextIds.XXXXX);
	}
    
    private void bindValues() {
    	DataBindingContext dbc = new DataBindingContext();

    	dbc.bindValue(SWTObservables.observeText(name, SWT.Modify),
    			PojoObservables.observeValue(clinic, "name"), 
    			new UpdateValueStrategy().setAfterConvertValidator(
    					new NonEmptyString(NO_CLINIC_NAME_MESSAGE, nameDecorator)), 
    					null);
    	
    	super.bindValues(dbc);
    }
    
    protected void handleStatusChanged(IStatus status) {
		if (status.getSeverity() == IStatus.OK) {
			form.setMessage(getOkMessage(), IMessageProvider.NONE);
	    	submit.setEnabled(true);
		}
		else {
			form.setMessage(status.getMessage(), IMessageProvider.ERROR);
	    	submit.setEnabled(false);
		}		
    }

	@Override
	public void setFocus() {
		form.setFocus();
	}
	
	public void saveForm() {
		clinic.setAddress(address);
		SiteAdapter siteAdapter = (SiteAdapter) clinicAdapter.getParent().getParent();
		clinic.setSite(siteAdapter.getSite());
		
        try {
            SDKQuery query;
            SDKQueryResult result;

            if ((clinic.getId() == null) || (clinic.getId() == 0)) {
                Assert.isTrue(clinic.getAddress().getId() == null, 
                        "insert invoked on address already in database");
                
                query = new InsertExampleQuery(address);                    
                result = appService.executeQuery(query);
                clinic.setAddress((Address) result.getObjectResult());
                query = new InsertExampleQuery(clinic); 
            }
            else { 
                Assert.isNotNull(clinic.getAddress().getId(), 
                        "update invoked on address not in database");

                query = new UpdateExampleQuery(clinic.getAddress());                    
                result = appService.executeQuery(query);
                clinic.setAddress((Address) result.getObjectResult());
                query = new UpdateExampleQuery(clinic); 
            }
            
            result = appService.executeQuery(query);
            clinic = (Clinic) result.getObjectResult();
        }
        catch (final RemoteAccessException exp) {
            Display.getDefault().asyncExec(new Runnable() {
                public void run() {
                    MessageDialog.openError(
                            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), 
                            "Connection Attempt Failed", 
                            "Could not perform database operation. Make sure server is running correct version.");
                }
            });
        }
        catch (Exception exp) {
            exp.printStackTrace();
        }
		
		SessionManager.getInstance().updateClinics(clinicAdapter.getParent());		
		getSite().getPage().closeEditor(ClinicEntryForm.this, false);
	}
}
