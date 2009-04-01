package edu.ualberta.med.biobank.forms;

import org.eclipse.core.databinding.beans.PojoObservables;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.springframework.remoting.RemoteAccessException;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.forms.input.FormInput;
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

public class SiteEntryForm extends AddressEntryFormCommon {	
	public static final String ID =
	      "edu.ualberta.med.biobank.forms.SiteEntryForm";
	
	private static final String NEW_SITE_OK_MESSAGE = "Create a new BioBank site.";
	private static final String SITE_OK_MESSAGE = "Edit a BioBank site.";
	private static final String NO_SITE_NAME_MESSAGE = "Site must have a name";
	
	private SiteAdapter siteAdapter;
	
	private Site site;
	
	protected Combo session;
	private Button submit;
	
	public void init(IEditorSite editorSite, IEditorInput input) throws PartInitException {
		super.init(editorSite, input);
		if ( !(input instanceof FormInput)) 
			throw new PartInitException("Invalid editor input");
		
		Node node = ((FormInput) input).getNode();
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
		form.getBody().setLayout(new GridLayout(1, false));
		createSiteSection();
        createAddressArea();
        createButtonsSection();
        
        // When adding help uncomment line below
        // PlatformUI.getWorkbench().getHelpSystem().setHelp(composite, IJavaHelpContextIds.XXXXX);
	}
	
	private void createSiteSection() {		
		toolkit.createLabel(form.getBody(), 
				"Studies, Clinics, and Storage Types can be added after submitting this information.", 
				SWT.LEFT);
		
		Composite client = toolkit.createComposite(form.getBody());
		GridLayout layout = new GridLayout(2, false);
		layout.horizontalSpacing = 10;
		client.setLayout(layout);
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		toolkit.paintBordersFor(client);
		
		createSessionSelectionWidget(client);	  

        createBoundWidget(client, Text.class, "Name", null,
            PojoObservables.observeValue(site, "name"),
            NonEmptyString.class, NO_SITE_NAME_MESSAGE);      

        createBoundWidget(client, Combo.class, "Activity Status", 
            FormConstants.ACTIVITY_STATUS,
            PojoObservables.observeValue(site, "activityStatus"),
            null, null);  

        Text comment = (Text) createBoundWidget(client, Text.class, "Comments", 
            null, PojoObservables.observeValue(site, "comment"), null, null);
        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.heightHint = 40;
        comment.setLayoutData(gd);
	}
	
	private void createButtonsSection() {
		Composite client = toolkit.createComposite(form.getBody());
		GridLayout layout = new GridLayout();
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
