package edu.ualberta.med.biobank.dialogs;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import edu.ualberta.med.biobank.common.wrappers.CenterWrapper;
import edu.ualberta.med.biobank.common.wrappers.OriginInfoWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import edu.ualberta.med.biobank.gui.common.dialogs.BgcBaseDialog;
import edu.ualberta.med.biobank.gui.common.widgets.utils.ComboSelectionUpdate;
import edu.ualberta.med.biobank.widgets.BiobankLabelProvider;

public class SpecimenOriginSelectDialog extends BgcBaseDialog {
    private SpecimenWrapper specimen;
    private CenterWrapper<?> selectedCenter;
    private List<CenterWrapper<?>> centers;

    public SpecimenOriginSelectDialog(Shell parentShell,
        SpecimenWrapper specimen, List<CenterWrapper<?>> centers) {
        super(parentShell);
        this.specimen = specimen;
        this.centers = centers;
    }

    @Override
    protected String getTitleAreaMessage() {
        return Messages.SpecimenOriginSelectDialog_description;
    }

    @Override
    protected String getTitleAreaTitle() {
        return Messages.SpecimenOriginSelectDialog_title;
    }

    @Override
    protected String getDialogShellTitle() {
        return Messages.SpecimenOriginSelectDialog_title;
    }

    @Override
    protected void createDialogAreaInternal(Composite parent) throws Exception {
        Composite contents = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(2, false);
        contents.setLayout(layout);
        contents.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        List<Object> objectList = new ArrayList<Object>(centers);
        widgetCreator.createComboViewer(contents,
            Messages.SpecimenOriginSelectDialog_centers_label, objectList,
            null, null, new ComboSelectionUpdate() {
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
