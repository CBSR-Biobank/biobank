package edu.ualberta.med.biobank.forms;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.PojoObservables;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

import edu.ualberta.med.biobank.model.Address;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.SiteInput;
import edu.ualberta.med.biobank.model.SiteNode;


public class SiteViewForm extends AddressViewForm {	
	public static final String ID =
	      "edu.ualberta.med.biobank.forms.SiteViewForm";
	
	private Site site;
	
	Label name;

	public void doSave(IProgressMonitor monitor) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doSaveAs() {
		// TODO Auto-generated method stub
		
	}

	public void init(IEditorSite editorSite, IEditorInput input)
			throws PartInitException {
		super.init(editorSite, input);
		if ( !(input instanceof SiteInput)) 
			throw new PartInitException("Invalid editor input"); 
		
		SiteNode node = (SiteNode) ((SiteInput) input).getAdapter(SiteNode.class);

		site = node.getSite();
		address = site.getAddress();
		setPartName("Site " + site.getName());
	}

	@Override
	public void createPartControl(Composite parent) {
		
		toolkit = new FormToolkit(parent.getDisplay());
		form = toolkit.createForm(parent);	
		
		form.setText("BioBank Site Information");
		toolkit.decorateFormHeading(form);
		//form.setMessage(OK_MESSAGE);
		
		GridLayout layout = new GridLayout(1, false);
		layout.marginHeight = 10;
		layout.marginWidth = 6;
		//layout.horizontalSpacing = 20;
		form.getBody().setLayout(layout);
		//form.getBody().setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		Section section = toolkit.createSection(form.getBody(), Section.TITLE_BAR);
		section.setText("Site");
		section.setLayout(layout);
		section.setLayoutData(new GridData(GridData.FILL_BOTH));
		Composite sbody = toolkit.createComposite(section);
		section.setClient(sbody);
		layout = new GridLayout(2, false);
		layout.horizontalSpacing = 10;
		sbody.setLayout(layout);
		toolkit.paintBordersFor(sbody);
		form.getBody().setLayout(layout);
		
		name = createLabelledField(sbody, "Name :", 100, null);
		
		createAddressArea();
		bindValues();
	}
    
    private void bindValues() {
    	DataBindingContext dbc = new DataBindingContext();
    	
		dbc.bindValue(SWTObservables.observeText(name),
				PojoObservables.observeValue(site, "name"), null, null);
    	
    	super.bindValues(dbc);
    }

	@Override
	public boolean isDirty() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isSaveAsAllowed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void handleStatusChanged() {
		// TODO Auto-generated method stub
		
	}
}
