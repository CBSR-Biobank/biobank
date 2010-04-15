package edu.ualberta.med.biobank.dialogs;

import java.util.List;

import org.eclipse.core.databinding.beans.PojoObservables;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.common.wrappers.SourceVesselWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudySourceVesselWrapper;

public class StudySourceVesselDialog extends BiobankDialog {

    private static final String TITLE = "Study Source Vessel";

    private StudySourceVesselWrapper origStudySourceVessel;

    private StudySourceVesselWrapper studySourceVessel;

    private List<SourceVesselWrapper> allSourceVesselsMap;

    private ComboViewer sourceVesselComboViewer;

    public StudySourceVesselDialog(Shell parent,
        StudySourceVesselWrapper studySourceVessel,
        List<SourceVesselWrapper> sourceVessels) {
        super(parent);
        Assert.isNotNull(studySourceVessel);
        Assert.isNotNull(sourceVessels);
        this.origStudySourceVessel = studySourceVessel;
        this.studySourceVessel = new StudySourceVesselWrapper(null);
        this.studySourceVessel.setStudy(studySourceVessel.getStudy());
        this.studySourceVessel.setSourceVessel(studySourceVessel
            .getSourceVessel());
        this.studySourceVessel.setNeedTimeDrawn(studySourceVessel
            .getNeedTimeDrawn());
        this.studySourceVessel.setNeedRealVolume(studySourceVessel
            .getNeedRealVolume());
        allSourceVesselsMap = sourceVessels;
    }

    @Override
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        String title = new String();

        if (origStudySourceVessel.getSourceVessel() == null) {
            title = "Add";
        } else {
            title = "Edit ";
        }
        title += TITLE;
        shell.setText(title);
    }

    @Override
    protected Control createContents(Composite parent) {
        Control contents = super.createContents(parent);
        setTitleImage(BioBankPlugin.getDefault().getImageRegistry().get(
            BioBankPlugin.IMG_COMPUTER_KEY));
        if (origStudySourceVessel.getSourceVessel() == null) {
            setTitle("Add Study Source Vessel");
            setMessage("Add source vessel to this study");
        } else {
            setTitle("Edit Study Source Vessel");
            setMessage("Edit source vessel for this study");
        }
        return contents;
    }

    @Override
    protected void createDialogAreaInternal(Composite parent) throws Exception {
        Composite contents = new Composite(parent, SWT.NONE);
        contents.setLayout(new GridLayout(2, false));
        contents.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        sourceVesselComboViewer = getWidgetCreator()
            .createComboViewerWithNoSelectionValidator(contents,
                "Source Vessel", allSourceVesselsMap,
                studySourceVessel.getSourceVessel(),
                "A source vessel should be selected");
        sourceVesselComboViewer
            .addSelectionChangedListener(new ISelectionChangedListener() {
                @Override
                public void selectionChanged(SelectionChangedEvent event) {
                    IStructuredSelection stSelection = (IStructuredSelection) sourceVesselComboViewer
                        .getSelection();
                    studySourceVessel
                        .setSourceVessel((SourceVesselWrapper) stSelection
                            .getFirstElement());
                }
            });

        createBoundWidgetWithLabel(contents, Button.class, SWT.BORDER,
            "Need Time Drawn", new String[0], PojoObservables.observeValue(
                studySourceVessel, "needTimeDrawn"), null);

        createBoundWidgetWithLabel(contents, Button.class, SWT.BORDER,
            "Need Real Volume", new String[0], PojoObservables.observeValue(
                studySourceVessel, "needRealVolume"), null);
    }

    @Override
    protected void okPressed() {
        origStudySourceVessel.setSourceVessel(studySourceVessel
            .getSourceVessel());
        origStudySourceVessel.setNeedTimeDrawn(studySourceVessel
            .getNeedTimeDrawn());
        origStudySourceVessel.setNeedRealVolume(studySourceVessel
            .getNeedRealVolume());
        super.okPressed();
    }

}
