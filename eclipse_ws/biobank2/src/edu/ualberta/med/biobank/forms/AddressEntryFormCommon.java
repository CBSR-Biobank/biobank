package edu.ualberta.med.biobank.forms;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.widgets.Section;

import edu.ualberta.med.biobank.model.Address;

public abstract class AddressEntryFormCommon extends BiobankEditForm {
    
	protected Address address;

	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
        super.init(site, input);
	}

	protected void createAddressArea() {
		Section section = toolkit.createSection(form.getBody(), Section.TITLE_BAR);
		section.setText("Address");
		//section.setFont(FormUtils.getSectionFont());
		section.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		Composite client = toolkit.createComposite(section);
		section.setClient(client);
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 10;
		client.setLayout(layout);
		toolkit.paintBordersFor(client);

        createWidgetsFromHashMap(FormConstants.ADDRESS_FIELDS, 
                FormConstants.ADDRESS_ORDERED_FIELDS, address, client);
        
        Combo combo = (Combo) controls.get("province");
        Assert.isNotNull(combo, "could not find province combo for address");
	}
    
    protected abstract void handleStatusChanged(IStatus severity);
}
