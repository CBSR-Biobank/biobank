package edu.ualberta.med.biobank.widgets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
import edu.ualberta.med.biobank.model.PvAttrCustom;

public class PvInfoWidget extends BiobankWidget {
    private Button checkButton;
    private Button addButton;
    private Button removeButton;
    private List itemList;
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
            put(
                "Visit",
                new LabelDialogInfo("Visit Type Values",
                    "Please enter a visit type:",
                    "To enter multiple visit type values, separate with semicolon."));
            put("Consent", new LabelDialogInfo("Consent Types",
                "Please enter a consent type:",
                "To enter multiple consent values, separate with semicolon."));
        }
    };

    private LabelDialogInfo labelDlgInfo;

    public PvInfoWidget(Composite parent, int style,
        final PvAttrCustom pvCustomInfo, boolean selected) {
        super(parent, style);
        setLayout(new GridLayout(1, false));
        setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        hasListValues = (pvCustomInfo.getType().equals("select_single") || pvCustomInfo
            .getType().equals("select_multiple"));
        selected |= (pvCustomInfo.getAllowedValues() != null);

        if (hasListValues) {
            labelDlgInfo = LABEL_DLG_INFO.get(pvCustomInfo.getLabel());
            Assert.isNotNull(labelDlgInfo, "no dialog info for label "
                + pvCustomInfo.getLabel());

            checkButton = new Button(this, SWT.CHECK);
            checkButton.setText(pvCustomInfo.getLabel());
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

            if (pvCustomInfo.getIsDefault()) {
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
            addButton.setImage(BioBankPlugin.getDefault().getImageRegistry()
                .get(BioBankPlugin.IMG_ADD));
            addButton.setToolTipText("Add");
            addButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

            addButton.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    notifyListeners();
                    Assert.isNotNull(labelDlgInfo, "no dialog info for label "
                        + pvCustomInfo.getLabel());

                    ListAddDialog dlg = new ListAddDialog(PlatformUI
                        .getWorkbench().getActiveWorkbenchWindow().getShell(),
                        labelDlgInfo.title, labelDlgInfo.prompt,
                        labelDlgInfo.helpText);
                    if (dlg.open() == Dialog.OK) {
                        java.util.List<String> currentItems = new ArrayList<String>(
                            Arrays.asList(itemList.getItems()));
                        java.util.List<String> newItems = Arrays.asList(dlg
                            .getResult());

                        if (currentItems.size() == 0) {
                            currentItems.addAll(newItems);
                        } else {
                            // make sure there are no duplicates
                            for (String item : newItems) {
                                item.trim();
                                if (!currentItems.contains(item)) {
                                    currentItems.add(item);
                                }
                            }
                        }

                        Collections.sort(currentItems);
                        itemList.removeAll();
                        for (String item : currentItems) {
                            itemList.add(item);
                        }
                        checkButton.setSelection(true);
                    }
                }
            });

            removeButton = new Button(bcomp, SWT.PUSH);
            removeButton.setImage(BioBankPlugin.getDefault().getImageRegistry()
                .get(BioBankPlugin.IMG_DELETE));
            removeButton.setToolTipText("Remove");
            removeButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

            removeButton.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    for (String selection : itemList.getSelection()) {
                        itemList.remove(selection);
                        notifyListeners();
                    }
                }
            });

            itemList = new List(comp, SWT.BORDER | SWT.V_SCROLL);
            itemList.setLayoutData(new GridData(GridData.FILL_BOTH));
            if (pvCustomInfo.getAllowedValues() != null) {
                for (String item : pvCustomInfo.getAllowedValues()) {
                    itemList.add(item);
                }
            }
            Menu m = new Menu(PlatformUI.getWorkbench()
                .getActiveWorkbenchWindow().getShell(), SWT.POP_UP);

            MenuItem mi = new MenuItem(m, SWT.CASCADE);
            mi.setText("Move to Top");
            mi.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent event) {
                    int index = itemList.getSelectionIndex();
                    if (index <= 0)
                        return;
                    String[] items = itemList.getItems();
                    String[] newList = new String[items.length];
                    newList[0] = items[index];
                    int i = 1;
                    for (String item : items) {
                        if (!item.equals(items[index])) {
                            newList[i] = item;
                            ++i;
                        }
                    }
                    itemList.setItems(newList);
                }
            });

            mi = new MenuItem(m, SWT.CASCADE);
            mi.setText("Move Up");
            mi.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent event) {
                    int index = itemList.getSelectionIndex();
                    if (index <= 0)
                        return;
                    String[] items = itemList.getItems();
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
                    itemList.setItems(newList);
                }
            });

            mi = new MenuItem(m, SWT.CASCADE);
            mi.setText("Move Down");
            mi.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent event) {
                    int index = itemList.getSelectionIndex();
                    String[] items = itemList.getItems();
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
                    itemList.setItems(newList);
                }
            });

            mi = new MenuItem(m, SWT.CASCADE);
            mi.setText("Move to Bottom");
            mi.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent event) {
                    int index = itemList.getSelectionIndex();
                    String[] items = itemList.getItems();
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
                    itemList.setItems(newList);
                }
            });

            itemList.setMenu(m);
        } else {
            checkButton = new Button(this, SWT.CHECK);
            checkButton.setText(pvCustomInfo.getLabel());
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

            if (pvCustomInfo.getIsDefault()) {
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
            return StringUtils.join(itemList.getItems(), ";");
        }
        return null;
    }

    public void setSelected(boolean selected) {
        checkButton.setSelection(selected);
    }

}
