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
import org.springframework.util.Assert;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.SiteNode;
import edu.ualberta.med.biobank.model.WsObject;
import edu.ualberta.med.biobank.validators.NonEmptyString;

public class SiteEntryForm extends AddressEntryForm {	
	public static final String ID =
	      "edu.ualberta.med.biobank.forms.SiteEntryForm";
	
	private static final String NEW_SITE_OK_MESSAGE = "Create a new BioBank site.";
	private static final String SITE_OK_MESSAGE = "Edit a BioBank site.";
	private static final String NO_SITE_NAME_MESSAGE = "Site must have a name";
	
	private WsObject node;
	
	private Site site;
	
	protected Combo session;
	private Text name;	
	private ControlDecoration nameDecorator;
	private Button submit;

	public void init(IEditorSite editorSite, IEditorInput input) throws PartInitException {
		super.init(editorSite, input);
		if ( !(input instanceof WsObjectInput)) 
			throw new PartInitException("Invalid editor input");
		
		node = ((WsObjectInput) input).getWsObject();
		Assert.notNull(node, "Null editor input");
		
		Assert.isTrue((node instanceof SiteNode), 
				"Invalid editor input: object of type "
				+ node.getClass().getName());
		
		SiteNode siteNode = (SiteNode) node;
		site = siteNode.getSite();
		address = site.getAddress();
		
		if (site.getId() == null) {
			setPartName("New Site");
		}
		else {
			setPartName("Site " + site.getName());
		}
	}
	
	private String getOkMessage() {
		if (site.getId() == null) {
			return NEW_SITE_OK_MESSAGE;
		}
		return SITE_OK_MESSAGE;
	}

	public void createPartControl(Composite parent) {		
		toolkit = new FormToolkit(parent.getDisplay());
		form = toolkit.createForm(parent);	
		
		form.setText("BioBank Site Information");
		toolkit.decorateFormHeading(form);
		form.setMessage(getOkMessage());
		
		GridLayout layout = new GridLayout(1, false);
		//layout.marginHeight = 10;
		//layout.marginWidth = 6;
		//layout.horizontalSpacing = 20;
		form.getBody().setLayout(layout);
		
		toolkit.createLabel(form.getBody(), 
				"Studies, Clinics, and Storage Types can be added after submitting this information.", 
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
		name.addKeyListener(keyListener);
		
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
				saveSettings();
			}
		});
		
		bindValues();
		
		// When adding help uncomment line below
		// PlatformUI.getWorkbench().getHelpSystem().setHelp(composite, IJavaHelpContextIds.XXXXX);
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
	public void doSave(IProgressMonitor monitor) {
		super.doSave(monitor);
		saveSettings();
	}
    
    private void saveSettings() {
		site.setAddress(address);
		String sessionName;
		
		if (node instanceof SiteNode) {
			sessionName = node.getParent().getName();
		}
		else {
			sessionName = node.getName();
		}
		
		try {
			if (site.getId() == null) {
				BioBankPlugin.getDefault().createObject(sessionName, site);
			}
			else {
				BioBankPlugin.getDefault().updateObject(sessionName, site);
			}
		}
		catch (Exception exp) {
			exp.printStackTrace();
		}
		getSite().getPage().closeEditor(SiteEntryForm.this, false);    	
    }

	@Override
	public void setFocus() {
		form.setFocus();
	}
}
