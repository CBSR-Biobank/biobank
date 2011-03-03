package edu.ualberta.med.biobank.dialogs;

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

import edu.ualberta.med.biobank.BiobankPlugin;
import edu.ualberta.med.biobank.Messages;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.security.Group;
import edu.ualberta.med.biobank.common.security.ProtectionGroupPrivilege;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.validators.NonEmptyStringValidator;
import edu.ualberta.med.biobank.widgets.BiobankText;
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
    private MultiSelectWidget readOnlySitesWidget;
    private MultiSelectWidget canUpdateSitesWidget;
    private List<SiteWrapper> allSites;
    private MultiSelectWidget featuresWidget;

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

        final Button isSiteAdministratorCheckBox = (Button) createBoundWidgetWithLabel(
            contents, Button.class, SWT.CHECK,
            Messages.getString("GroupEditDialog.site.administrator.title"),
            null, modifiedGroup, "isSiteAdministrator", null);
        isSiteAdministratorCheckBox
            .addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    widgetCreator.showWidget(featuresWidget,
                        !isSiteAdministratorCheckBox.getSelection());
                }
            });
        isSiteAdministratorCheckBox
            .setToolTipText("All features are enabled when a group is site administrator");

        List<String> siteNames = new ArrayList<String>();
        final LinkedHashMap<Integer, String> siteMap = new LinkedHashMap<Integer, String>();
        if (getAllSites() != null)
            for (SiteWrapper site : getAllSites()) {
                Integer siteId = site.getId();
                String siteName = site.getNameShort();
                siteNames.add(siteName);
                siteMap.put(siteId, siteName);
            }
        readOnlySitesWidget = new MultiSelectWidget(parent, SWT.NONE,
            Messages.getString("GroupEditDialog.site.list.readonly"), //$NON-NLS-1$
            Messages.getString("GroupEditDialog.site.list.available"), 75); //$NON-NLS-1$ 
        readOnlySitesWidget.setSelections(siteMap,
            modifiedGroup.getReadOnlySites());

        canUpdateSitesWidget = new MultiSelectWidget(parent, SWT.NONE,
            Messages.getString("GroupEditDialog.site.list.canUpdate"), //$NON-NLS-1$ 
            Messages.getString("GroupEditDialog.site.list.available"), 75); //$NON-NLS-1$
        canUpdateSitesWidget.setSelections(siteMap,
            modifiedGroup.getCanUpdateSites());

        List<ProtectionGroupPrivilege> features = SessionManager
            .getAppService().getSecurityFeatures();
        final LinkedHashMap<Integer, String> featuresMap = new LinkedHashMap<Integer, String>();
        for (ProtectionGroupPrivilege pgp : features) {
            featuresMap.put(pgp.getId().intValue(), pgp.getName());
        }
        featuresWidget = new MultiSelectWidget(parent, SWT.NONE,
            Messages.getString("GroupEditDialog.feature.list.selected"), //$NON-NLS-1$
            Messages.getString("GroupEditDialog.feature.list.available"), 75); //$NON-NLS-1$ 
        featuresWidget.setSelections(featuresMap,
            modifiedGroup.getFeaturesEnabled());
    }

    private List<SiteWrapper> getAllSites() {
        if (allSites == null) {
            try {
                allSites = SiteWrapper.getSites(SessionManager.getAppService());
            } catch (Exception e) {
                BiobankPlugin.openAsyncError(Messages
                    .getString("GroupEditDialog.msg.error.retrieve.sites"), e); //$NON-NLS-1$
            }
        }
        return allSites;
    }

    @Override
    protected void okPressed() {
        // try saving or updating the group inside this dialog so that if there
        // is an error the entered information is not lost
        try {
            modifiedGroup.setCanUpdateSites(canUpdateSitesWidget.getSelected());
            modifiedGroup.setReadOnlySites(readOnlySitesWidget.getSelected());
            modifiedGroup.setFeaturesEnabled(featuresWidget.getSelected());
            Group groupeResult = SessionManager.getAppService().persistGroup(
                modifiedGroup);
            originalGroup.copy(groupeResult);
            close();
        } catch (ApplicationException e) {
            if (e.getMessage().contains("Duplicate entry")) { //$NON-NLS-1$
                BiobankPlugin.openAsyncError(GROUP_PERSIST_ERROR_TITLE,
                    Messages.getString("GroupEditDialog.msg.error.name.used")); //$NON-NLS-1$
            } else {
                BiobankPlugin.openAsyncError(GROUP_PERSIST_ERROR_TITLE, e);
            }
        }
    }
}