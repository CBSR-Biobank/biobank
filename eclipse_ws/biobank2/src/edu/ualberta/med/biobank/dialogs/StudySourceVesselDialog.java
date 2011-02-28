package edu.ualberta.med.biobank.dialogs;

import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.common.wrappers.SourceVesselWrapper;
import edu.ualberta.med.biobank.common.wrappers.SourceSpecimenWrapper;
import edu.ualberta.med.biobank.widgets.utils.ComboSelectionUpdate;

public class StudySourceVesselDialog extends BiobankDialog {

    private static final String TITLE = "Study Source Vessel";

    private SourceSpecimenWrapper origStudySourceVessel;

    private SourceSpecimenWrapper studySourceVessel;

    private List<SourceVesselWrapper> allSourceVesselsMap;

    private String currentTitle;

    private String message;

    public StudySourceVesselDialog(Shell parent,
        SourceSpecimenWrapper studySourceVessel,
        List<SourceVesselWrapper> sourceVessels) {
        super(parent);
        Assert.isNotNull(studySourceVessel);
        Assert.isNotNull(sourceVessels);
        this.origStudySourceVessel = studySourceVessel;
        this.studySourceVessel = new SourceSpecimenWrapper(null);
        this.studySourceVessel.setStudy(studySourceVessel.getStudy());
        this.studySourceVessel.setSourceVessel(studySourceVessel
            .getSourceVessel());
        this.studySourceVessel.setNeedTimeDrawn(studySourceVessel
            .getNeedTimeDrawn());
        this.studySourceVessel.setNeedOriginalVolume(studySourceVessel
            .getNeedOriginalVolume());
        allSourceVesselsMap = sourceVessels;
        message = "source vessel to this study";
        if (origStudySourceVessel.getSourceVessel() == null) {
            currentTitle = "Add " + TITLE;
            message = "Add " + message;
        } else {
            currentTitle = "Edit " + TITLE;
            message = "Edit " + message;
        }
    }

    @Override
    protected String getDialogShellTitle() {
        return currentTitle;
    }

    @Override
    protected String getTitleAreaMessage() {
        return message;
    }

    @Override
    protected String getTitleAreaTitle() {
        return currentTitle;
    }

    @Override
    protected Image getTitleAreaImage() {
        // FIXME should use another icon
        return BioBankPlugin.getDefault().getImageRegistry()
            .get(BioBankPlugin.IMG_COMPUTER_KEY);
    }

    @Override
    protected void createDialogAreaInternal(Composite parent) throws Exception {
        Composite contents = new Composite(parent, SWT.NONE);
        contents.setLayout(new GridLayout(2, false));
        contents.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        getWidgetCreator().createComboViewer(contents, "Source Vessel",
            allSourceVesselsMap, studySourceVessel.getSourceVessel(),
            "A source vessel should be selected", new ComboSelectionUpdate() {
                @Override
                public void doSelection(Object selectedObject) {
                    studySourceVessel
                        .setSourceVessel((SourceVesselWrapper) selectedObject);
                }
            });

        createBoundWidgetWithLabel(contents, Button.class, SWT.BORDER,
            "Need Time Drawn", new String[0], studySourceVessel,
            "needTimeDrawn", null);

        createBoundWidgetWithLabel(contents, Button.class, SWT.BORDER,
            "Need Original Volume", new String[0], studySourceVessel,
            "needOriginalVolume", null);
    }

    @Override
    protected void okPressed() {
        origStudySourceVessel.setSourceVessel(studySourceVessel
            .getSourceVessel());
        origStudySourceVessel.setNeedTimeDrawn(studySourceVessel
            .getNeedTimeDrawn());
        origStudySourceVessel.setNeedOriginalVolume(studySourceVessel
            .getNeedOriginalVolume());
        super.okPressed();
    }

}
