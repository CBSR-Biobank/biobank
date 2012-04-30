package edu.ualberta.med.biobank.dialogs.user;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ICheckable;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Listener;

import edu.ualberta.med.biobank.common.action.security.ManagerContext;
import edu.ualberta.med.biobank.gui.common.dialogs.BgcWizardPage;
import edu.ualberta.med.biobank.gui.common.widgets.BgcEntryFormWidgetListener;
import edu.ualberta.med.biobank.gui.common.widgets.MultiSelectEvent;
import edu.ualberta.med.biobank.model.Membership;
import edu.ualberta.med.biobank.model.PermissionEnum;
import edu.ualberta.med.biobank.model.Role;
import edu.ualberta.med.biobank.widgets.multiselect.MultiSelectWidget;
import edu.ualberta.med.biobank.widgets.trees.permission.PermissionCheckTreeWidget;
import edu.ualberta.med.biobank.widgets.trees.permission.PermissionNode;

public class MembershipPermissionsPage extends BgcWizardPage {
    // track the permissions and roles the the user explicitly clicks on and off
    // (as opposed to automatically removed by switching domains)
    private final Set<PermissionEnum> explicitPerms =
        new HashSet<PermissionEnum>();
    private final Set<Role> explicitRoles = new HashSet<Role>();
    private final PermissionsCheckStateHandler permissionsCheckStateHandler =
        new PermissionsCheckStateHandler();
    private final RolesSelectionHandler rolesSelectionHandler =
        new RolesSelectionHandler();
    private final Membership membership;
    private final ManagerContext context;

    private Button userManagerButton;
    private Button everyPermissionButton;
    private MultiSelectWidget<Role> rolesWidget;
    private PermissionCheckTreeWidget permissionsTree;

    private final WritableValue validPermissions = new WritableValue(
        Boolean.FALSE, Boolean.class);

    MembershipPermissionsPage(Membership membership, ManagerContext context) {
        super("", "Roles and Permissions", null);

        setMessage("What the user (or group) is allowed to do");

        this.membership = membership;
        this.context = context;

        explicitPerms.addAll(membership.getPermissions());
        explicitRoles.addAll(membership.getRoles());
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);

        updateEveryPermissionButton();
        updateUserManagerButton();
        updateRoleSelections();
        updatePermissionSelections();
    }

    @Override
    protected void createDialogAreaInternal(Composite parent) throws Exception {
        Composite container = new Composite(parent, SWT.NONE);
        container.setLayout(new GridLayout(1, false));
        container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        createEveryPermissionButton(container);
        createUserManagerButton(container);

        Group rolesGroup = createGroup(container, "Roles");
        createRolesWidget(rolesGroup);
        updateRoleSelections();

        createPermissionsTree(container);
        updatePermissionSelections();

        createPermissionsValidation("Select at least one permission or role (with permissions) to grant");

        rolesWidget.addSelectionChangedListener(rolesSelectionHandler);
        permissionsTree.addCheckStateListener(permissionsCheckStateHandler);

        setControl(container);
    }

    private void createPermissionsValidation(String message) {
        WritableValue dummy = new WritableValue(Boolean.FALSE, Boolean.class);
        getWidgetCreator().addBooleanBinding(dummy, validPermissions, message);
    }

    private void updatePermissionSelections() {
        permissionsTree.setEnabled(!membership.isEveryPermission());

        Set<PermissionEnum> options = getPermissionOptions();
        Set<PermissionEnum> selected =
            new HashSet<PermissionEnum>(explicitPerms);
        selected.retainAll(options);

        // don't listen to changes the user isn't making
        permissionsTree.removeCheckStateListener(permissionsCheckStateHandler);

        try {
            // select and disable options that the selected roles include, see
            // http://tom-eclipse-dev.blogspot.ca/2007/01/tableviewers-and-nativelooking.html
            for (Role role : membership.getRoles()) {
                selected.addAll(role.getPermissions());
            }

            permissionsTree.setInput(options);
            permissionsTree.setSelections(selected);

            membership.getPermissions().clear();
            membership.getPermissions().addAll(selected);

            updatePageComplete();
        } finally {
            permissionsTree.addCheckStateListener(permissionsCheckStateHandler);
        }
    }

    private Set<PermissionEnum> getPermissionOptions() {
        Set<PermissionEnum> options = new HashSet<PermissionEnum>();
        for (Membership managerMemb : context.getManager().getAllMemberships()) {
            if (membership.isManageable(managerMemb)) {
                if (managerMemb.isEveryPermission()) {
                    options.addAll(PermissionEnum.valuesList());
                    break;
                }
                options.addAll(managerMemb.getAllPermissions());
            }
        }
        // remove options that do not meet requirements
        Iterator<PermissionEnum> it = options.iterator();
        while (it.hasNext()) {
            PermissionEnum permission = it.next();
            if (!permission.isRequirementsMet(membership)) {
                it.remove();
            }
        }
        return options;
    }

    private void updateRoleSelections() {
        rolesWidget.setEnabled(!membership.isEveryPermission());

        Set<Role> options = getRoleOptions();
        Set<Role> selected = new HashSet<Role>(explicitRoles);
        selected.retainAll(options);

        // don't listen to changes the user isn't making
        rolesWidget.removeSelectionChangedListener(rolesSelectionHandler);

        try {
            rolesWidget.setSelections(options, selected);

            membership.getRoles().clear();
            membership.getRoles().addAll(selected);

            updatePageComplete();
        } finally {
            rolesWidget.addSelectionChangedListener(rolesSelectionHandler);
        }
    }

    private Set<Role> getRoleOptions() {
        Set<Role> options = new HashSet<Role>();
        for (Membership managerMemb : context.getManager().getAllMemberships()) {
            if (membership.isManageable(managerMemb)) {
                if (managerMemb.isEveryPermission()) {
                    options.addAll(context.getRoles());
                    break;
                }
                options.addAll(managerMemb.getRoles());
            }
        }
        return options;
    }

    private void createUserManagerButton(Composite parent) {
        userManagerButton = new Button(parent, SWT.CHECK);
        userManagerButton.setText("Can create users");
        userManagerButton
            .setToolTipText("Can manage, create, edit, and delete users and groups (for the previously selected centers and studies).");

        GridData gd = new GridData();
        gd.horizontalIndent = 20;
        userManagerButton.setLayoutData(gd);

        userManagerButton.setSelection(membership.isUserManager());
        userManagerButton.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                boolean userManager = userManagerButton.getSelection();
                membership.setUserManager(userManager);
            }
        });
    }

    private void createEveryPermissionButton(Composite parent) {
        everyPermissionButton = new Button(parent, SWT.CHECK);
        everyPermissionButton.setText("Grant all permissions and roles");
        everyPermissionButton
            .setToolTipText("Grant all current and future roles and permissions");
        everyPermissionButton.setSelection(membership.isEveryPermission());
        everyPermissionButton.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                boolean everyPermission = everyPermissionButton.getSelection();
                membership.setEveryPermission(everyPermission);

                updateUserManagerButton();

                rolesWidget.setEnabled(!everyPermission);
                permissionsTree.setEnabled(!everyPermission);

                updatePageComplete();
            }
        });
    }

    private Group createGroup(Composite parent, String title) {
        Group group = new Group(parent, SWT.SHADOW_IN);
        group.setText(title);
        group.setLayout(new GridLayout(2, false));
        group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        return group;
    }

    private void createRolesWidget(Composite parent) {
        rolesWidget = new MultiSelectWidget<Role>(parent, SWT.NONE,
            "Available Roles",
            "Selected Roles", 100) {
            @Override
            protected String getTextForObject(Role role) {
                return role.getName();
            }
        };
    }

    private void createPermissionsTree(Composite parent) {
        permissionsTree = new PermissionCheckTreeWidget(parent, true,
            PermissionEnum.valuesList());

        GridLayout gl = new GridLayout(2, false);
        gl.marginWidth = 5;
        gl.marginHeight = 0;
        gl.marginTop = 10;
        gl.horizontalSpacing = 10;
        gl.verticalSpacing = 5;

        GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
        gd.heightHint = 160;

        permissionsTree.setLayout(gl);
        permissionsTree.setLayoutData(gd);
    }

    private void updateEveryPermissionButton() {
        boolean canGrantEveryPermission = false;
        for (Membership m : context.getManager().getAllMemberships()) {
            if (m.isUserManager() && m.isEveryPermission()) {
                if (m.getDomain().isSuperset(membership.getDomain())) {
                    canGrantEveryPermission = true;
                    break;
                }
            }
        }
        if (!canGrantEveryPermission) {
            everyPermissionButton.setSelection(false);
        }
        everyPermissionButton.setEnabled(canGrantEveryPermission);

        updateUserManagerButton();
    }

    private void updateUserManagerButton() {
        boolean everyPermission = membership.isEveryPermission();
        userManagerButton.setEnabled(everyPermission);
        if (!everyPermission) userManagerButton.setSelection(false);
    }

    private void updatePageComplete() {
        boolean permsSelected = !membership.getAllPermissions().isEmpty();
        validPermissions.setValue(permsSelected);

        setPageComplete(permsSelected);
    }

    private class RolesSelectionHandler implements BgcEntryFormWidgetListener {
        @Override
        public void selectionChanged(MultiSelectEvent event) {
            List<Role> selected = rolesWidget.getSelected();

            explicitRoles.clear();
            explicitRoles.addAll(selected);

            membership.getRoles().clear();
            membership.getRoles().addAll(selected);

            updatePermissionSelections();
        }
    }

    private class PermissionsCheckStateHandler implements ICheckStateListener {
        @Override
        public void checkStateChanged(CheckStateChangedEvent event) {
            Object element = event.getElement();
            if (!(element instanceof PermissionNode)) return;

            PermissionNode node = (PermissionNode) event.getElement();
            PermissionEnum perm = node.getPermission();

            ICheckable checkable = event.getCheckable();
            if (isInRole(perm)) {
                checkable.setChecked(element, true);
                return;
            }

            if (event.getChecked()) {
                explicitPerms.add(perm);
                membership.getPermissions().add(perm);
            } else {
                explicitPerms.remove(perm);
                membership.getPermissions().remove(perm);
            }

            updatePageComplete();
        }

        private boolean isInRole(Object o) {
            for (Role role : membership.getRoles())
                if (role.getPermissions().contains(o)) return true;
            return false;
        }
    }
}
