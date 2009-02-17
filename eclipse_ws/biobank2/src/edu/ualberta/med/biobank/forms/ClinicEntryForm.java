package edu.ualberta.med.biobank.forms;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.PojoObservables;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.springframework.util.Assert;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.helpers.ClinicSaveHelper;
import edu.ualberta.med.biobank.model.Clinic;
import edu.ualberta.med.biobank.treeview.Node;
import edu.ualberta.med.biobank.treeview.ClinicAdapter;
import edu.ualberta.med.biobank.treeview.SiteAdapter;
import edu.ualberta.med.biobank.validators.NonEmptyString;

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
		if ( !(input instanceof NodeInput)) 
			throw new PartInitException("Invalid editor input");
		
		Node node = ((NodeInput) input).getNode();
		Assert.notNull(node, "Null editor input");

		Assert.isTrue((node instanceof ClinicAdapter), 
				"Invalid editor input: object of type "
				+ node.getClass().getName());

		clinicAdapter = (ClinicAdapter) node;
		clinic = clinicAdapter.getClinic();
		SiteAdapter siteNode = (SiteAdapter) clinicAdapter.getParent().getParent();
		clinic.setSite(siteNode.getSite());
		address = clinic.getAddress();
		
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

	public void createPartControl(Composite parent) {		
		toolkit = new FormToolkit(parent.getDisplay());
		form = toolkit.createForm(parent);	
		
		form.setText("Clinic Information");
		toolkit.decorateFormHeading(form);
		form.setMessage(getOkMessage());
		
		GridLayout layout = new GridLayout(1, false);
		form.getBody().setLayout(layout);
		
		toolkit.createLabel(form.getBody(), 
				"Clinics can be associated with studies after submitting this initial information.", 
				SWT.LEFT);
		
		Section section = toolkit.createSection(form.getBody(), 
				ExpandableComposite.TITLE_BAR
				| ExpandableComposite.EXPANDED);
		section.setText("Site");
		section.setFont(FormUtils.getSectionFont());
		Composite sbody = toolkit.createComposite(section);
		section.setClient(sbody);
		section.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		layout = new GridLayout(2, false);
		layout.horizontalSpacing = 10;
		sbody.setLayout(layout);
		toolkit.paintBordersFor(sbody);
		
		name = FormUtils.createLabelledText(toolkit, sbody, "Name:", 100, null);
		nameDecorator = FormUtils.createDecorator(name, NO_CLINIC_NAME_MESSAGE);
		
		createAddressArea();

		section = toolkit.createSection(form.getBody(), SWT.NONE);
		sbody = toolkit.createComposite(section);
		section.setClient(sbody);
		section.setLayoutData(new GridData(GridData.FILL_BOTH));
		layout = new GridLayout();
		layout.horizontalSpacing = 10;
		layout.numColumns = 2;
		sbody.setLayout(layout);
		toolkit.paintBordersFor(sbody);

		submit = toolkit.createButton(sbody, "Submit", SWT.PUSH);
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
			form.setMessage(getOkMessage());
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

	@Override
	public void doSave(IProgressMonitor monitor) {
		super.doSave(monitor);
		saveSettings();
	}
	
	public void saveSettings() {
		clinic.setAddress(address);
		
		ClinicSaveHelper helper = new ClinicSaveHelper(
				clinicAdapter.getAppService(), clinic);
		
		BusyIndicator.showWhile(
				PlatformUI.getWorkbench().getActiveWorkbenchWindow()
				.getShell().getDisplay(), helper);
		
		final SiteAdapter siteAdapter = 
			(SiteAdapter) clinicAdapter.getParent().getParent();
		BusyIndicator.showWhile(
				PlatformUI.getWorkbench().getActiveWorkbenchWindow()
				.getShell().getDisplay(), new Runnable() {
					public void run() {
						siteAdapter.getSite().getClinicCollection();
					}
				});
		
		SessionManager.getInstance().updateClinics(clinicAdapter.getParent());
		
		getSite().getPage().closeEditor(ClinicEntryForm.this, false);
	}
}
