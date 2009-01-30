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
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.widgets.FormToolkit;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.model.Address;
import edu.ualberta.med.biobank.model.SessionNode;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.SiteInput;
import edu.ualberta.med.biobank.model.SiteNode;
import edu.ualberta.med.biobank.validators.NonEmptyString;

public class SiteForm extends AddressForm {	
	public static final String ID =
	      "edu.ualberta.med.biobank.forms.SiteDialog";
	
	private static final String OK_MESSAGE = "Creates a new BioBank site.";
	private static final String NO_SITE_NAME_MESSAGE = "Site must have a name";
	
	private Site site;
	
	private String[] sessionNames;

	protected Combo session;
	private Text name;	
	private ControlDecoration nameDecorator;
	private boolean editMode = false;

	public void init(IEditorSite editorSite, IEditorInput input) throws PartInitException {
		super.init(editorSite, input);
		if ( !(input instanceof SiteInput)) 
			throw new PartInitException("Invalid editor input"); 
		
		SiteInput siteInput = (SiteInput) input;
		SessionNode sessionNode = (SessionNode) input.getAdapter(SessionNode.class);
		SiteNode siteNode = (SiteNode) input.getAdapter(SiteNode.class);
		
		if ((sessionNode == null) && (siteNode == null)) 
			throw new PartInitException("Invalid editor input adapter");
		if (siteNode != null) {
			site = siteNode.getSite();
			address = site.getAddress();
			editMode = true;
			setPartName("Site " + site.getName());
		}
		else {
			site = new Site();
			address = new Address();
			setPartName("New Site");
		}
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
		
		Composite contents = form.getBody();
		
		contents.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
				
		Group group = new Group(contents, SWT.SHADOW_NONE);
		group.setText("Site");
		group.setLayout(new GridLayout(2, false));
		group.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		
//		if (sessionNames.length > 1) {			
//			Label label = new Label(group, SWT.LEFT);
//			label.setText("Session:");
//			
//			session = new Combo(group, SWT.READ_ONLY);
//			session.setItems(sessionNames);
//			session.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
//		}
//		else {
//			session = null;
//		}
		
		name = createLabelledText(group, "Name:", 100, null);
		nameDecorator = createDecorator(name, NO_SITE_NAME_MESSAGE);
		
		createAddressArea(contents);
		
		bindValues();

		GridLayoutFactory.swtDefaults().applyTo(contents);
		
		// When adding help uncomment line below
		// PlatformUI.getWorkbench().getHelpSystem().setHelp(composite, IJavaHelpContextIds.XXXXX);
		
		toolkit.paintBordersFor(form.getBody());
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
		//okButton.setEnabled(severity == IStatus.OK);
		if (severity == IStatus.OK) {
			form.setMessage(OK_MESSAGE);
		}
		else {
			form.setMessage(currentStatus.getMessage(), IMessageProvider.ERROR);
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
		//super.okPressed();
	}

	@Override
	public void setFocus() {
		form.setFocus();
	}
}
