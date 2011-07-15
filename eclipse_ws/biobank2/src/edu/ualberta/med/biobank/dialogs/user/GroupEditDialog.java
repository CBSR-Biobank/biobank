package edu.ualberta.med.biobank.dialogs.user;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.security.Group;
import edu.ualberta.med.biobank.common.security.ProtectionGroupPrivilege;
import edu.ualberta.med.biobank.common.wrappers.CenterWrapper;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.gui.common.dialogs.BgcBaseDialog;
import edu.ualberta.med.biobank.gui.common.validators.NonEmptyStringValidator;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseText;
import edu.ualberta.med.biobank.server.applicationservice.BiobankSecurityUtil;
import edu.ualberta.med.biobank.widgets.multiselect.MultiSelectWidget;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class GroupEditDialog extends BgcBaseDialog {
    public static final int CLOSE_PARENT_RETURN_CODE = 3;
    private final String currentTitle;
    private final String titleAreaMessage;

    private Group originalGroup, modifiedGroup;
    private MultiSelectWidget workingCentersWidget;
    private List<CenterWrapper<?>> allCenters;
    private MultiSelectWidget centerFeaturesWidget;

    public GroupEditDialog(Shell parent, Group originalGroup, boolean isNewGroup) {
        super(parent);
        Assert.isNotNull(originalGroup);
        this.originalGroup = originalGroup;
        this.modifiedGroup = new Group();
        this.modifiedGroup.copy(originalGroup);
        if (isNewGroup) {
            currentTitle = Messages.GroupEditDialog_title_add;
            titleAreaMessage = Messages.GroupEditDialog_titlearea_add;
        } else {
            currentTitle = Messages.GroupEditDialog_title_edit;
            titleAreaMessage = Messages.GroupEditDialog_titlearea_modify;
        }
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
            Messages.GroupEditDialog_property_title_name, null, modifiedGroup,
            "name", new NonEmptyStringValidator( //$NON-NLS-1$
                Messages.GroupEditDialog_msg_name_required));

        final Button isCenterAdministratorCheckBox = (Button) createBoundWidgetWithLabel(
            contents, Button.class, SWT.CHECK,
            Messages.GroupEditDialog_center_administrator_title, null,
            modifiedGroup, "isWorkingCentersAdministrator", null); //$NON-NLS-1$
        isCenterAdministratorCheckBox
            .addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    setCenterAdministrator();
                }
            });
        isCenterAdministratorCheckBox
            .setToolTipText(Messages.GroupEditDialog_center_administrator_tooltip);

        List<String> centerNames = new ArrayList<String>();
        final LinkedHashMap<Integer, String> centerMap = new LinkedHashMap<Integer, String>();
        if (getAllCenters() != null)
            for (CenterWrapper<?> center : getAllCenters()) {
                Integer centerId = center.getId();
                String centerName = center.getNameShort();
                centerNames.add(centerName);
                centerMap.put(centerId, centerName);
            }

        workingCentersWidget = new MultiSelectWidget(parent, SWT.NONE,
            Messages.GroupEditDialog_center_list_available,
            Messages.GroupEditDialog_center_list_working, 75);
        workingCentersWidget.setSelections(centerMap,
            modifiedGroup.getWorkingCenterIds());

        centerFeaturesWidget = createFeaturesSelectionWidget(
            parent,
            SessionManager.getAppService().getSecurityCenterFeatures(
                SessionManager.getUser()),
            modifiedGroup.getCenterFeaturesEnabled(),
            BiobankSecurityUtil.CENTER_FEATURE_START_NAME,
            Messages.GroupEditDialog_feature_center_list_available,
            Messages.GroupEditDialog_feature_center_list_selected);
        setCenterAdministrator();
    }

    protected void setCenterAdministrator() {
        boolean centerAdministrator = modifiedGroup
            .getIsWorkingCentersAdministrator();
        centerFeaturesWidget.setEnabled(!centerAdministrator);
        if (centerAdministrator)
            centerFeaturesWidget.selectAll();
        else
            centerFeaturesWidget.setSelection(modifiedGroup
                .getCenterFeaturesEnabled());
    }

    private MultiSelectWidget createFeaturesSelectionWidget(Composite parent,
        List<ProtectionGroupPrivilege> availableFeatures,
        List<Integer> selectedFeatures, String replaceString,
        String availableString, String enabledString) {
        final LinkedHashMap<Integer, String> featuresMap = new LinkedHashMap<Integer, String>();
        for (ProtectionGroupPrivilege pgp : availableFeatures) {
            featuresMap.put(pgp.getId().intValue(),
                pgp.getName().replace(replaceString, "")); //$NON-NLS-1$
        }
        MultiSelectWidget featuresWidget = new MultiSelectWidget(parent,
            SWT.NONE, availableString, enabledString, 75); //$NON-NLS-1$
        featuresWidget.setSelections(featuresMap, selectedFeatures);
        return featuresWidget;
    }

    private List<CenterWrapper<?>> getAllCenters() {
        if (allCenters == null) {
            if (!SessionManager.getUser().isSuperAdministrator())
                allCenters = Arrays
                    .asList(new CenterWrapper<?>[] { SessionManager.getUser()
                        .getCurrentWorkingCenter() });
            else
                try {
                    allCenters = CenterWrapper.getCenters(SessionManager
                        .getAppService());
                } catch (Exception e) {
                    BgcPlugin.openAsyncError(
                        Messages.GroupEditDialog_msg_error_retrieve_centers, e);
                }
        }
        return allCenters;
    }

    @Override
    protected void okPressed() {
        // try saving or updating the group inside this dialog so that if there
        // is an error the entered information is not lost
        try {
            modifiedGroup.setWorkingCenterIds(workingCentersWidget
                .getSelected());
            if (modifiedGroup.getIsWorkingCentersAdministrator())
                modifiedGroup
                    .setCenterFeaturesEnabled(new ArrayList<Integer>());
            else
                modifiedGroup.setCenterFeaturesEnabled(centerFeaturesWidget
                    .getSelected());
            Group groupeResult = SessionManager.getAppService().persistGroup(
                modifiedGroup);
            originalGroup.copy(groupeResult);
            close();
        } catch (ApplicationException e) {
            if (e.getMessage().contains("Duplicate entry")) { //$NON-NLS-1$
                BgcPlugin.openAsyncError(
                    Messages.GroupEditDialog_msg_persit_error,
                    Messages.GroupEditDialog_msg_error_name_used);
            } else {
                BgcPlugin.openAsyncError(
                    Messages.GroupEditDialog_msg_persit_error, e);
            }
        }
    }
}