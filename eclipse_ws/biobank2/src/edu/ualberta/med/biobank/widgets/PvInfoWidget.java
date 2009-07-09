
package edu.ualberta.med.biobank.widgets;

import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.PlatformUI;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.dialogs.ListAddDialog;
import edu.ualberta.med.biobank.model.PvInfoPossible;

public class PvInfoWidget extends BiobankWidget {
    String label;
    String type;
    Button checkButton;
    Button addButton;
    Button removeButton;
    List list;
    boolean hasListValues;

    public PvInfoWidget(Composite parent, int style,
        PvInfoPossible pvInfoPossible, boolean selected, String value) {
        super(parent, style);

        setLayout(new GridLayout(1, false));
        setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        label = pvInfoPossible.getLabel();
        type = pvInfoPossible.getPvInfoType().getType();
        hasListValues = type.equals("select_single")
            || type.equals("select_single_and_quantity")
            || type.equals("select_multiple");
        if (hasListValues) {

            checkButton = new Button(this, SWT.CHECK);
            checkButton.setText(label);

            if (pvInfoPossible.getIsDefault()) {
                checkButton.setEnabled(false);
                checkButton.setSelection(true);
            }
            else {
                checkButton.setSelection(selected);
            }

            // this composite holds the list and the "Add" and "Remove" buttons
            Composite comp = new Composite(this, SWT.NONE);
            comp.setLayout(new GridLayout(2, false));
            comp.setLayoutData(new GridData(GridData.FILL_BOTH));

            // this composite holds the "Add" and "Remove" buttons
            Composite bcomp = new Composite(comp, SWT.NONE);
            bcomp.setLayout(new GridLayout(1, false));
            bcomp.setLayoutData(new GridData());

            addButton = new Button(bcomp, SWT.PUSH);
            addButton.setText("Add");
            addButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

            addButton.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    String title = "";
                    String prompt = "";
                    String helpText = "";

                    if (label.equals("Aliquot Volume")) {
                        title = "Allowed Aliquot Volumes";
                        prompt = "Please enter a new volume:";
                        helpText = "To enter multiple volumes, separate with semicolon.";
                    }
                    else if (label.equals("Blood Received")) {
                        title = "Allowed Blood Received Volumes";
                        prompt = "Please enter a new volume:";
                        helpText = "To enter multiple volumes, separate with semicolon.";
                    }
                    else if (label.equals("Visit Type")) {
                        title = "Visit Type Values";
                        prompt = "Please enter a visit type:";
                        helpText = "To enter multiple visit type values, separate with semicolon.";
                    }
                    else if (label.equals("Consent")) {
                        title = "Consent Types";
                        prompt = "Please enter a consent type:";
                        helpText = "To enter multiple consent values, separate with semicolon.";
                    }
                    else {
                        Assert.isTrue(false, "invalid value for label " + label);
                    }

                    ListAddDialog dlg = new ListAddDialog(
                        PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                        title, prompt, helpText);
                    dlg.open();

                    // make sure there are no duplicates
                    String [] newItems = dlg.getResult();
                    String [] currentItems = list.getItems();
                    ArrayList<String> duplicates = new ArrayList<String>();

                    for (String newItem : newItems) {
                        boolean found = false;
                        for (String currentItem : currentItems) {
                            if (currentItem.equals(newItem)) {
                                found = true;
                                duplicates.add(newItem);
                                break;
                            }
                        }

                        if (!found) list.add(newItem.trim());
                    }

                    int numDuplicates = duplicates.size();
                    if (numDuplicates > 0) {
                        String msg = "Value " + duplicates.get(0)
                            + " already in " + title;
                        if (numDuplicates > 1) {
                            msg = "Values " + duplicates.toString()
                                + " already in " + title;
                        }
                        BioBankPlugin.openError(title, msg);
                    }
                    checkButton.setSelection(true);
                }
            });

            removeButton = new Button(bcomp, SWT.PUSH);
            removeButton.setText("Remove");
            removeButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

            removeButton.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    for (String selection : list.getSelection()) {
                        list.remove(selection);
                    }
                }
            });

            list = new List(comp, SWT.BORDER | SWT.V_SCROLL);
            list.setLayoutData(new GridData(GridData.FILL_BOTH));
            if (value.length() > 0) {
                for (String item : value.split(";")) {
                    list.add(item);
                }
            }
            Menu m = new Menu(
                PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                SWT.POP_UP);

            MenuItem mi = new MenuItem(m, SWT.CASCADE);
            mi.setText("Move to Top");
            mi.addSelectionListener(new SelectionListener() {
                public void widgetSelected(SelectionEvent event) {
                    int index = list.getSelectionIndex();
                    if (index <= 0) return;
                    String [] items = list.getItems();
                    String [] newList = new String [items.length];
                    newList[0] = items[index];
                    int i = 1;
                    for (String item : items) {
                        if (!item.equals(items[index])) {
                            newList[i] = item;
                            ++i;
                        }
                    }
                    list.setItems(newList);
                }

                @Override
                public void widgetDefaultSelected(SelectionEvent e) {}
            });

            mi = new MenuItem(m, SWT.CASCADE);
            mi.setText("Move Up");
            mi.addSelectionListener(new SelectionListener() {
                public void widgetSelected(SelectionEvent event) {
                    int index = list.getSelectionIndex();
                    if (index <= 0) return;
                    String [] items = list.getItems();
                    String [] newList = new String [items.length];
                    int i = 0;
                    for (String item : items) {
                        if ((i < index - 1) || (i > index)) {
                            newList[i] = item;
                        }
                        ++i;
                    }
                    newList[index - 1] = items[index];
                    newList[index] = items[index - 1];
                    list.setItems(newList);
                }

                @Override
                public void widgetDefaultSelected(SelectionEvent e) {}
            });

            mi = new MenuItem(m, SWT.CASCADE);
            mi.setText("Move Down");
            mi.addSelectionListener(new SelectionListener() {
                public void widgetSelected(SelectionEvent event) {
                    int index = list.getSelectionIndex();
                    String [] items = list.getItems();
                    if (index >= items.length - 1) return;
                    String [] newList = new String [items.length];
                    int i = 0;
                    for (String item : items) {
                        if ((i < index) || (i > index + 1)) {
                            newList[i] = item;
                        }
                        ++i;
                    }
                    newList[index] = items[index + 1];
                    newList[index + 1] = items[index];
                    list.setItems(newList);
                }

                @Override
                public void widgetDefaultSelected(SelectionEvent e) {}
            });

            mi = new MenuItem(m, SWT.CASCADE);
            mi.setText("Move to Bottom");
            mi.addSelectionListener(new SelectionListener() {
                public void widgetSelected(SelectionEvent event) {
                    int index = list.getSelectionIndex();
                    String [] items = list.getItems();
                    if (index >= items.length - 1) return;
                    String [] newList = new String [items.length];
                    int i = 0;
                    for (String item : items) {
                        if (!item.equals(items[index])) {
                            newList[i] = item;
                            ++i;
                        }
                    }
                    newList[i] = items[index];
                    list.setItems(newList);
                }

                @Override
                public void widgetDefaultSelected(SelectionEvent e) {}
            });

            list.setMenu(m);
        }
        else {
            checkButton = new Button(this, SWT.CHECK);
            checkButton.setText(pvInfoPossible.getLabel());
            GridData gd = new GridData(GridData.VERTICAL_ALIGN_BEGINNING
                | GridData.GRAB_HORIZONTAL);
            checkButton.setLayoutData(gd);

            if (pvInfoPossible.getIsDefault()) {
                checkButton.setEnabled(false);
                checkButton.setSelection(true);
            }
            else {
                checkButton.setSelection(selected);
            }
        }
    }

    public boolean getSelected() {
        return checkButton.getSelection();
    }

    public String getValues() {
        if (hasListValues) {
            return StringUtils.join(list.getItems(), ";");
        }
        return null;
    }

}
