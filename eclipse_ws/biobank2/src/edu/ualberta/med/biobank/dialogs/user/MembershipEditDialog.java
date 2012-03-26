package edu.ualberta.med.biobank.dialogs.user;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

import edu.ualberta.med.biobank.common.action.security.ManagerContext;
import edu.ualberta.med.biobank.gui.common.dialogs.BgcBaseDialog;
import edu.ualberta.med.biobank.gui.common.widgets.BgcEntryFormWidgetListener;
import edu.ualberta.med.biobank.gui.common.widgets.MultiSelectEvent;
import edu.ualberta.med.biobank.model.Center;
import edu.ualberta.med.biobank.model.HasName;
import edu.ualberta.med.biobank.model.Membership;
import edu.ualberta.med.biobank.model.PermissionEnum;
import edu.ualberta.med.biobank.model.Principal;
import edu.ualberta.med.biobank.model.Role;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.util.NullHelper;
import edu.ualberta.med.biobank.widgets.multiselect.MultiSelectWidget;
import edu.ualberta.med.biobank.widgets.trees.permission.PermissionCheckTreeWidget;
import edu.ualberta.med.biobank.widgets.trees.permission.PermissionCheckTreeWidget.PermissionTreeRes;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class MembershipEditDialog extends BgcBaseDialog {
    private final static NamedOption<Center> ALL_CENTERS_OPTION =
        new NamedOption<Center>(null, "All Centers");
    private final static NamedOption<Study> ALL_STUDIES_OPTION =
        new NamedOption<Study>(null, "All Studies");

    private final String currentTitle;
    private final String titleAreaMessage;
    private final Membership membership;
    private final Membership originalMembership;
    private final ManagerContext context;

    private ComboViewer centersViewer;
    private ComboViewer studiesViewer;

    private MultiSelectWidget<Role> rolesWidget;
    private PermissionCheckTreeWidget permissionsTree;

    public MembershipEditDialog(Shell parent, Membership membership,
        ManagerContext context) {
        super(parent);

        this.membership = membership;
        this.originalMembership = new Membership();

        copyMembership(membership, originalMembership);

        this.context = context;

        if (membership.isNew()) {
            this.currentTitle = Messages.MembershipAddDialog_title;
            this.titleAreaMessage = Messages.MembershipAddDialog_description;
        } else {
            this.currentTitle = "Edit Roles and Permissions";
            this.titleAreaMessage =
                "Edit Roles and Permissions specific to a center and study";
        }
    }

    private static boolean isDomainTaken(Principal p, Center c, Study s) {
        for (Membership m : p.getMemberships()) {
            if (NullHelper.safeEquals(c, m.getCenter())
                && NullHelper.safeEquals(s, m.getStudy())) {
                return true;
            }
        }
        return false;
    }

    private static final void copyMembership(Membership src, Membership dst) {
        dst.setCenter(src.getCenter());
        dst.setStudy(src.getStudy());
        dst.setEveryPermission(src.isEveryPermission());
        dst.setUserManager(src.isUserManager());
        dst.getRoles().addAll(src.getRoles());
        dst.getPermissions().addAll(src.getPermissions());
    }

    @Override
    protected String getDialogShellTitle() {
        return currentTitle;
    }

    @Override
    protected String getTitleAreaMessage() {
        return titleAreaMessage;
    }

    @Override
    protected String getTitleAreaTitle() {
        return currentTitle;
    }

    @Override
    protected void createDialogAreaInternal(Composite parent)
        throws ApplicationException {
        Composite contents = new Composite(parent, SWT.NONE);
        contents.setLayout(new GridLayout(1, false));
        contents.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        TabFolder tb = new TabFolder(contents, SWT.TOP);
        GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
        tb.setLayoutData(gd);

        Composite centerStudyComp = createTabItem(tb,
            Messages.MembershipAddDialog_centerStudy_tab_title, 1);
        createCentersCombo(centerStudyComp);
        createStudysCombo(centerStudyComp);

        createRolesWidget(createTabItem(tb,
            Messages.MembershipAddDialog_roles_tab_title, 1));

        createPermissionWidgets(createTabItem(tb,
            Messages.MembershipAddDialog_permissions_tab_title, 1));
    }

    private void createCentersCombo(Composite contents)
        throws ApplicationException {
        Group groupComp = new Group(contents, SWT.SHADOW_IN);
        groupComp.setText(Messages.MembershipAddDialog_center_label);
        groupComp.setLayout(new GridLayout(2, false));
        groupComp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        centersViewer = createComboViewer(groupComp,
            Messages.MembershipAddDialog_selected_center_label,
            getCenterOptions(),
            new NamedOption<Center>(membership.getCenter()),
            null,
            null,
            new NamedOptionLabelProvider());

        centersViewer.addSelectionChangedListener(
            new ISelectionChangedListener() {
                @Override
                public void selectionChanged(SelectionChangedEvent event) {
                    membership.setCenter(getCenterSelection());
                    updateStudyOptions();
                }
            });
    }

    protected void createStudysCombo(Composite parent)
        throws ApplicationException {
        Group groupComp = new Group(parent, SWT.SHADOW_IN);
        groupComp.setText(Messages.MembershipAddDialog_study_label);
        groupComp.setLayout(new GridLayout(2, false));
        groupComp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        studiesViewer = createComboViewer(groupComp,
            Messages.MembershipAddDialog_selected_study_label,
            getStudyOptions(membership.getCenter()),
            new NamedOption<Study>(membership.getStudy()),
            null,
            null,
            new NamedOptionLabelProvider());

        studiesViewer.addFilter(new ViewerFilter() {
            @Override
            public boolean select(Viewer viewer, Object parentElement,
                Object element) {
                @SuppressWarnings("unchecked")
                Study study = ((NamedOption<Study>) element).getOption();
                boolean taken = isDomainTaken(membership.getPrincipal(),
                    getCenterSelection(), study);
                return !taken;
            }
        });

        studiesViewer.addSelectionChangedListener(
            new ISelectionChangedListener() {
                @Override
                public void selectionChanged(SelectionChangedEvent event) {
                    membership.setStudy(getStudySelection());
                    updateRoleAndPermissionOptions();
                }
            });
    }

    private Composite createTabItem(TabFolder tb, String title, int columns) {
        TabItem item = new TabItem(tb, SWT.NONE);
        item.setText(title);
        Composite contents = new Composite(tb, SWT.NONE);
        contents.setLayout(new GridLayout(columns, false));
        item.setControl(contents);
        return contents;
    }

    private void createRolesWidget(Composite contents)
        throws ApplicationException {
        GridData gd;
        rolesWidget = new MultiSelectWidget<Role>(contents, SWT.NONE,
            Messages.MembershipAddDialog_roles_available_label,
            Messages.MembershipAddDialog_roles_selected_label, 300) {
            @Override
            protected String getTextForObject(Role nodeObject) {
                return nodeObject.getName();
            }
        };

        rolesWidget.setSelections(
            new ArrayList<Role>(context.getRoles()),
            new ArrayList<Role>(membership.getRoles()));
        gd = (GridData) rolesWidget.getLayoutData();
        gd.horizontalSpan = 2;

        rolesWidget.addSelectionChangedListener(
            new BgcEntryFormWidgetListener() {
                @Override
                public void selectionChanged(MultiSelectEvent event) {
                    checkedRolesOrPermissionsSelected();
                    updatePermissionOptions();
                }
            });
    }

    protected void checkedRolesOrPermissionsSelected() {
        if (rolesWidget.getSelected().isEmpty()
            && !permissionsTree.hasCheckedItems())
            setErrorMessage(Messages.MembershipAddDialog_role_error_msg);
        else
            setErrorMessage(null);
    }

    private void createPermissionWidgets(Composite contents) {
        permissionsTree = new PermissionCheckTreeWidget(contents, false,
            PermissionEnum.valuesList());
        permissionsTree.setSelections(membership.getPermissions());

        GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
        gd.horizontalSpan = 2;
        permissionsTree.setLayoutData(gd);

        permissionsTree.addCheckedListener(new ICheckStateListener() {
            @Override
            public void checkStateChanged(CheckStateChangedEvent event) {
                if (!event.getChecked()) {
                    checkedRolesOrPermissionsSelected();
                }
            }
        });
    }

    @Override
    protected void okPressed() {
        if (rolesWidget.getSelected().isEmpty()
            && !permissionsTree.hasCheckedItems()) {
            checkedRolesOrPermissionsSelected();
            return;
        }

        membership.getRoles().removeAll(rolesWidget.getRemovedFromSelection());
        membership.getRoles().addAll(rolesWidget.getAddedToSelection());

        PermissionTreeRes res = permissionsTree.getAddedAndRemovedNodes();
        membership.getPermissions().removeAll(res.removedPermissions);
        membership.getPermissions().addAll(res.addedPermissions);
        super.okPressed();
    }

    private Study getStudySelection() {
        ISelection sel = studiesViewer.getSelection();
        if (sel != null) {
            IStructuredSelection structSel = (IStructuredSelection) sel;
            if (structSel.size() > 0) {
                Object selection = structSel.getFirstElement();
                @SuppressWarnings("unchecked")
                Study study = ((NamedOption<Study>) selection).getOption();
                return study;
            }
        }
        return null;
    }

    private Center getCenterSelection() {
        ISelection sel = centersViewer.getSelection();
        if (sel != null) {
            IStructuredSelection structSel = (IStructuredSelection) sel;
            if (structSel.size() > 0) {
                Object selection = structSel.getFirstElement();
                @SuppressWarnings("unchecked")
                Center center = ((NamedOption<Center>) selection).getOption();
                return center;
            }
        }
        return null;
    }

    @Override
    protected void cancelPressed() {
        copyMembership(originalMembership, membership);
        super.cancelPressed();
    }

    private Set<NamedOption<Center>> getCenterOptions() {
        Set<NamedOption<Center>> options = new HashSet<NamedOption<Center>>();
        for (Membership m : context.getManager().getAllMemberships()) {
            if (m.getCenter() == null) {
                options.add(ALL_CENTERS_OPTION);
                for (Center c : context.getCenters()) {
                    options.add(new NamedOption<Center>(c));
                }
                break;
            } else {
                options.add(new NamedOption<Center>(m.getCenter()));
            }
        }
        return options;
    }

    private void updateStudyOptions() {
        studiesViewer.setInput(getStudyOptions(membership.getCenter()));
    }

    private void updatePermissionOptions() {
        // TODO: updating of Permission options
    }

    private void updateRoleAndPermissionOptions() {

    }

    public Set<NamedOption<Study>> getStudyOptions(Center selectedCenter) {
        Set<NamedOption<Study>> options = new HashSet<NamedOption<Study>>();

        for (Membership m : context.getManager().getAllMemberships()) {
            Center center = m.getCenter();
            if (center == null || center.equals(selectedCenter)) {
                if (m.getStudy() == null) {
                    options.add(ALL_STUDIES_OPTION);
                    for (Study s : context.getStudies()) {
                        options.add(new NamedOption<Study>(s));
                    }
                    break;
                } else {
                    options.add(new NamedOption<Study>(m.getStudy()));
                }
            }
        }
        return options;
    }

    static class NamedOptionLabelProvider extends LabelProvider {
        @Override
        public String getText(Object element) {
            return ((NamedOption<?>) element).getName();
        }
    }

    static class NamedOption<T extends HasName> implements
        Comparable<NamedOption<T>> {
        private final T option;
        private final String name;

        private NamedOption(T option) {
            this(option, option.getNameShort());
        }

        private NamedOption(T option, String name) {
            this.option = option;
            this.name = name;
        }

        public T getOption() {
            return option;
        }

        public String getName() {
            return name;
        }

        @Override
        public int compareTo(NamedOption<T> that) {
            return NullHelper.safeCompareTo(name, that.name);
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result =
                prime * result + ((option == null) ? 0 : option.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            NamedOption<?> other = (NamedOption<?>) obj;
            if (option == null) {
                if (other.option != null) return false;
            } else if (!option.equals(other.option)) return false;
            return true;
        }
    }
}