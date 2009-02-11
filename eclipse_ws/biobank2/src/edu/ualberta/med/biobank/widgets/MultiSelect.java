package edu.ualberta.med.biobank.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.ui.forms.widgets.FormToolkit;

import edu.ualberta.med.biobank.forms.FormUtils;

public class MultiSelect extends Composite {
	private List selList;
	
	private List availList;

	public MultiSelect(Composite parent, int style, int minHeight) {
		super(parent, style);
		
		setLayout(new GridLayout(3, false));
		setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		selList = new List(this, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.heightHint = minHeight;
		gd.widthHint = 200;
		selList.setLayoutData(gd);
		
		Composite buttons = new Composite(this, SWT.NONE);
		buttons.setLayout(new GridLayout(1, true));
		gd = new GridData();
		gd.horizontalAlignment = GridData.FILL;
		buttons.setLayoutData(gd);
		
		Button addButton = new Button(buttons, SWT.PUSH);
		addButton.setText("<< Add");
		addButton.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false));
		
		Button removeButton = new Button(buttons, SWT.PUSH);
		removeButton.setText("Remove >>");
		removeButton.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false));
		
		availList = new List(this, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.heightHint = minHeight;
		gd.widthHint = 200;
		availList.setLayoutData(gd);
		
		Label label = new Label(this, SWT.NONE);
		label.setText("Selected Clinics");
		label.setFont(FormUtils.getSectionFont());
		gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.horizontalSpan = 2;
		label.setLayoutData(gd);
		label = new Label(this, SWT.NONE);
		label.setText("Available Clinics");
		label.setFont(FormUtils.getSectionFont());
	}

	public void adaptToToolkit(FormToolkit toolkit) {
		adaptAllChildren(this, toolkit);
	}
	
	private void adaptAllChildren(Composite container, FormToolkit toolkit) {
		Control[] children = container.getChildren();
		for (Control aChild : children) {
			toolkit.adapt(aChild, true, true);
			if (aChild instanceof Composite) {
				adaptAllChildren((Composite) aChild, toolkit);
			}
		}
	}
	
	public void addAvailable(String[] available) {
		for (String item : available) {
			availList.add(item);
		}
	}
	
	public void addAvailable(String item) {
		availList.add(item);
	}
}
