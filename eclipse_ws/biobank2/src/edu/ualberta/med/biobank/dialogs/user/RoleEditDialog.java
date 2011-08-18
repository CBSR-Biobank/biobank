package edu.ualberta.med.biobank.dialogs.user;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.peer.RolePeer;
import edu.ualberta.med.biobank.common.security.ProtectionGroupPrivilege;
import edu.ualberta.med.biobank.common.wrappers.CenterWrapper;
import edu.ualberta.med.biobank.common.wrappers.RoleWrapper;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.gui.common.dialogs.BgcBaseDialog;
import edu.ualberta.med.biobank.gui.common.validators.NonEmptyStringValidator;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseText;
import edu.ualberta.med.biobank.widgets.multiselect.MultiSelectWidget;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class RoleEditDialog extends BgcBaseDialog {
    private final String currentTitle;
    private final String titleAreaMessage;

    private RoleWrapper originalRole, modifiedRole;
    private MultiSelectWidget workingCentersWidget;
    private List<CenterWrapper<?>> allCenters;
    private MultiSelectWidget centerFeaturesWidget;
    private Text centersFilterText;
    private LinkedHashMap<Integer, String> allFeaturesMap;

    public RoleEditDialog(Shell parent, RoleWrapper originalRole) {
        super(parent);
        Assert.isNotNull(originalRole);
        this.originalRole = originalRole;
        this.modifiedRole = new RoleWrapper(null);
        copyRole(originalRole, modifiedRole);
        if (originalRole.isNew()) {
            currentTitle = Messages.RoleEditDialog_title_add;
            titleAreaMessage = Messages.RoleEditDialog_titlearea_add;
        } else {
            currentTitle = Messages.RoleEditDialog_title_edit;
            titleAreaMessage = Messages.RoleEditDialog_titlearea_modify;
        }
    }

    private void copyRole(RoleWrapper src, RoleWrapper dest) {
        dest.setName(src.getName());
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
        contents.setLayout(new GridLayout(2, false));
        contents.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        createBoundWidgetWithLabel(contents, BgcBaseText.class, SWT.BORDER,
            Messages.RoleEditDialog_property_title_name, null, modifiedRole,
            RolePeer.NAME.getName(), new NonEmptyStringValidator( //$NON-NLS-1$
                Messages.RoleEditDialog_msg_name_required));

        // createBoundWidgetWithLabel(contents, Button.class, SWT.CHECK,
        // Messages.GroupEditDialog_center_administrator_title, null,
        //            modifiedGroup, "isWorkingCentersAdministrator", null); //$NON-NLS-1$

        // List<String> centerNames = new ArrayList<String>();
        // final LinkedHashMap<Integer, String> centerMap = new
        // LinkedHashMap<Integer, String>();
        // if (getAllCenters() != null)
        // for (CenterWrapper<?> center : getAllCenters()) {
        // Integer centerId = center.getId();
        // String centerName = center.getNameShort();
        // centerNames.add(centerName);
        // centerMap.put(centerId, centerName);
        // }
        //
        // workingCentersWidget = new MultiSelectWidget(parent, SWT.NONE,
        // Messages.GroupEditDialog_center_list_available,
        // Messages.GroupEditDialog_center_list_working, 110);
        // workingCentersWidget.setSelections(centerMap,
        // modifiedRole.getWorkingCenterIds());
        // workingCentersWidget.setFilter(new ViewerFilter() {
        // @Override
        // public boolean select(Viewer viewer, Object parentElement,
        // Object element) {
        // if (centersFilterText == null)
        // return true;
        // MultiSelectNode node = (MultiSelectNode) element;
        // return TableFilter.contains(node.getName(),
        // centersFilterText.getText());
        // }
        // });
        // Label label = new Label(parent, SWT.NONE);
        // label.setText(Messages.GroupEditDialog_filter_centers);
        // centersFilterText = new Text(parent, SWT.BORDER);
        // GridData gd = new GridData(SWT.FILL, SWT.NONE, true, false);
        // centersFilterText.setLayoutData(gd);
        // centersFilterText.addModifyListener(new ModifyListener() {
        // @Override
        // public void modifyText(ModifyEvent e) {
        // workingCentersWidget.refreshLists();
        // }
        // });
        //
        // Label separator = new Label(parent, SWT.HORIZONTAL | SWT.SEPARATOR);
        // gd = new GridData();
        // gd.horizontalAlignment = SWT.CENTER;
        // gd.widthHint = 250;
        // separator.setLayoutData(gd);
        //
        // centerFeaturesWidget = createFeaturesSelectionWidget(
        // parent,
        // SessionManager.getAppService().getSecurityCenterFeatures(
        // SessionManager.getUserOld()),
        // modifiedRole.getCenterFeaturesEnabled(),
        // BiobankSecurityUtil.CENTER_FEATURE_START_NAME,
        // Messages.GroupEditDialog_feature_center_list_available,
        // Messages.GroupEditDialog_feature_center_list_selected);
    }

    private MultiSelectWidget createFeaturesSelectionWidget(Composite parent,
        List<ProtectionGroupPrivilege> availableFeatures,
        List<Integer> selectedFeatures, String replaceString,
        String availableString, String enabledString) {
        allFeaturesMap = new LinkedHashMap<Integer, String>();
        for (ProtectionGroupPrivilege pgp : availableFeatures) {
            allFeaturesMap.put(pgp.getId().intValue(),
                pgp.getName().replace(replaceString, "")); //$NON-NLS-1$
        }
        MultiSelectWidget featuresWidget = new MultiSelectWidget(parent,
            SWT.NONE, availableString, enabledString, 110);
        featuresWidget.setSelections(allFeaturesMap, selectedFeatures);
        return featuresWidget;
    }

    private List<CenterWrapper<?>> getAllCenters() {
        if (allCenters == null) {
            if (!SessionManager.getUser().isSuperAdmin())
                allCenters = Arrays
                    .asList(new CenterWrapper<?>[] { SessionManager.getUser()
                        .getCurrentWorkingCenter() });
            else
                try {
                    allCenters = CenterWrapper.getCenters(SessionManager
                        .getAppService());
                } catch (Exception e) {
                    BgcPlugin.openAsyncError(
                        Messages.RoleEditDialog_msg_error_retrieve_centers, e);
                }
        }
        return allCenters;
    }

    @Override
    protected void okPressed() {
        // try saving or updating the group inside this dialog so that if there
        // is an error the entered information is not lost
        try {
            // modifiedRole
            // .setWorkingCenterIds(workingCentersWidget.getSelected());
            // modifiedRole.setCenterFeaturesEnabled(centerFeaturesWidget
            // .getSelected());
            copyRole(modifiedRole, originalRole);
            originalRole.persist();
            close();
        } catch (Exception e) {
            if (e.getMessage().contains("Duplicate entry")) { //$NON-NLS-1$
                BgcPlugin.openAsyncError(
                    Messages.RoleEditDialog_msg_persit_error,
                    Messages.RoleEditDialog_msg_error_name_used);
            } else {
                BgcPlugin.openAsyncError(
                    Messages.RoleEditDialog_msg_persit_error, e);
            }
        }
    }
}