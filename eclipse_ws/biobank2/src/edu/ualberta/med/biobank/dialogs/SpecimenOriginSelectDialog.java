package edu.ualberta.med.biobank.dialogs;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.CenterWrapper;
import edu.ualberta.med.biobank.common.wrappers.OriginInfoWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import edu.ualberta.med.biobank.gui.common.dialogs.BgcBaseDialog;
import edu.ualberta.med.biobank.gui.common.widgets.utils.ComboSelectionUpdate;
import edu.ualberta.med.biobank.widgets.BiobankLabelProvider;

public class SpecimenOriginSelectDialog extends BgcBaseDialog {
    private static final I18n i18n = I18nFactory
        .getI18n(SpecimenOriginSelectDialog.class);
    private final SpecimenWrapper specimen;
    private CenterWrapper<?> selectedCenter;
    private final List<CenterWrapper<?>> centers;
    private final String description;

    @SuppressWarnings("nls")
    public SpecimenOriginSelectDialog(Shell parentShell,
        SpecimenWrapper specimen, List<CenterWrapper<?>> centers) {
        super(parentShell);
        this.specimen = specimen;
        this.centers = centers;
        description =
            // TR: dialog title area message
            i18n.tr("Select the center where specimen ''{0}'' was created.",
                specimen.getInventoryId());
    }

    @Override
    protected String getTitleAreaMessage() {
        return description;
    }

    @SuppressWarnings("nls")
    @Override
    protected String getTitleAreaTitle() {
        // TR: dialog title area title
        return i18n.tr("Specimen original center");
    }

    @SuppressWarnings("nls")
    @Override
    protected String getDialogShellTitle() {
        // TR: dialog shell title
        return i18n.tr("Specimen original center");
    }

    @SuppressWarnings("nls")
    @Override
    protected void createDialogAreaInternal(Composite parent) throws Exception {
        Assert.isNotNull(SessionManager.getUser().getCurrentWorkingCenter());

        Composite contents = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(2, false);
        contents.setLayout(layout);
        contents.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        Label l = new Label(contents, SWT.WRAP);
        GridData gd = new GridData(GridData.FILL, SWT.TOP, true, true, 2, 1);
        gd.widthHint = 400;
        l.setLayoutData(gd);

        l.setText(i18n
            .tr("Source specimens require an origin center. By removing this specimen from this shipment the origin center has to be re-assigned. Please select this specimen's origin center. If this specimen is to be deleted then select your center from the list."));

        selectedCenter = SessionManager.getUser().getCurrentWorkingCenter();

        List<Object> objectList = new ArrayList<Object>(centers);
        widgetCreator.createComboViewer(contents,
            i18n.tr("Creation center"),
            objectList,
            selectedCenter, null, new ComboSelectionUpdate() {
                @Override
                public void doSelection(Object selectedObject) {
                    if (selectedObject instanceof CenterWrapper<?>)
                        selectedCenter = (CenterWrapper<?>) selectedObject;
                    else
                        selectedCenter = null;
                }
            }, new BiobankLabelProvider());
    }

    @Override
    protected void okPressed() {
        if (selectedCenter != null) {
            OriginInfoWrapper origin = new OriginInfoWrapper(
                specimen.getAppService());
            origin.setCenter(selectedCenter);
            specimen.setOriginInfo(origin);
        }
        super.okPressed();
    }
}
