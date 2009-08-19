package edu.ualberta.med.biobank.forms;

import java.util.Collection;

import org.apache.commons.collections.MapIterator;
import org.apache.commons.collections.map.ListOrderedMap;
import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.PartInitException;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.ModelUtils;
import edu.ualberta.med.biobank.forms.input.FormInput;
import edu.ualberta.med.biobank.model.PatientVisit;
import edu.ualberta.med.biobank.model.PvInfo;
import edu.ualberta.med.biobank.model.PvInfoData;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.treeview.PatientVisitAdapter;
import edu.ualberta.med.biobank.treeview.SiteAdapter;
import edu.ualberta.med.biobank.treeview.StudyAdapter;
import edu.ualberta.med.biobank.widgets.infotables.SamplesListWidget;

public class PatientVisitViewForm extends BiobankViewForm {

    public static final String ID = "edu.ualberta.med.biobank.forms.PatientVisitViewForm";

    private PatientVisitAdapter patientVisitAdapter;

    private PatientVisit patientVisit;

    private SamplesListWidget samplesWidget;

    // used to keep track of which data has been entered or left blank for
    // a patient visit.
    class CombinedPvInfo {
        PvInfo pvInfo;
        PvInfoData pvInfoData;

        public CombinedPvInfo() {
            pvInfo = null;
            pvInfoData = null;
        }
    }

    private ListOrderedMap combinedPvInfoMap;

    private Label clinicLabel;

    public PatientVisitViewForm() {
        super();
        combinedPvInfoMap = new ListOrderedMap();

    }

    @Override
    public void init() {
        Assert.isTrue((adapter instanceof PatientVisitAdapter),
            "Invalid editor input: object of type "
                + adapter.getClass().getName());

        patientVisitAdapter = (PatientVisitAdapter) adapter;
        retrievePatientVisit();

        setPartName("Visit "
            + BioBankPlugin.getDateFormatter().format(
                patientVisit.getDateDrawn()));
    }

    @Override
    protected void createFormContent() {
        form.setText("Visit Drawn Date: "
            + BioBankPlugin.getDateTimeFormatter().format(
                patientVisit.getDateDrawn()));
        form.getBody().setLayout(new GridLayout(1, false));
        form.getBody().setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        addRefreshToolbarAction();
        createVisitSection();
        createSamplesSection();

    }

    private void createVisitSection() {
        Composite client = toolkit.createComposite(form.getBody());
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 10;
        client.setLayout(layout);
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(client);

        Study study = ((StudyAdapter) patientVisitAdapter
            .getParentFromClass(StudyAdapter.class)).getStudy();

        clinicLabel = (Label) createWidget(client, Label.class, SWT.NONE,
            "Clinic");

        // get all PvInfo from study, since user may not have filled in all
        // fields
        for (PvInfo pvInfo : study.getPvInfoCollection()) {
            CombinedPvInfo combinedPvInfo = new CombinedPvInfo();
            combinedPvInfo.pvInfo = pvInfo;
            combinedPvInfoMap.put(pvInfo.getId(), combinedPvInfo);
        }

        Collection<PvInfoData> pvInfoDataCollection = patientVisit
            .getPvInfoDataCollection();
        if (pvInfoDataCollection != null) {
            for (PvInfoData pvInfoData : pvInfoDataCollection) {
                Integer key = pvInfoData.getPvInfo().getId();
                CombinedPvInfo combinedPvInfo = (CombinedPvInfo) combinedPvInfoMap
                    .get(key);
                combinedPvInfo.pvInfoData = pvInfoData;
            }
        }

        Label widget;
        MapIterator it = combinedPvInfoMap.mapIterator();
        while (it.hasNext()) {
            it.next();
            CombinedPvInfo combinedPvInfo = (CombinedPvInfo) it.getValue();
            String type = combinedPvInfo.pvInfo.getPvInfoType().getType();
            String value = "";

            if (combinedPvInfo.pvInfoData != null) {
                value = combinedPvInfo.pvInfoData.getValue();
            }

            Label labelWidget = toolkit.createLabel(client,
                combinedPvInfo.pvInfo.getLabel() + ":", SWT.LEFT);
            labelWidget.setLayoutData(new GridData(
                GridData.VERTICAL_ALIGN_BEGINNING));

            int style = SWT.BORDER | SWT.LEFT;
            if (type.equals("text") || type.equals("select_multiple")) {
                style |= SWT.WRAP;
            }

            if ((value != null) && type.equals("select_multiple")) {
                value = value.replace(';', '\n');
            }

            widget = toolkit.createLabel(client, value, style);
            GridData gd = new GridData(GridData.FILL_HORIZONTAL);
            widget.setLayoutData(gd);
        }
        setPatientVisitValues();
    }

    private void setPatientVisitValues() {
        FormUtils.setTextValue(clinicLabel,
            patientVisit.getClinic() == null ? "" : patientVisit.getClinic()
                .getName());
        // FIXME update all pvinfos ?
    }

    private void createSamplesSection() {
        Composite parent = createSectionWithClient("Samples");
        samplesWidget = new SamplesListWidget(parent,
            (SiteAdapter) patientVisitAdapter
                .getParentFromClass(SiteAdapter.class), patientVisit
                .getSampleCollection());
        samplesWidget.adaptToToolkit(toolkit, true);
        samplesWidget.setSelection(patientVisitAdapter.getSelectedSample());

        final Button edit = toolkit.createButton(parent,
            "Edit this information", SWT.PUSH);
        edit.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                getSite().getPage().closeEditor(PatientVisitViewForm.this,
                    false);
                try {
                    getSite().getPage().openEditor(
                        new FormInput(patientVisitAdapter),
                        PatientVisitEntryForm.ID, true);
                } catch (PartInitException exp) {
                    exp.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void reload() {
        retrievePatientVisit();
        setPartName("Visit "
            + BioBankPlugin.getDateFormatter().format(
                patientVisit.getDateDrawn()));
        form.setText("Visit Drawn Date: "
            + BioBankPlugin.getDateTimeFormatter().format(
                patientVisit.getDateDrawn()));
        setPatientVisitValues();
    }

    private void retrievePatientVisit() {
        try {
            patientVisit = ModelUtils.getObjectWithId(patientVisitAdapter
                .getAppService(), PatientVisit.class, patientVisitAdapter
                .getPatientVisit().getId());
            patientVisitAdapter.setPatientVisit(patientVisit);
        } catch (Exception ex) {
            SessionManager.getLogger().error(
                "Error while retrieving patient visit "
                    + patientVisitAdapter.getPatientVisit().getDateDrawn(), ex);
        }
    }

}
