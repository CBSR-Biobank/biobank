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
    private static final I18n i18n = I18nFactory
        .getI18n(WorkingCenterSelectDialog.class);

    private final UserWrapper user;
    private CenterWrapper<?> currentCenter;
    private final List<CenterWrapper<?>> availableCenters;

    public WorkingCenterSelectDialog(Shell parentShell, UserWrapper user,
        List<CenterWrapper<?>> availableCenters) {
        super(parentShell);
        this.user = user;
        this.availableCenters = availableCenters;
    }

    @SuppressWarnings("nls")
    @Override
    protected String getTitleAreaMessage() {
        // TR: select working center title area message
        return i18n.tr("Choose the center you will work with.");
    }

    @SuppressWarnings("nls")
    @Override
    protected String getTitleAreaTitle() {
        // TR: select working center title area title
        return i18n.tr("Working center selection");
    }

    @SuppressWarnings("nls")
    @Override
    protected String getDialogShellTitle() {
        // TR: select working center shell title
        return i18n.tr("Working center selection");
    }

    @SuppressWarnings("nls")
    @Override
    protected void createDialogAreaInternal(Composite parent) throws Exception {
        Composite contents = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(2, false);
        contents.setLayout(layout);
        contents.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        List<Object> objectList = new ArrayList<Object>(availableCenters);
        // TR: no center selection combo box option
        String noCenterString = i18n.tr("-- no center selection --");
        if (user.isInSuperAdminMode())
            objectList.add(noCenterString);
        widgetCreator.createComboViewer(contents,
            // TR: select working center combo box label
            i18n.tr("Available centers"),
            objectList, noCenterString, null, new ComboSelectionUpdate() {
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
