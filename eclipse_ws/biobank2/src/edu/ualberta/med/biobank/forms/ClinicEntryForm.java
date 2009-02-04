package edu.ualberta.med.biobank.forms;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.PojoObservables;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
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
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.model.Address;
import edu.ualberta.med.biobank.model.Clinic;
import edu.ualberta.med.biobank.model.SiteInput;
import edu.ualberta.med.biobank.validators.NonEmptyString;

public class ClinicEntryForm extends AddressEntryForm {	
	public static final String ID =
	      "edu.ualberta.med.biobank.forms.SiteEntryForm";
	
	private static final String OK_MESSAGE = "Creates a new BioBank site.";
	private static final String NO_SITE_NAME_MESSAGE = "Site must have a name";
	
	private Clinic clinic;
	
	protected Combo session;
	private Text name;	
	private ControlDecoration nameDecorator;

	public void init(IEditorSite editorSite, IEditorInput input) throws PartInitException {
		super.init(editorSite, input);
		if ( !(input instanceof SiteInput)) 
			throw new PartInitException("Invalid editor input"); 

		clinic = new Clinic();
		address = new Address();
		setPartName("New Clinic");
	}

	public void createPartControl(Composite parent) {
		KeyListener keyListener = new KeyListener() {
			@Override
			public void keyPressed(KeyEvent e) {
				if ((e.keyCode & SWT.MODIFIER_MASK) == 0) {
					setDirty(true);
				}
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
				// nothing
			}
		};
		
		toolkit = new FormToolkit(parent.getDisplay());
		form = toolkit.createForm(parent);	
		
		form.setText("BioBank Site Information");
		toolkit.decorateFormHeading(form);
		form.setMessage(OK_MESSAGE);
		
		GridLayout layout = new GridLayout(1, false);
		//layout.marginHeight = 10;
		//layout.marginWidth = 6;
		//layout.horizontalSpacing = 20;
		form.getBody().setLayout(layout);
		
		toolkit.createLabel(form.getBody(), 
				"Studies, Clinics, and Storage Types can be added after submitting this initial information.", 
				SWT.LEFT);

		
		Section section = toolkit.createSection(form.getBody(), 
				ExpandableComposite.TITLE_BAR
				| ExpandableComposite.EXPANDED);
		section.setText("Site");
		Composite sbody = toolkit.createComposite(section);
		section.setClient(sbody);
		section.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		layout = new GridLayout(2, false);
		layout.horizontalSpacing = 10;
		sbody.setLayout(layout);
		toolkit.paintBordersFor(sbody);
		
		String[] sessionNames = BioBankPlugin.getDefault().getSessionNames();
		
		if (sessionNames.length > 1) {			
			toolkit.createLabel(sbody, "Session:", SWT.LEFT);
			session = new Combo(sbody, SWT.READ_ONLY);
			session.setItems(sessionNames);
			session.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		}
		else {
			session = null;
		}
		
		name = createLabelledText(sbody, "Name:", 100, null);
		nameDecorator = createDecorator(name, NO_SITE_NAME_MESSAGE);
		
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

		final Button submit = toolkit.createButton(sbody, "Submit", SWT.PUSH);
		submit.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				clinic.setAddress(address);
				String sessionName;
				if (session == null) {
					String[] sessionNames = BioBankPlugin.getDefault().getSessionNames();
					sessionName = sessionNames[0];
				}
				else {
					sessionName = session.getText();
				}
				
				try {
					BioBankPlugin.getDefault().createObject(sessionName, clinic);
				}
				catch (Exception exp) {
					exp.printStackTrace();
				}
				getSite().getPage().closeEditor(ClinicEntryForm.this, false);
			}
		});

		final Button cancel = toolkit.createButton(sbody, "Cancel", SWT.PUSH);
		cancel.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				getSite().getPage().closeEditor(ClinicEntryForm.this, false);
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
    					new NonEmptyString(NO_SITE_NAME_MESSAGE, nameDecorator)), 
    					null);
    	
    	super.bindValues(dbc);
    }
    
    protected void handleStatusChanged() {
    	int severity = currentStatus.getSeverity(); 
		//okButton.setEnabled(severity == IStatus.OK);
		if (severity == IStatus.OK) {
			form.setMessage(OK_MESSAGE);
		}
		else {
			form.setMessage(currentStatus.getMessage(), IMessageProvider.ERROR);
		}		
    }

	@Override
	public void setFocus() {
		form.setFocus();
	}
}
