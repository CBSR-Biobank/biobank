package edu.ualberta.med.biobank.widgets.infotables;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import edu.ualberta.med.biobank.common.action.security.ManagerContext;
import edu.ualberta.med.biobank.common.action.security.MembershipContext;
import edu.ualberta.med.biobank.common.util.StringUtil;
import edu.ualberta.med.biobank.dialogs.user.MembershipEditWizard;
import edu.ualberta.med.biobank.gui.common.widgets.BgcLabelProvider;
import edu.ualberta.med.biobank.gui.common.widgets.BgcTableSorter;
import edu.ualberta.med.biobank.gui.common.widgets.DefaultAbstractInfoTableWidget;
import edu.ualberta.med.biobank.gui.common.widgets.IInfoTableDeleteItemListener;
import edu.ualberta.med.biobank.gui.common.widgets.IInfoTableEditItemListener;
import edu.ualberta.med.biobank.gui.common.widgets.InfoTableEvent;
import edu.ualberta.med.biobank.model.Center;
import edu.ualberta.med.biobank.model.Membership;
import edu.ualberta.med.biobank.model.PermissionEnum;
import edu.ualberta.med.biobank.model.Principal;
import edu.ualberta.med.biobank.model.Role;
import edu.ualberta.med.biobank.model.Study;

public class MembershipInfoTable
    extends DefaultAbstractInfoTableWidget<Membership> {
    public static final int ROWS_PER_PAGE = 7;
    private static final String[] HEADINGS = new String[] {
        Messages.MembershipInfoTable_center_label,
        Messages.MembershipInfoTable_study_label,
        "Manager",
        "Roles and Permissions" };

    private final MembershipContext context;
    private final ManagerContext managerContext;

    public MembershipInfoTable(Composite parent,
        final Principal principal, MembershipContext context,
        ManagerContext managerContext) {
        super(parent, HEADINGS, ROWS_PER_PAGE);

        setCollection(principal.getMemberships());

        this.context = context;
        this.managerContext = managerContext;

        addEditItemListener(new IInfoTableEditItemListener<Membership>() {
            @Override
            public void editItem(InfoTableEvent<Membership> event) {
                Membership membership = ((Membership) getSelection());
                editMembership(membership);
            }
        });

        addDeleteItemListener(new IInfoTableDeleteItemListener<Membership>() {
            @Override
            public void deleteItem(InfoTableEvent<Membership> event) {
                Membership membership = ((Membership) getSelection());
                principal.getMemberships().remove(membership);
                getList().remove(membership);
                setList(getList());
            }
        });
    }

    protected void editMembership(Membership m) {
        Shell shell = PlatformUI.getWorkbench()
            .getActiveWorkbenchWindow().getShell();

        MembershipEditWizard wiz =
            new MembershipEditWizard(m, managerContext);
        WizardDialog dlg = new WizardDialog(shell, wiz);

        int res = dlg.open();
        if (res == Dialog.OK) {
            setCollection(getList());
            setSelection(m);
            notifyListeners();
        }
    }

    private static String getCentersString(Membership m) {
        if (m.getDomain().isAllCenters()) {
            return "All Centers";
        } else {
            List<String> centerNames = new ArrayList<String>();
            for (Center c : m.getDomain().getCenters()) {
                centerNames.add(c.getNameShort());
            }
            Collections.sort(centerNames, String.CASE_INSENSITIVE_ORDER);
            return StringUtil.join(centerNames, "\\r\\n");
        }
    }

    private static String getStudiesString(Membership m) {
        if (m.getDomain().isAllCenters()) {
            return "All Studies";
        } else {
            List<String> studyNames = new ArrayList<String>();
            for (Study s : m.getDomain().getStudies()) {
                studyNames.add(s.getNameShort());
            }
            Collections.sort(studyNames, String.CASE_INSENSITIVE_ORDER);
            return StringUtil.join(studyNames, "\\r\\n");
        }
    }

    private static String getRolesAndPermissionsSummary(Membership m) {
        List<String> rolesAndPerms = new ArrayList<String>();
        for (Role role : m.getRoles()) {
            rolesAndPerms.add(role.getName());
        }
        for (PermissionEnum permission : m.getPermissions()) {
            rolesAndPerms.add(permission.getName());
        }
        String summary = StringUtil.join(rolesAndPerms, ", ");
        summary = StringUtil.truncate(summary, 50, "...");
        return summary;
    }

    @Override
    protected BgcLabelProvider getLabelProvider() {
        return new BgcLabelProvider() {
            @Override
            public String getColumnText(Object element, int columnIndex) {
                Membership m = (Membership) element;
                switch (columnIndex) {
                case 0:
                    return getCentersString(m);
                case 1:
                    return getStudiesString(m);
                case 2:
                    return m.isUserManager() ? "Yes" : "No";
                case 3:
                    return getRolesAndPermissionsSummary(m);
                default:
                    return ""; //$NON-NLS-1$
                }
            }
        };
    }

    @Override
    protected BgcTableSorter getTableSorter() {
        return null;
    }
}