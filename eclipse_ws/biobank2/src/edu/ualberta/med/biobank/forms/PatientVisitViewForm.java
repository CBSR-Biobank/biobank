
package edu.ualberta.med.biobank.forms;

import java.text.SimpleDateFormat;
import java.util.Collection;

import org.apache.commons.collections.MapIterator;
import org.apache.commons.collections.map.ListOrderedMap;
import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.forms.input.FormInput;
import edu.ualberta.med.biobank.model.PatientVisit;
import edu.ualberta.med.biobank.model.PvInfo;
import edu.ualberta.med.biobank.model.PvInfoData;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.treeview.Node;
import edu.ualberta.med.biobank.treeview.PatientVisitAdapter;
import edu.ualberta.med.biobank.treeview.StudyAdapter;

public class PatientVisitViewForm extends BiobankViewForm {

    public static final String ID = "edu.ualberta.med.biobank.forms.PatientVisitViewForm";

    private PatientVisitAdapter patientVisitAdapter;

    private PatientVisit patientVisit;

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

    public PatientVisitViewForm() {
        super();
        combinedPvInfoMap = new ListOrderedMap();

    }

    @Override
    public void init(IEditorSite editorSite, IEditorInput input)
        throws PartInitException {
        super.init(editorSite, input);

        Node node = ((FormInput) input).getNode();
        Assert.isNotNull(node, "Null editor input");

        patientVisitAdapter = (PatientVisitAdapter) node;
        appService = patientVisitAdapter.getAppService();
        patientVisit = patientVisitAdapter.getPatientVisit();

        if (patientVisit.getId() == null) {
            setPartName("New Visit");
        }
        else {
            SimpleDateFormat sdf = new SimpleDateFormat(
                BioBankPlugin.DATE_FORMAT);
            setPartName("Visit " + sdf.format(patientVisit.getDateDrawn()));
        }
    }

    @Override
    protected void createFormContent() {
        SimpleDateFormat sdf = new SimpleDateFormat(
            BioBankPlugin.DATE_TIME_FORMAT);
        form.setText("Visit Drawn Date: "
            + sdf.format(patientVisit.getDateDrawn()));
        form.getBody().setLayout(new GridLayout(1, false));
        form.getBody().setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        addRefreshToolbarAction();

        createVisitSection();

    }

    private void createVisitSection() {
        Composite client = toolkit.createComposite(form.getBody());
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 10;
        client.setLayout(layout);
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(client);

        Study study = ((StudyAdapter) patientVisitAdapter.getParent().getParent().getParent()).getStudy();

        // get all PvInfo from study, since user may not have filled in all
        // fields
        for (PvInfo pvInfo : study.getPvInfoCollection()) {
            CombinedPvInfo combinedPvInfo = new CombinedPvInfo();
            combinedPvInfo.pvInfo = pvInfo;
            combinedPvInfoMap.put(pvInfo.getId(), combinedPvInfo);
        }

        Collection<PvInfoData> pvInfoDataCollection = patientVisit.getPvInfoDataCollection();
        if (pvInfoDataCollection != null) {
            for (PvInfoData pvInfoData : pvInfoDataCollection) {
                Integer key = pvInfoData.getPvInfo().getId();
                CombinedPvInfo combinedPvInfo = (CombinedPvInfo) combinedPvInfoMap.get(key);
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

    }

    @Override
    protected void reload() {

    }

}
