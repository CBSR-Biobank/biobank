package edu.ualberta.med.biobank.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.List;
import org.eclipse.ui.forms.widgets.FormToolkit;

import edu.ualberta.med.biobank.model.SdataType;

public class SdataWidget extends Composite {
    Button checkButton;
    Button addButton;
    Button removeButton;

    public SdataWidget(Composite parent, int style, SdataType sdataType) {
        super(parent, style);
        
        setLayout(new GridLayout(1, false));
        setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        
        String type = sdataType.getType();
        if (type.equals("Aliquot Volume") || type.equals("Blood Received") 
                || type.equals("Visit")) {
            
            checkButton = new Button(this, SWT.CHECK);
            checkButton.setText(type);
            
            // this composite holds the list and the "Add" and "Remove" buttons
            Composite comp = new Composite(this, SWT.NONE);
            comp.setLayout(new GridLayout(2, false));
            comp.setLayoutData(new GridData(GridData.FILL_BOTH));
            
            List list = new List(comp, SWT.BORDER | SWT.V_SCROLL);
            list.setLayoutData(new GridData(GridData.FILL_BOTH));
            
            // this composite holds the "Add" and "Remove" buttons
            comp = new Composite(comp, SWT.NONE);
            comp.setLayout(new GridLayout(1, false));
            comp.setLayoutData(new GridData());
            
            addButton = new Button(comp, SWT.PUSH);
            addButton.setText("Add");
            addButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

            addButton.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(SelectionEvent e) {
                }
            });
            
            removeButton = new Button(comp, SWT.PUSH);
            removeButton.setText("Remove");
            removeButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

            removeButton.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(SelectionEvent e) {
                }
            });
        }
        else {
            checkButton = new Button(this, SWT.CHECK);
            checkButton.setText(type);
            GridData gd = new GridData(GridData.VERTICAL_ALIGN_BEGINNING | GridData.GRAB_HORIZONTAL);
            checkButton.setLayoutData(gd);
        }
    }

    public void adaptToToolkit(FormToolkit toolkit) {
        toolkit.adapt(this, true, true);
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

}
