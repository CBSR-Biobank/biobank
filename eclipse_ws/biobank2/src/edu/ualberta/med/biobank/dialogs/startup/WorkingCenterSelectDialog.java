package edu.ualberta.med.biobank.dialogs.startup;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.common.wrappers.CenterWrapper;
import edu.ualberta.med.biobank.common.wrappers.UserWrapper;
import edu.ualberta.med.biobank.gui.common.dialogs.BgcBaseDialog;
import edu.ualberta.med.biobank.gui.common.widgets.utils.ComboSelectionUpdate;
import edu.ualberta.med.biobank.widgets.BiobankLabelProvider;

public class WorkingCenterSelectDialog extends BgcBaseDialog {
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
    // TR: no center selection combo box option
    private static final String COMBO_LABEL = i18n.tr("Available centers");

    private final UserWrapper user;
    private CenterWrapper<?> currentCenter;
    private final List<CenterWrapper<?>> availableCenters;

    public WorkingCenterSelectDialog(Shell parentShell, UserWrapper user,
        List<CenterWrapper<?>> availableCenters) {
        super(parentShell);
        this.user = user;
        this.availableCenters = availableCenters;
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
        widgetCreator.createComboViewer(contents, COMBO_LABEL,
            objectList, NO_CENTER_STRING, null, new ComboSelectionUpdate() {
                @Override
                public void doSelection(Object selectedObject) {
                    if (selectedObject instanceof CenterWrapper<?>)
                        currentCenter = (CenterWrapper<?>) selectedObject;
                    else
                        currentCenter = null;
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

    @Override
    protected void okPressed() {
        user.setCurrentWorkingCenter(currentCenter);
        super.okPressed();
    }
}
