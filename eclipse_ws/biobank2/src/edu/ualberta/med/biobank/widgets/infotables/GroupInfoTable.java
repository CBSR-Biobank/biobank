package edu.ualberta.med.biobank.widgets.infotables;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.PlatformUI;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.action.security.GroupDeleteAction;
import edu.ualberta.med.biobank.common.action.security.GroupDeleteInput;
import edu.ualberta.med.biobank.common.action.security.GroupGetAction;
import edu.ualberta.med.biobank.common.action.security.GroupGetInput;
import edu.ualberta.med.biobank.common.action.security.GroupGetOutput;
import edu.ualberta.med.biobank.common.action.security.ManagerContext;
import edu.ualberta.med.biobank.common.util.StringUtil;
import edu.ualberta.med.biobank.dialogs.user.GroupEditDialog;
import edu.ualberta.med.biobank.dialogs.user.TmpUtil;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.gui.common.widgets.BgcLabelProvider;
import edu.ualberta.med.biobank.gui.common.widgets.DefaultAbstractInfoTableWidget;
import edu.ualberta.med.biobank.gui.common.widgets.IInfoTableDeleteItemListener;
import edu.ualberta.med.biobank.gui.common.widgets.IInfoTableDoubleClickItemListener;
import edu.ualberta.med.biobank.gui.common.widgets.IInfoTableEditItemListener;
import edu.ualberta.med.biobank.gui.common.widgets.InfoTableEvent;
import edu.ualberta.med.biobank.model.Group;
import edu.ualberta.med.biobank.model.HasName;
import edu.ualberta.med.biobank.util.NullUtil;

public abstract class GroupInfoTable extends
    DefaultAbstractInfoTableWidget<Group> {
    public static final I18n i18n = I18nFactory.getI18n(GroupInfoTable.class);
    public static final int ROWS_PER_PAGE = 12;
    private static final String[] HEADINGS =
        new String[] { HasName.PropertyName.NAME.toString() };

    private final ManagerContext context;

    @SuppressWarnings("nls")
    public GroupInfoTable(Composite parent, List<Group> collection,
        ManagerContext context) {
        super(parent, HEADINGS, ROWS_PER_PAGE);

        setList(collection);

        this.context = context;

        addEditItemListener(new IInfoTableEditItemListener<Group>() {
            @Override
            public void editItem(InfoTableEvent<Group> event) {
                editGroup(getSelection());
            }
        });

        addDeleteItemListener(new IInfoTableDeleteItemListener<Group>() {
            @Override
            public void deleteItem(InfoTableEvent<Group> event) {
                deleteGroup(getSelection());
            }
        });

        addClickListener(new IInfoTableDoubleClickItemListener<Group>() {
            @Override
            public void doubleClick(InfoTableEvent<Group> event) {
                Group g = getSelection();
                if (g != null) editGroup(g);
            }
        });

        MenuItem item = new MenuItem(menu, SWT.PUSH);
        item.setText(i18n.tr("duplicate (verb)", "Duplicate"));
        item.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                duplicate(getSelection());
            }
        });
    }

    protected abstract void duplicate(Group original);

    @Override
    protected BgcLabelProvider getLabelProvider() {
        return new BgcLabelProvider() {
            @Override
            public String getColumnText(Object element, int columnIndex) {
                Group group = (Group) element;
                switch (columnIndex) {
                case 0:
                    return group.getName();
                default:
                    return StringUtil.EMPTY_STRING;
                }
            }
        };
    }

    protected void editGroup(Group group) {
        GroupGetOutput output = null;

        try {
            output = SessionManager.getAppService()
                .doAction(new GroupGetAction(new GroupGetInput(group)));
        } catch (Throwable t) {
            TmpUtil.displayException(t);
            return;
        }

        GroupEditDialog dlg = new GroupEditDialog(PlatformUI.getWorkbench()
            .getActiveWorkbenchWindow().getShell(), output, context);
        int res = dlg.open();
        if (res == Dialog.OK) {
            Group modifiedGroup = output.getGroup();

            List<Group> tmp = new ArrayList<Group>(getList());
            tmp.remove(group);
            tmp.add(modifiedGroup);
            Collections.sort(tmp, new GroupComparator());
            setList(tmp);

            setSelection(modifiedGroup);
        }
    }

    @SuppressWarnings("nls")
    protected boolean deleteGroup(Group group) {
        try {
            String name = group.getName();
            // dialog message.
            String message =
                i18n.tr("Are you certain you want to delete \"{0}\"?",
                    name);

            if (BgcPlugin.openConfirm(
                // dialog title.
                i18n.tr("Confirm Deletion"), message)) {

                SessionManager.getAppService().doAction(
                    new GroupDeleteAction(new GroupDeleteInput(group)));

                // remove the group from the collection
                getList().remove(group);

                reload();

                notifyListeners();
                return true;
            }
        } catch (Throwable t) {
            TmpUtil.displayException(t);
        }
        return false;
    }

    public static class GroupComparator implements Comparator<Group> {
        @Override
        public int compare(Group a, Group b) {
            return NullUtil.cmp(a.getName(), b.getName(),
                String.CASE_INSENSITIVE_ORDER);
        }
    }
}