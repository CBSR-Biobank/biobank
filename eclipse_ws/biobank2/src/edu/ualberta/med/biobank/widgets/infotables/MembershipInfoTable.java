package edu.ualberta.med.biobank.widgets.infotables;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.PlatformUI;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.common.action.security.ManagerContext;
import edu.ualberta.med.biobank.common.action.security.MembershipContext;
import edu.ualberta.med.biobank.common.util.StringUtil;
import edu.ualberta.med.biobank.dialogs.user.MembershipEditWizard;
import edu.ualberta.med.biobank.dialogs.user.SecurityWizardDialog;
import edu.ualberta.med.biobank.gui.common.widgets.BgcLabelProvider;
import edu.ualberta.med.biobank.gui.common.widgets.BgcTableSorter;
import edu.ualberta.med.biobank.gui.common.widgets.DefaultAbstractInfoTableWidget;
import edu.ualberta.med.biobank.gui.common.widgets.IInfoTableDeleteItemListener;
import edu.ualberta.med.biobank.gui.common.widgets.IInfoTableDoubleClickItemListener;
import edu.ualberta.med.biobank.gui.common.widgets.IInfoTableEditItemListener;
import edu.ualberta.med.biobank.gui.common.widgets.InfoTableEvent;
import edu.ualberta.med.biobank.model.Center;
import edu.ualberta.med.biobank.model.Domain;
import edu.ualberta.med.biobank.model.Membership;
import edu.ualberta.med.biobank.model.PermissionEnum;
import edu.ualberta.med.biobank.model.Principal;
import edu.ualberta.med.biobank.model.Role;
import edu.ualberta.med.biobank.model.Study;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class MembershipInfoTable
    extends DefaultAbstractInfoTableWidget<Membership> {
    public static final I18n i18n = I18nFactory
        .getI18n(MembershipInfoTable.class);
    public static final int ROWS_PER_PAGE = 7;
    @SuppressWarnings("nls")
    private static final String[] HEADINGS = new String[] {
        Center.NAME.plural().toString(),
        Study.NAME.plural().toString(),
        i18n.tr("Manager"),
        i18n.tr("Roles and Permissions") };

    private final ManagerContext managerContext;

    @SuppressWarnings("unused")
    public MembershipInfoTable(Composite parent,
        final Principal principal, MembershipContext context,
        ManagerContext managerContext) {
        super(parent, HEADINGS, ROWS_PER_PAGE);

        setCollection(principal.getMemberships());

        this.managerContext = managerContext;

        addEditItemListener(new IInfoTableEditItemListener<Membership>() {
            @Override
            public void editItem(InfoTableEvent<Membership> event) {
                Membership membership = getSelection();
                editMembership(membership);
            }
        });

        addDeleteItemListener(new IInfoTableDeleteItemListener<Membership>() {
            @Override
            public void deleteItem(InfoTableEvent<Membership> event) {
                Membership membership = getSelection();
                principal.getMemberships().remove(membership);
                getList().remove(membership);
                setList(getList());
            }
        });

        addClickListener(new IInfoTableDoubleClickItemListener<Membership>() {
            @Override
            public void doubleClick(InfoTableEvent<Membership> event) {
                Membership m = getSelection();
                if (m != null) editMembership(m);
            }
        });

        MultilineHandler handler = new MultilineHandler();

        Table table = getTableViewer().getTable();
        table.addListener(SWT.MeasureItem, handler);
        table.addListener(SWT.PaintItem, handler);
        table.addListener(SWT.EraseItem, handler);
    }

    protected void editMembership(Membership m) {
        Shell shell = PlatformUI.getWorkbench()
            .getActiveWorkbenchWindow().getShell();

        MembershipEditWizard wiz =
            new MembershipEditWizard(m, managerContext);
        WizardDialog dlg = new SecurityWizardDialog(shell, wiz);

        int res = dlg.open();
        if (res == Dialog.OK) {
            setCollection(getList());
            setSelection(m);
            notifyListeners();
        }
    }

    @SuppressWarnings("nls")
    private static String getCentersString(Membership m) {
        if (m.getDomain().isAllCenters()) {
            return Domain.PropertyName.ALL_CENTERS.toString();
        }

        List<String> centerNames = new ArrayList<String>();
        for (Center c : m.getDomain().getCenters()) {
            centerNames.add(c.getNameShort());
        }
        Collections.sort(centerNames, String.CASE_INSENSITIVE_ORDER);
        return StringUtil.join(centerNames, "\n");
    }

    @SuppressWarnings("nls")
    private static String getStudiesString(Membership m) {
        if (m.getDomain().isAllStudies()) {
            return Domain.PropertyName.ALL_STUDIES.toString();
        }

        List<String> studyNames = new ArrayList<String>();
        for (Study s : m.getDomain().getStudies()) {
            studyNames.add(s.getNameShort());
        }
        Collections.sort(studyNames, String.CASE_INSENSITIVE_ORDER);
        return StringUtil.join(studyNames, "\n");
    }

    @SuppressWarnings("nls")
    private static String getRolesAndPermissionsSummary(Membership m) {
        if (m.isEveryPermission()) return i18n.tr("All");

        List<String> rolesAndPerms = new ArrayList<String>();
        for (Role role : m.getRoles()) {
            rolesAndPerms.add(role.getName());
        }
        for (PermissionEnum permission : m.getPermissions()) {
            rolesAndPerms.add(permission.getName());
        }
        Collections.sort(rolesAndPerms, String.CASE_INSENSITIVE_ORDER);
        String summary = StringUtil.join(rolesAndPerms, ", ");
        summary = StringUtil.truncate(summary, 50, "...");
        return summary;
    }

    @Override
    protected BgcLabelProvider getLabelProvider() {
        return new BgcLabelProvider() {
            @SuppressWarnings("nls")
            @Override
            public String getColumnText(Object element, int columnIndex) {
                Membership m = (Membership) element;
                switch (columnIndex) {
                case 0:
                    return getCentersString(m);
                case 1:
                    return getStudiesString(m);
                case 2:
                    return m.isUserManager()
                        ? i18n.tr("Yes")
                        : i18n.tr("No");
                case 3:
                    return getRolesAndPermissionsSummary(m);
                default:
                    return StringUtil.EMPTY_STRING;
                }
            }
        };
    }

    @Override
    protected BgcTableSorter getTableSorter() {
        return null;
    }

    /**
     * 
     * @see http://www.java2s.com/Tutorial/Java/0280__SWT/MultilineTablecell.htm
     */
    public static class MultilineHandler implements Listener {
        public MultilineHandler() {
        }

        @Override
        public void handleEvent(Event event) {
            switch (event.type) {
            case SWT.MeasureItem: {
                TableItem item = (TableItem) event.item;
                String text = getText(item, event.index);
                Point size = event.gc.textExtent(text);
                event.width = size.x;
                event.height = Math.max(event.height, size.y);
                break;
            }
            case SWT.PaintItem: {
                TableItem item = (TableItem) event.item;
                String text = getText(item, event.index);
                Point size = event.gc.textExtent(text);
                int offset = Math.max(0, (event.height - size.y) / 2);
                event.gc.drawText(text, event.x, event.y + offset, true);
                break;
            }
            case SWT.EraseItem: {
                event.detail &= ~SWT.FOREGROUND;
                break;
            }
            }
        }

        String getText(TableItem item, int column) {
            String text = item.getText(column);
            return text;
        }
    }

    @Override
    protected Boolean canEdit(Membership target) throws ApplicationException {
        return true;
    }

    @Override
    protected Boolean canDelete(Membership target) throws ApplicationException {
        return true;
    }

    @Override
    protected Boolean canView(Membership target) throws ApplicationException {
        return true;
    };
}