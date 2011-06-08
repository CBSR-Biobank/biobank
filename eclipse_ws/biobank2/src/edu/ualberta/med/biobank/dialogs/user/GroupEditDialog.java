package edu.ualberta.med.biobank.dialogs.user;

import java.util.ArrayList;
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

import edu.ualberta.med.biobank.Messages;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.security.Group;
import edu.ualberta.med.biobank.common.security.ProtectionGroupPrivilege;
import edu.ualberta.med.biobank.common.wrappers.CenterWrapper;
import edu.ualberta.med.biobank.gui.common.BiobankGuiCommonPlugin;
import edu.ualberta.med.biobank.gui.common.dialogs.BiobankDialog;
import edu.ualberta.med.biobank.gui.common.validators.NonEmptyStringValidator;
import edu.ualberta.med.biobank.gui.common.widgets.BiobankText;
import edu.ualberta.med.biobank.server.applicationservice.BiobankSecurityUtil;
import edu.ualberta.med.biobank.widgets.multiselect.MultiSelectWidget;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class GroupEditDialog extends BiobankDialog {
    public static final int CLOSE_PARENT_RETURN_CODE = 3;
    private final String currentTitle;
    private final String titleAreaMessage;
    private static final String MSG_NAME_REQUIRED = Messages
        .getString("GroupEditDialog.msg.name.required"); //$NON-NLS-1$
    private static final String GROUP_PERSIST_ERROR_TITLE = Messages
        .getString("GroupEditDialog.msg.persit.error"); //$NON-NLS-1$

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
            currentTitle = Messages.getString("GroupEditDialog.title.add"); //$NON-NLS-1$
            titleAreaMessage = Messages
                .getString("GroupEditDialog.titlearea.add"); //$NON-NLS-1$
        } else {
            currentTitle = Messages.getString("GroupEditDialog.title.edit"); //$NON-NLS-1$
            titleAreaMessage = Messages
                .getString("GroupEditDialog.titlearea.modify"); //$NON-NLS-1$
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

        createBoundWidgetWithLabel(contents, BiobankText.class, SWT.BORDER,
            Messages.getString("GroupEditDialog.property.title.name"), null, //$NON-NLS-1$ 
            modifiedGroup, "name", new NonEmptyStringValidator( //$NON-NLS-1$
                MSG_NAME_REQUIRED));

        final Button isCenterAdministratorCheckBox = (Button) createBoundWidgetWithLabel(
            contents, Button.class, SWT.CHECK,
            Messages.getString("GroupEditDialog.center.administrator.title"),
            null, modifiedGroup, "isWorkingCentersAdministrator", null);
        isCenterAdministratorCheckBox
            .addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    setCenterAdministrator();
                }
            });
        isCenterAdministratorCheckBox.setToolTipText(Messages
            .getString("GroupEditDialog.center.administrator.tooltip"));

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
            Messages.getString("GroupEditDialog.center.list.available"), //$NON-NLS-1$ 
            Messages.getString("GroupEditDialog.center.list.working"), 75); //$NON-NLS-1$
        workingCentersWidget.setSelections(centerMap,
            modifiedGroup.getWorkingCenterIds());

        centerFeaturesWidget = createFeaturesSelectionWidget(
            parent,
            SessionManager.getAppService().getSecurityCenterFeatures(),
            modifiedGroup.getCenterFeaturesEnabled(),
            BiobankSecurityUtil.CENTER_FEATURE_START_NAME,
            Messages.getString("GroupEditDialog.feature.center.list.available"), //$NON-NLS-1$
            Messages.getString("GroupEditDialog.feature.center.list.selected")); //$NON-NLS-1$
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
                pgp.getName().replace(replaceString, ""));
        }
        MultiSelectWidget featuresWidget = new MultiSelectWidget(parent,
            SWT.NONE, availableString, enabledString, 75); //$NON-NLS-1$
        featuresWidget.setSelections(featuresMap, selectedFeatures);
        return featuresWidget;
    }

    private List<CenterWrapper<?>> getAllCenters() {
        if (allCenters == null) {
            try {
                allCenters = CenterWrapper.getCenters(SessionManager
                    .getAppService());
            } catch (Exception e) {
                BiobankGuiCommonPlugin
                    .openAsyncError(
                        Messages
                            .getString("GroupEditDialog.msg.error.retrieve.centers"), e); //$NON-NLS-1$
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
                BiobankGuiCommonPlugin.openAsyncError(
                    GROUP_PERSIST_ERROR_TITLE,
                    Messages.getString("GroupEditDialog.msg.error.name.used")); //$NON-NLS-1$
            } else {
                BiobankGuiCommonPlugin.openAsyncError(
                    GROUP_PERSIST_ERROR_TITLE, e);
            }
        }
    }
}