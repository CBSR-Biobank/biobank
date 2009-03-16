package edu.ualberta.med.biobank.widgets;

import java.util.ArrayList;
import java.util.Arrays;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.List;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.FormToolkit;

import edu.ualberta.med.biobank.dialogs.ListAddDialog;
import edu.ualberta.med.biobank.model.SdataType;

public class SdataWidget extends Composite {
    Button checkButton;
    Button addButton;
    Button removeButton;

    public SdataWidget(Composite parent, int style, SdataType sdataType) {
        super(parent, style);
        
        setLayout(new GridLayout(1, false));
        setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        
        final String type = sdataType.getType();
        if (type.equals("Aliquot Volume") || type.equals("Blood Received") 
                || type.equals("Visit")) {
            
            checkButton = new Button(this, SWT.CHECK);
            checkButton.setText(type);
            
            // this composite holds the list and the "Add" and "Remove" buttons
            Composite comp = new Composite(this, SWT.NONE);
            comp.setLayout(new GridLayout(2, false));
            comp.setLayoutData(new GridData(GridData.FILL_BOTH));
            
            final List list = new List(comp, SWT.BORDER | SWT.V_SCROLL);
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
                    String title = "";
                    String prompt = "";
                    String helpText = "";

                    if (type.equals("Aliquot Volume")) {
                        title = "Allowed Aliquot Volumes";
                        prompt = "Please enter a new volume:";
                        helpText = "To enter multiple volumes, separate with semicolon.";
                    }
                    else if (type.equals("Blood Received")) { 
                        title = "Allowed Blood Received Volumes";
                        prompt = "Please enter a new volume:";
                        helpText = "To enter multiple volumes, separate with semicolon.";
                    }
                    else if (type.equals("Visit")) {
                        title = "Visit Values";
                        prompt = "Please enter a visit type:";
                        helpText = "To enter multiple visit values, separate with semicolon.";
                    }
                    else {
                        Assert.isTrue(false, "invalid value for type " + type);
                    }
                    
                    ListAddDialog dlg = new ListAddDialog(
                            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), 
                            title, prompt, helpText);
                    dlg.open();
                    
                    // make sure there are no duplicates
                    String[] newItems = dlg.getResult();
                    String[] currentItems = list.getItems();
                    ArrayList<String> duplicates = new ArrayList<String>();
                    ArrayList<String> unique = new ArrayList<String>();
                    
                    for (String newItem : newItems) {
                        boolean found = false;
                        for (String currentItem : currentItems) {
                            if (currentItem.equals(newItem)) {
                                found = true;
                                duplicates.add(newItem);
                                break;
                            }
                        }
                        
                        if (!found) {
                            unique.add(newItem);
                        }
                    }
                    
                    int numDuplicates = duplicates.size();
                    if (numDuplicates > 0) {
                        String msg = "Value " + duplicates.get(0) + " already in " + title;
                        if (numDuplicates > 1) {
                            msg = "Values " + duplicates.toString() + " already in " + title;                            
                        }
                        MessageDialog.openError(
                                PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                                title, msg);
                    }
                    
                    for (String item : unique.toArray(new String[unique.size()])) {
                        list.add(item);
                    }
                    checkButton.setSelection(true);
                }
            });
            
            removeButton = new Button(comp, SWT.PUSH);
            removeButton.setText("Remove");
            removeButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

            removeButton.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(SelectionEvent e) {
                    for (String selection : list.getSelection()) {
                        list.remove(selection);
                    }
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
