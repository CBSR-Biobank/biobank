package edu.ualberta.med.biobank.dialogs.user;

import java.util.ArrayList;
import java.util.Map;

import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ICheckable;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

import edu.ualberta.med.biobank.common.action.security.ManagerContext;
import edu.ualberta.med.biobank.gui.common.dialogs.BgcWizardPage;
import edu.ualberta.med.biobank.gui.common.widgets.BgcEntryFormWidgetListener;
import edu.ualberta.med.biobank.gui.common.widgets.MultiSelectEvent;
import edu.ualberta.med.biobank.model.Membership;
import edu.ualberta.med.biobank.model.PermissionEnum;
import edu.ualberta.med.biobank.model.Role;
import edu.ualberta.med.biobank.widgets.multiselect.MultiSelectWidget;
import edu.ualberta.med.biobank.widgets.trees.permission.PermissionCheckTreeWidget;

public class MembershipRolesPage extends BgcWizardPage {
    // TODO: remember the explicitly checked set of PermissionEnum-s
    private final Map<PermissionEnum, Boolean> explicitlyChecked = null;
    private final PermissionsCheckStateHandler permissionsCheckStateHandler =
        new PermissionsCheckStateHandler();
    private final Membership membership;
    private final ManagerContext context;

    private MultiSelectWidget<Role> rolesWidget;
    private PermissionCheckTreeWidget permissionsTree;

    MembershipRolesPage(Membership membership, ManagerContext context) {
        super("Roles and Permissions",
            "Select roles and permissions for this membership", null);

        this.membership = membership;
        this.context = context;
    }

    @Override
    protected void createDialogAreaInternal(Composite parent) throws Exception {
        Composite container = new Composite(parent, SWT.NONE);
        container.setLayout(new GridLayout(1, false));
        container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        Group rolesGroup = createGroup(container, "Roles");
        createRolesWidget(rolesGroup);

        rolesWidget.setSelections(
            new ArrayList<Role>(context.getRoles()),
            new ArrayList<Role>(membership.getRoles()));

        rolesWidget.addSelectionChangedListener(
            new BgcEntryFormWidgetListener() {
                @Override
                public void selectionChanged(MultiSelectEvent event) {
                }
            });

//        Group permsGroup = createGroup(container, "Permissions");
        createPermissionsTree(container);

        permissionsTree.setSelections(membership.getPermissions());

        permissionsTree.addCheckedListener(permissionsCheckStateHandler);
        
        setControl(container);
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
            "Selected Roles", 80) {
            @Override
            protected String getTextForObject(Role role) {
                return role.getName();
            }
        };
    }
    
    private void createPermissionsTree(Composite parent) {
        permissionsTree = new PermissionCheckTreeWidget(parent, true,
            PermissionEnum.valuesList());
    }

    private void updatePageComplete() {
        boolean complete = !membership.getAllPermissions().isEmpty();
        setPageComplete(complete);
    }

    private class PermissionsCheckStateHandler implements ICheckStateListener {
        @Override
        public void checkStateChanged(CheckStateChangedEvent event) {
            Object element = event.getElement();
            ICheckable checkable = event.getCheckable();
            if (isInRole(element)) checkable.setChecked(element, true);
        }

        private boolean isInRole(Object o) {
            for (Role role : membership.getRoles()) 
                if (role.getPermissions().contains(o)) return true;
            return false;
        }
    }
}
