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
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.PlatformUI;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.common.wrappers.EventAttrTypeEnum;
import edu.ualberta.med.biobank.dialogs.ListAddDialog;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseWidget;
import edu.ualberta.med.biobank.model.EventAttrCustom;

public class EventAttrWidget extends BgcBaseWidget {
    private static final I18n i18n = I18nFactory
        .getI18n(EventAttrWidget.class);

    private Button checkButton;
    private Button addButton;
    private Button removeButton;
    private List itemList;
    private boolean hasListValues;
    private LabelDialogInfo labelDlgInfo;

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

    @SuppressWarnings("nls")
    private static Map<String, LabelDialogInfo> LABEL_DLG_INFO =
        new HashMap<String, LabelDialogInfo>() {
            private static final long serialVersionUID = 1L;
            {
                put(i18n.tr("Patient Type"),
                    new LabelDialogInfo(
                        i18n.tr("Patient Type Values"),
                        i18n.tr("Please enter a patient type:"),
                        i18n.tr("To enter multiple patient type values, separate with semicolon.")));
                put(i18n.tr("Visit Type"),
                    new LabelDialogInfo(
                        i18n.tr("Visit Type Values"),
                        i18n.tr("Please enter a visit type:"),
                        i18n.tr("To enter multiple visit type values, separate with semicolon.")));
                put(i18n.tr("Consent"),
                    new LabelDialogInfo(
                        i18n.tr("Consent Types"),
                        i18n.tr("Please enter a consent type:"),
                        i18n.tr("To enter multiple consent values, separate with semicolon.")));
            }
        };

    @SuppressWarnings("nls")
    public EventAttrWidget(Composite parent, int style,
        final EventAttrCustom customInfo, boolean selected) {
        super(parent, style);
        setLayout(new GridLayout(1, false));
        setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        hasListValues = customInfo.getType() == EventAttrTypeEnum.SELECT_SINGLE
            || customInfo.getType() == EventAttrTypeEnum.SELECT_MULTIPLE;
        // selected |= (customInfo.getAllowedValues() != null);

        if (hasListValues) {
            labelDlgInfo = LABEL_DLG_INFO.get(customInfo.getLabel());
            Assert.isNotNull(labelDlgInfo, "no dialog info for label "
                + customInfo.getLabel());

            createCheckButton(customInfo, selected);

            // this composite holds the list and the "Add" and "Remove" buttons
            Composite comp = new Composite(this, SWT.NONE);
            comp.setLayout(new GridLayout(2, false));
            comp.setLayoutData(new GridData(GridData.FILL_BOTH));

            // this composite holds the "Add" and "Remove" buttons
            Composite bcomp = new Composite(comp, SWT.NONE);
            bcomp.setLayout(new GridLayout(1, false));
            bcomp.setLayoutData(new GridData());

            addButton = new Button(bcomp, SWT.PUSH);
            addButton.setImage(BgcPlugin.getDefault().getImageRegistry()
                .get(BgcPlugin.IMG_ADD));
            addButton.setToolTipText(i18n.tr("Add"));
            addButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

            addButton.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    notifyListeners();
                    Assert.isNotNull(labelDlgInfo, "no dialog info for label "
                        + customInfo.getLabel());

                    ListAddDialog dlg = new ListAddDialog(PlatformUI
                        .getWorkbench().getActiveWorkbenchWindow().getShell(),
                        labelDlgInfo.title, labelDlgInfo.prompt,
                        labelDlgInfo.helpText);
                    if (dlg.open() == Dialog.OK) {
                        java.util.List<String> currentItems =
                            new ArrayList<String>(
                                Arrays.asList(itemList.getItems()));
                        java.util.List<String> newItems = Arrays.asList(dlg
                            .getResult());

                        if (currentItems.size() == 0) {
                            currentItems.addAll(newItems);
                        } else {
                            // make sure there are no duplicates
                            for (String item : newItems) {
                                item = item.trim();
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
            removeButton.setImage(BgcPlugin.getDefault().getImageRegistry()
                .get(BgcPlugin.IMG_DELETE));
            removeButton.setToolTipText(i18n.tr("Remove"));
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
            if (customInfo.getAllowedValues() != null) {
                for (String item : customInfo.getAllowedValues()) {
                    itemList.add(item);
                }
            }
            Menu m = new Menu(PlatformUI.getWorkbench()
                .getActiveWorkbenchWindow().getShell(), SWT.POP_UP);

            MenuItem mi = new MenuItem(m, SWT.CASCADE);
            mi.setText(i18n.tr("Move to Top"));
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
            mi.setText(i18n.tr("Move Up"));
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
            mi.setText(i18n.tr("Move Down"));
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
            mi.setText(i18n.tr("Move to Bottom"));
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
            createCheckButton(customInfo, selected);
        }
    }

    private void createCheckButton(final EventAttrCustom pvCustomInfo,
        boolean selected) {
        checkButton = new Button(this, SWT.CHECK);
        checkButton.setText(pvCustomInfo.getLabel());
        checkButton.addSelectionListener(new SelectionAdapter() {
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

    public boolean getSelected() {
        return checkButton.getSelection();
    }

    public String getValues() {
        if (hasListValues) {
            return StringUtils.join(itemList.getItems(),
                EventAttrCustom.VALUE_MULTIPLE_SEPARATOR);
        }
        return null;
    }

    public void setSelected(boolean selected) {
        checkButton.setSelection(selected);
    }

    public void reloadAllowedValues(EventAttrCustom pvCustomInfo) {
        if (itemList != null) {
            itemList.removeAll();
            if (pvCustomInfo.getAllowedValues() != null) {
                for (String item : pvCustomInfo.getAllowedValues()) {
                    itemList.add(item);
                }
            }
        }
    }

}
