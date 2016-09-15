package edu.ualberta.med.biobank.dialogs.startup;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.osgi.service.prefs.Preferences;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.common.wrappers.CenterWrapper;
import edu.ualberta.med.biobank.common.wrappers.UserWrapper;
import edu.ualberta.med.biobank.gui.common.dialogs.PersistedDialog;
import edu.ualberta.med.biobank.gui.common.widgets.utils.ComboSelectionUpdate;
import edu.ualberta.med.biobank.rcp.Application;
import edu.ualberta.med.biobank.widgets.BiobankLabelProvider;

public class WorkingCenterSelectDialog extends PersistedDialog {
    private static final I18n i18n = I18nFactory.getI18n(WorkingCenterSelectDialog.class);

    @SuppressWarnings("nls")
    // TR: select working center title area title
    private static final String TITLE = i18n.tr("Working center selection");

    @SuppressWarnings("nls")
    // TR: select working center title area message
    private static final String TITLE_MSG = i18n.tr("Choose the center you will work with.");

    @SuppressWarnings("nls")
    private static final String NO_CENTER_STRING = i18n.tr("-- no center selection --");

    @SuppressWarnings("nls")
    private static final String LAST_WORKING_CENTER_ID = "lastWorkingCenterId";

    @SuppressWarnings("nls")
    // TR: no center selection combo box option
    private static final String COMBO_LABEL = i18n.tr("Select center");

    private final UserWrapper user;
    private CenterWrapper<?> currentCenter;
    private final List<CenterWrapper<?>> availableCenters;

    public Preferences pluginPrefs = null;

    public WorkingCenterSelectDialog(Shell parentShell, UserWrapper user,
        List<CenterWrapper<?>> availableCenters) {
        super(parentShell);
        this.user = user;
        this.availableCenters = availableCenters;
        pluginPrefs = InstanceScope.INSTANCE.getNode(Application.PLUGIN_ID);
    }

    @Override
    protected String getTitleAreaMessage() {
        return TITLE_MSG;
    }

    @Override
    protected String getTitleAreaTitle() {
        return TITLE;
    }

    @Override
    protected String getDialogShellTitle() {
        return TITLE;
    }

    @Override
    protected void createDialogAreaInternal(Composite parent) throws Exception {
        Composite contents = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(2, false);
        contents.setLayout(layout);
        contents.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        List<Object> objectList = new ArrayList<Object>(availableCenters);
        if (user.isSuperAdmin()) {
            objectList.add(NO_CENTER_STRING);
        }

        CenterWrapper<?> lastWorkingCentre = getLastUsedWorkingCenter(availableCenters);

        Object initialSelection = (lastWorkingCentre != null)
            ? lastWorkingCentre : NO_CENTER_STRING;

        widgetCreator.createComboViewer(contents, COMBO_LABEL,
            objectList, initialSelection, null, new ComboSelectionUpdate() {
                @Override
                public void doSelection(Object selectedObject) {
                    if (selectedObject instanceof CenterWrapper<?>) {
                        currentCenter = (CenterWrapper<?>) selectedObject;
                        pluginPrefs.putInt(LAST_WORKING_CENTER_ID, currentCenter.getId());
                    } else {
                        currentCenter = null;
                        pluginPrefs.putInt(LAST_WORKING_CENTER_ID, -1);
                    }
                }
            }, new BiobankLabelProvider() {
                @Override
                public String getText(Object element) {
                    if (element instanceof CenterWrapper) {
                        return ((CenterWrapper<?>) element).getNameShort();
                    }
                    return super.getText(element);
                }
            });
    }

    private CenterWrapper<?> getLastUsedWorkingCenter(List<CenterWrapper<?>> availableCenters) {
        int lastWorkingCentreId = pluginPrefs.getInt(LAST_WORKING_CENTER_ID, -1);

        if (lastWorkingCentreId >= 0) {
            for (CenterWrapper<?> centre : availableCenters) {
                if (centre.getId() == lastWorkingCentreId) {
                    return centre;
                }
            }
        }
        return null;
    }

    @Override
    protected void okPressed() {
        user.setCurrentWorkingCenter(currentCenter);
        super.okPressed();
    }
}
