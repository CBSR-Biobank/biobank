package edu.ualberta.med.biobank.dialogs;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.common.wrappers.PvSourceVesselWrapper;
import edu.ualberta.med.biobank.common.wrappers.SourceVesselWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudySourceVesselWrapper;
import edu.ualberta.med.biobank.validators.IntegerNumberValidator;
import edu.ualberta.med.biobank.widgets.DateTimeWidget;

public class PvSourceVesselDialog extends BiobankDialog {

    private static final String TITLE = "Source Vessel";

    private PvSourceVesselWrapper pvSourceVessel;

    private ComboViewer sourceVesselsComboViewer;

    private Map<String, StudySourceVesselWrapper> mapStudySourceVessel;

    private DateTimeWidget timeDrawnWidget;

    private Control volumeText;

    private List<SourceVesselWrapper> allSourceVessels;

    private Label timeDrawnLabel;

    private Label volumeLabel;

    public PvSourceVesselDialog(Shell parent,
        PvSourceVesselWrapper pvSourceVessel,
        List<StudySourceVesselWrapper> studySourceVessels,
        List<SourceVesselWrapper> allSourceVessels) {
        super(parent);
        Assert.isNotNull(pvSourceVessel);
        Assert.isNotNull(studySourceVessels);
        this.pvSourceVessel = pvSourceVessel;
        mapStudySourceVessel = new HashMap<String, StudySourceVesselWrapper>();
        for (StudySourceVesselWrapper ssv : studySourceVessels) {
            mapStudySourceVessel.put(ssv.getSourceVessel().getName(), ssv);
        }
        this.allSourceVessels = allSourceVessels;
    }

    @Override
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        String title = new String();

        if (pvSourceVessel.getSourceVessel() == null) {
            title = "Add ";
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
        if (pvSourceVessel.getSourceVessel() == null) {
            setTitle("Add Source Vessel");
            setMessage("Add a source vessel to a patient visit");
        } else {
            setTitle("Edit Source Vessel");
            setMessage("Edit a source vessel in a patient visit");
        }
        return contents;
    }

    @Override
    protected void createDialogAreaInternal(Composite parent) {
        Composite contents = new Composite(parent, SWT.NONE);
        contents.setLayout(new GridLayout(3, false));
        contents.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        boolean useStudyOnlySourceVessels = true;
        StudySourceVesselWrapper ssv = null;
        if (pvSourceVessel.getSourceVessel() != null) {
            ssv = mapStudySourceVessel.get(pvSourceVessel.getSourceVessel()
                .getName());
        }
        if (ssv == null && pvSourceVessel.getSourceVessel() != null
            && allSourceVessels.contains(pvSourceVessel.getSourceVessel())) {
            useStudyOnlySourceVessels = false;
        }
        sourceVesselsComboViewer = getWidgetCreator()
            .createComboViewerWithNoSelectionValidator(contents,
                "Source Vessel", mapStudySourceVessel.values(), ssv,
                "A source vessel should be selected");
        if (!useStudyOnlySourceVessels) {
            sourceVesselsComboViewer.setInput(allSourceVessels);
            sourceVesselsComboViewer.setSelection(new StructuredSelection(
                pvSourceVessel.getSourceVessel()));
        }
        sourceVesselsComboViewer
            .addSelectionChangedListener(new ISelectionChangedListener() {
                @Override
                public void selectionChanged(SelectionChangedEvent event) {
                    Object selection = ((IStructuredSelection) sourceVesselsComboViewer
                        .getSelection()).getFirstElement();
                    if (selection instanceof StudySourceVesselWrapper) {
                        pvSourceVessel
                            .setSourceVessel(((StudySourceVesselWrapper) selection)
                                .getSourceVessel());
                    } else {
                        pvSourceVessel
                            .setSourceVessel((SourceVesselWrapper) selection);
                    }
                    updateWidgetVisibilityAndValues();
                }
            });

        final Button allSourceVesselCheckBox = new Button(contents, SWT.CHECK);
        allSourceVesselCheckBox.setText("Show only study source vessels");
        allSourceVesselCheckBox.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (allSourceVesselCheckBox.getSelection()) {
                    sourceVesselsComboViewer.setInput(mapStudySourceVessel
                        .values());
                } else {
                    sourceVesselsComboViewer.setInput(allSourceVessels);
                }
            }
        });
        allSourceVesselCheckBox.setSelection(useStudyOnlySourceVessels);

        Text quantityText = (Text) createBoundWidgetWithLabel(contents,
            Text.class, SWT.BORDER, "Quantity", new String[0], BeansObservables
                .observeValue(pvSourceVessel, "quantity"),
            new IntegerNumberValidator("quantity should be a whole number",
                false));
        GridData gd = (GridData) quantityText.getLayoutData();
        gd.horizontalSpan = 2;

        timeDrawnLabel = widgetCreator.createLabel(contents, "Time drawn");
        timeDrawnWidget = widgetCreator.createDateTimeWidget(contents,
            timeDrawnLabel, pvSourceVessel.getTimeDrawn(), BeansObservables
                .observeValue(pvSourceVessel, "timeDrawn"), null, SWT.TIME);
        gd = (GridData) timeDrawnWidget.getLayoutData();
        gd.horizontalSpan = 2;

        volumeLabel = widgetCreator.createLabel(contents, "Volume (ml)");
        volumeLabel.setLayoutData(new GridData(
            GridData.VERTICAL_ALIGN_BEGINNING));
        volumeText = widgetCreator.createBoundWidget(contents, Text.class,
            SWT.BORDER, volumeLabel, new String[0], BeansObservables
                .observeValue(pvSourceVessel, "volume"), null);
        gd = (GridData) volumeText.getLayoutData();
        gd.horizontalSpan = 2;

        updateWidgetVisibilityAndValues();
    }

    public void updateWidgetVisibilityAndValues() {
        StudySourceVesselWrapper ssv = null;
        SourceVesselWrapper currentSourceVessel = pvSourceVessel
            .getSourceVessel();
        if (currentSourceVessel != null) {
            ssv = mapStudySourceVessel.get(currentSourceVessel.getName());
        }
        boolean enableTimeDrawn = (currentSourceVessel != null)
            && (ssv == null || Boolean.TRUE.equals(ssv.getNeedTimeDrawn()));
        boolean enableVolume = (currentSourceVessel != null)
            && (ssv == null || Boolean.TRUE.equals(ssv.getNeedOriginalVolume()));
        timeDrawnLabel.setVisible(enableTimeDrawn);
        timeDrawnWidget.setVisible(enableTimeDrawn);
        volumeLabel.setVisible(enableVolume);
        volumeText.setVisible(enableVolume);
        if (!enableTimeDrawn) {
            pvSourceVessel.setTimeDrawn(null);
        }
        if (!enableVolume) {
            pvSourceVessel.setVolume(null);
        }
    }

    public PvSourceVesselWrapper getPvSourceVessel() {
        return pvSourceVessel;
    }

}
