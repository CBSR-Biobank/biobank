package edu.ualberta.med.biobank.widgets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.Dialog;
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
import edu.ualberta.med.biobank.model.PvCustomInfo;

public class PvInfoWidget extends BiobankWidget {
    private Button checkButton;
    private Button addButton;
    private Button removeButton;
    private List list;
    boolean hasListValues;

    private static class LabelDialogInfo {
        public String title;
        public String prompt;
        public String helpText;

        public LabelDialogInfo(String title, String prompt, String helpText) {
            this.title = title;
            this.prompt = prompt;
            this.helpText = helpText;
        }
    };

    private static Map<String, LabelDialogInfo> LABEL_DLG_INFO = new HashMap<String, LabelDialogInfo>() {
        private static final long serialVersionUID = 1L;
        {
            put("Aliquot Volume", new LabelDialogInfo(
                "Allowed Aliquot Volumes", "Please enter a new volume:",
                "To enter multiple volumes, separate with semicolon."));
            put("Blood Received", new LabelDialogInfo(
                "Allowed Blood Received Volumes", "Please enter a new volume:",
                "To enter multiple volumes, separate with semicolon."));
            put(
                "Visit Type",
                new LabelDialogInfo("Visit Type Values",
                    "Please enter a visit type:",
                    "To enter multiple visit type values, separate with semicolon."));
            put("Consent", new LabelDialogInfo("Consent Types",
                "Please enter a consent type:",
                "To enter multiple consent values, separate with semicolon."));
        }
    };

    public PvInfoWidget(Composite parent, int style,
        final PvCustomInfo pvCustomInfo) {
        super(parent, style);

        setLayout(new GridLayout(1, false));
        setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        hasListValues = (pvCustomInfo.type.equals(4) || pvCustomInfo.type
            .equals(5));
        boolean selected = (pvCustomInfo.allowedValues != null);

        if (hasListValues) {
            checkButton = new Button(this, SWT.CHECK);
            checkButton.setText(pvCustomInfo.label);
            checkButton.addSelectionListener(new SelectionListener() {

                @Override
                public void widgetDefaultSelected(SelectionEvent e) {
                    notifyListeners();
                }

                @Override
                public void widgetSelected(SelectionEvent e) {
                    notifyListeners();
                }

            });

            if (pvCustomInfo.isDefault) {
                checkButton.setEnabled(false);
                checkButton.setSelection(true);
            } else {
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
                    notifyListeners();
                    LabelDialogInfo labelDlgInfo = LABEL_DLG_INFO
                        .get(pvCustomInfo.label);
                    Assert.isNotNull(labelDlgInfo, "no dialog info for label "
                        + pvCustomInfo.label);

                    ListAddDialog dlg = new ListAddDialog(PlatformUI
                        .getWorkbench().getActiveWorkbenchWindow().getShell(),
                        labelDlgInfo.title, labelDlgInfo.prompt,
                        labelDlgInfo.helpText);
                    if (dlg.open() == Dialog.OK) {
                        // make sure there are no duplicates
                        String[] newItems = dlg.getResult();
                        String[] currentItems = list.getItems();
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

                            if (!found)
                                list.add(newItem.trim());
                        }

                        int numDuplicates = duplicates.size();
                        if (numDuplicates > 0) {
                            String msg = "Value " + duplicates.get(0)
                                + " already in " + labelDlgInfo.title;
                            if (numDuplicates > 1) {
                                msg = "Values " + duplicates.toString()
                                    + " already in " + labelDlgInfo.title;
                            }
                            BioBankPlugin.openError(labelDlgInfo.title, msg);
                        }
                        checkButton.setSelection(true);
                    }
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
                        notifyListeners();
                    }
                }
            });

            list = new List(comp, SWT.BORDER | SWT.V_SCROLL);
            list.setLayoutData(new GridData(GridData.FILL_BOTH));
            if (pvCustomInfo.allowedValues != null) {
                for (String item : pvCustomInfo.allowedValues) {
                    list.add(item);
                }
            }
            Menu m = new Menu(PlatformUI.getWorkbench()
                .getActiveWorkbenchWindow().getShell(), SWT.POP_UP);

            MenuItem mi = new MenuItem(m, SWT.CASCADE);
            mi.setText("Move to Top");
            mi.addSelectionListener(new SelectionListener() {
                public void widgetSelected(SelectionEvent event) {
                    int index = list.getSelectionIndex();
                    if (index <= 0)
                        return;
                    String[] items = list.getItems();
                    String[] newList = new String[items.length];
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
                public void widgetDefaultSelected(SelectionEvent e) {
                }
            });

            mi = new MenuItem(m, SWT.CASCADE);
            mi.setText("Move Up");
            mi.addSelectionListener(new SelectionListener() {
                public void widgetSelected(SelectionEvent event) {
                    int index = list.getSelectionIndex();
                    if (index <= 0)
                        return;
                    String[] items = list.getItems();
                    String[] newList = new String[items.length];
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
                public void widgetDefaultSelected(SelectionEvent e) {
                }
            });

            mi = new MenuItem(m, SWT.CASCADE);
            mi.setText("Move Down");
            mi.addSelectionListener(new SelectionListener() {
                public void widgetSelected(SelectionEvent event) {
                    int index = list.getSelectionIndex();
                    String[] items = list.getItems();
                    if (index >= items.length - 1)
                        return;
                    String[] newList = new String[items.length];
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
                public void widgetDefaultSelected(SelectionEvent e) {
                }
            });

            mi = new MenuItem(m, SWT.CASCADE);
            mi.setText("Move to Bottom");
            mi.addSelectionListener(new SelectionListener() {
                public void widgetSelected(SelectionEvent event) {
                    int index = list.getSelectionIndex();
                    String[] items = list.getItems();
                    if (index >= items.length - 1)
                        return;
                    String[] newList = new String[items.length];
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
                public void widgetDefaultSelected(SelectionEvent e) {
                }
            });

            list.setMenu(m);
        } else {
            checkButton = new Button(this, SWT.CHECK);
            checkButton.setText(pvCustomInfo.label);
            GridData gd = new GridData(GridData.VERTICAL_ALIGN_BEGINNING
                | GridData.GRAB_HORIZONTAL);
            checkButton.setLayoutData(gd);
            checkButton.addSelectionListener(new SelectionListener() {

                @Override
                public void widgetDefaultSelected(SelectionEvent e) {
                    notifyListeners();
                }

                @Override
                public void widgetSelected(SelectionEvent e) {
                    notifyListeners();
                }

            });

            if (pvCustomInfo.isDefault) {
                checkButton.setEnabled(false);
                checkButton.setSelection(true);
            } else {
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
