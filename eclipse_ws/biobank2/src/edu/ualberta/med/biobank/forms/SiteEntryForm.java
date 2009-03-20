package edu.ualberta.med.biobank.forms;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.PojoObservables;
import org.eclipse.core.runtime.Assert;
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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.helpers.GetHelper;
import edu.ualberta.med.biobank.helpers.SiteGetHelper;
import edu.ualberta.med.biobank.helpers.SiteSaveHelper;
import edu.ualberta.med.biobank.model.Address;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.treeview.Node;
import edu.ualberta.med.biobank.treeview.SessionAdapter;
import edu.ualberta.med.biobank.treeview.SiteAdapter;
import edu.ualberta.med.biobank.validators.NonEmptyString;

public class SiteEntryForm extends AddressEntryForm {	
	public static final String ID =
	      "edu.ualberta.med.biobank.forms.SiteEntryForm";
	
	private static final String NEW_SITE_OK_MESSAGE = "Create a new BioBank site.";
	private static final String SITE_OK_MESSAGE = "Edit a BioBank site.";
	private static final String NO_SITE_NAME_MESSAGE = "Site must have a name";
	
	private SiteAdapter siteAdapter;
	
	private Site site;
	
	protected Combo session;
	private Text name;	
	private ControlDecoration nameDecorator;
	private Button submit;

	public void init(IEditorSite editorSite, IEditorInput input) throws PartInitException {
		super.init(editorSite, input);
		if ( !(input instanceof NodeInput)) 
			throw new PartInitException("Invalid editor input");
		
		Node node = ((NodeInput) input).getNode();
		Assert.isNotNull(node, "Null editor input");
		
		Assert.isTrue((node instanceof SiteAdapter), 
				"Invalid editor input: object of type "
				+ node.getClass().getName());
		
		siteAdapter = (SiteAdapter) node;
		site = siteAdapter.getSite();	
		
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
	
    // We don't want to modify the Site object we already have in memory.
    // Therefore, we need to get a new one from the ORM
	private void loadSite() {
        if ((site.getId() == null) || (site.getId() == 0)) {
            site = new Site();
            site.setAddress(new Address());
            return;
        }
        
        SiteGetHelper helper = new SiteGetHelper(
            siteAdapter.getAppService(), site.getId(), 0);

        BusyIndicator.showWhile(
            PlatformUI.getWorkbench().getActiveWorkbenchWindow()
            .getShell().getDisplay(), helper);

        site = helper.getResult();
	}

	public void createPartControl(Composite parent) {
	    loadSite();
        address = site.getAddress();    
        
		toolkit = new FormToolkit(parent.getDisplay());
		form = toolkit.createForm(parent);	
		
		form.setText("BioBank Site Information");
		toolkit.decorateFormHeading(form);
		form.setMessage(getOkMessage());
		
		GridLayout layout = new GridLayout(1, false);
		form.getBody().setLayout(layout);
		
		toolkit.createLabel(form.getBody(), 
				"Studies, Clinics, and Storage Types can be added after submitting this information.", 
				SWT.LEFT);
		
		Section section = toolkit.createSection(form.getBody(), 
				ExpandableComposite.TITLE_BAR
				| ExpandableComposite.EXPANDED);
		section.setText("Site");
		//section.setFont(FormUtils.getSectionFont());
		Composite sbody = toolkit.createComposite(section);
		section.setClient(sbody);
		section.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		layout = new GridLayout(2, false);
		layout.horizontalSpacing = 10;
		sbody.setLayout(layout);
		toolkit.paintBordersFor(sbody);
		
		String[] sessionNames = SessionManager.getInstance().getSessionNames();
		
		if (sessionNames.length > 1) {			
			toolkit.createLabel(sbody, "Session:", SWT.LEFT);
			session = new Combo(sbody, SWT.READ_ONLY);
			session.setItems(sessionNames);
			session.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		}
		else {
			session = null;
		}

        Label label = toolkit.createLabel(sbody, "Name:", SWT.LEFT);
        name  = toolkit.createText(sbody, "", SWT.SINGLE);
        name.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        name.addKeyListener(keyListener);		
		
		nameDecorator = FormUtils.createDecorator(label, NO_SITE_NAME_MESSAGE);
		
		createAddressArea();

		sbody = toolkit.createComposite(form.getBody());
		layout = new GridLayout();
		layout.horizontalSpacing = 10;
		layout.numColumns = 2;
		sbody.setLayout(layout);
		toolkit.paintBordersFor(sbody);

		submit = toolkit.createButton(sbody, "Submit", SWT.PUSH);
		submit.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				PlatformUI.getWorkbench().getActiveWorkbenchWindow()
					.getActivePage().saveEditor(SiteEntryForm.this, false);
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
		
		if (siteAdapter.getParent() == null) {
			siteAdapter.setParent(SessionManager.getInstance().getSessionSingle());
		}
		
		SiteSaveHelper helper = new SiteSaveHelper(
				siteAdapter.getAppService(), site);
		BusyIndicator.showWhile(
				PlatformUI.getWorkbench().getActiveWorkbenchWindow()
				.getShell().getDisplay(), helper);
		
		GetHelper<Site> sitesHelper = new GetHelper<Site> (
				siteAdapter.getAppService(), Site.class);
		BusyIndicator.showWhile(
				PlatformUI.getWorkbench().getActiveWorkbenchWindow()
				.getShell().getDisplay(), sitesHelper);
		
		siteAdapter.setSite(site);
		
		SessionAdapter sessionNode = (SessionAdapter) siteAdapter.getParent();
		for (Site site : sitesHelper.getResult()) {
			SiteAdapter adapter = new SiteAdapter(sessionNode, site);
			sessionNode.addChild(adapter);
		}
		
		getSite().getPage().closeEditor(SiteEntryForm.this, false);    	
    }

	@Override
	public void setFocus() {
		form.setFocus();
	}
}
