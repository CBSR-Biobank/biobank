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
import edu.ualberta.med.biobank.model.Address;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.treeview.Node;
import edu.ualberta.med.biobank.treeview.SessionAdapter;
import edu.ualberta.med.biobank.treeview.SiteAdapter;
import edu.ualberta.med.biobank.validators.NonEmptyString;
import gov.nih.nci.system.query.SDKQuery;
import gov.nih.nci.system.query.SDKQueryResult;
import gov.nih.nci.system.query.example.InsertExampleQuery;
import gov.nih.nci.system.query.example.UpdateExampleQuery;

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

	protected void createFormContent() {
        address = site.getAddress();   
		
		form.setText("BioBank Site Information");
		
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
		Composite client = toolkit.createComposite(section);
		section.setClient(client);
		section.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		layout = new GridLayout(2, false);
		layout.horizontalSpacing = 10;
		client.setLayout(layout);
		toolkit.paintBordersFor(client);
		
		String[] sessionNames = SessionManager.getInstance().getSessionNames();
		
		if (sessionNames.length > 1) {			
			toolkit.createLabel(client, "Session:", SWT.LEFT);
			session = new Combo(client, SWT.READ_ONLY);
			session.setItems(sessionNames);
			session.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		}
		else {
			session = null;
		}

        Label label = toolkit.createLabel(client, "Name:", SWT.LEFT);
        name  = toolkit.createText(client, "", SWT.SINGLE);
        name.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        name.addKeyListener(keyListener);		
		
		nameDecorator = FormUtils.createDecorator(label, NO_SITE_NAME_MESSAGE);
		
		createAddressArea();

		client = toolkit.createComposite(form.getBody());
		layout = new GridLayout();
		layout.horizontalSpacing = 10;
		layout.numColumns = 2;
		client.setLayout(layout);
		toolkit.paintBordersFor(client);

		submit = toolkit.createButton(client, "Submit", SWT.PUSH);
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
			form.setMessage(getOkMessage(), IMessageProvider.NONE);
	    	submit.setEnabled(true);
		}
		else {
			form.setMessage(status.getMessage(), IMessageProvider.ERROR);
	    	submit.setEnabled(false);
		}		
    }
    
    protected void saveForm() {
		site.setAddress(address);
		
		if (siteAdapter.getParent() == null) {
			siteAdapter.setParent(SessionManager.getInstance().getSessionSingle());
		}
		  
        try {
            SDKQuery query;
            SDKQueryResult result;

            if ((site.getId() == null) || (site.getId() == 0)) {
                Assert.isTrue(site.getAddress().getId() == null, "insert invoked on address already in database");
                
                query = new InsertExampleQuery(site.getAddress());                  
                result = siteAdapter.getAppService().executeQuery(query);
                site.setAddress((Address) result.getObjectResult());
                query = new InsertExampleQuery(site);   
            }
            else { 
                Assert.isNotNull(site.getAddress().getId(), "update invoked on address not in database");

                query = new UpdateExampleQuery(site.getAddress());                  
                result = siteAdapter.getAppService().executeQuery(query);
                site.setAddress((Address) result.getObjectResult());
                query = new UpdateExampleQuery(site);   
            }
            
            result = siteAdapter.getAppService().executeQuery(query);
            site = (Site) result.getObjectResult();
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
		
		SessionManager.getInstance().updateSites(
		        (SessionAdapter) siteAdapter.getParent());		
		getSite().getPage().closeEditor(SiteEntryForm.this, false);    	
    }

	@Override
	public void setFocus() {
		form.setFocus();
	}
}
