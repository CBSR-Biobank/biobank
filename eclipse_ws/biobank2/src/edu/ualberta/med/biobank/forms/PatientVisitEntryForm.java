
package edu.ualberta.med.biobank.forms;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections.MapIterator;
import org.apache.commons.collections.map.ListOrderedMap;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.forms.input.FormInput;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.PatientVisit;
import edu.ualberta.med.biobank.model.PvInfo;
import edu.ualberta.med.biobank.model.PvInfoData;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.treeview.Node;
import edu.ualberta.med.biobank.treeview.PatientAdapter;
import edu.ualberta.med.biobank.treeview.PatientVisitAdapter;
import edu.ualberta.med.biobank.treeview.StudyAdapter;
import edu.ualberta.med.biobank.widgets.DateTimeWidget;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.SDKQuery;
import gov.nih.nci.system.query.SDKQueryResult;
import gov.nih.nci.system.query.example.InsertExampleQuery;
import gov.nih.nci.system.query.example.UpdateExampleQuery;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class PatientVisitEntryForm extends BiobankEntryForm {
    public static final String ID = "edu.ualberta.med.biobank.forms.PatientVisitEntryForm";

    public static final String MSG_NEW_PATIENT_VISIT_OK = "Creating a new patient visit record.";

    public static final String MSG_PATIENT_VISIT_OK = "Editing an existing patient visit record.";

    public static final String MSG_NO_VISIT_NUMBER = "Visit must have a number";

    public static final String DATE_FORMAT = "yyyy-MM-dd";

    private PatientVisitAdapter patientVisitAdapter;

    private PatientVisit patientVisit;

    private Study study;

    class CombinedPvInfo {
        PvInfo pvInfo;
        PvInfoData pvInfoData;
        Control control;

        public CombinedPvInfo() {
            pvInfo = null;
            pvInfoData = null;
            control = null;
        }
    }

    private ListOrderedMap combinedPvInfoMap;

    DateTimeWidget dateDrawn;

    public PatientVisitEntryForm() {
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
        patientVisit = patientVisitAdapter.getPatientVisit();
        appService = patientVisitAdapter.getAppService();

        if (patientVisit.getId() == null) {
            setPartName("New Patient Visit");
        }
        else {
            SimpleDateFormat sdf = new SimpleDateFormat(
                BioBankPlugin.DATE_FORMAT);
            setPartName("Visit " + sdf.format(patientVisit.getDateDrawn()));
        }
    }

    @Override
    protected void createFormContent() {
        form.setText("Patient Visit Information");
        form.setMessage(getOkMessage(), IMessageProvider.NONE);
        form.getBody().setLayout(new GridLayout(1, false));

        createPvSection();
        createButtonsSection();
    }

    private void createPvSection() {
        Composite client = toolkit.createComposite(form.getBody());
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 10;
        client.setLayout(layout);
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(client);

        toolkit.createLabel(client, "Date Drawn:", SWT.NONE);
        dateDrawn = new DateTimeWidget(client, SWT.BORDER,
            patientVisit.getDateDrawn());
        dateDrawn.adaptToToolkit(toolkit);

        study = ((StudyAdapter) patientVisitAdapter.getParent().getParent().getParent()).getStudy();

        for (PvInfo pvInfo : study.getPvInfoCollection()) {
            CombinedPvInfo combinedPvInfo = new CombinedPvInfo();
            combinedPvInfo.pvInfo = pvInfo;
            combinedPvInfoMap.put(pvInfo.getId(), combinedPvInfo);
        }

        Collection<PvInfoData> pvDataCollection = patientVisit.getPvInfoDataCollection();
        if (pvDataCollection != null) {
            for (PvInfoData pvInfoData : pvDataCollection) {
                Integer key = pvInfoData.getPvInfo().getId();
                CombinedPvInfo combinedPvInfo = (CombinedPvInfo) combinedPvInfoMap.get(key);
                Assert.isNotNull(combinedPvInfo);
                combinedPvInfo.pvInfoData = pvInfoData;
            }
        }

        MapIterator it = combinedPvInfoMap.mapIterator();
        while (it.hasNext()) {
            it.next();
            CombinedPvInfo combinedPvInfo = (CombinedPvInfo) it.getValue();
            int typeId = combinedPvInfo.pvInfo.getPvInfoType().getId();
            String value = null;

            if (combinedPvInfo.pvInfoData != null) {
                value = combinedPvInfo.pvInfoData.getValue();
            }

            Label labelWidget = toolkit.createLabel(client,
                combinedPvInfo.pvInfo.getLabel() + ":", SWT.LEFT);
            labelWidget.setLayoutData(new GridData(
                GridData.VERTICAL_ALIGN_BEGINNING));

            switch (typeId) {
                case 1: // number
                    combinedPvInfo.control = toolkit.createText(client, value,
                        SWT.LEFT);
                    break;

                case 2: // text
                    combinedPvInfo.control = toolkit.createText(client, value,
                        SWT.LEFT | SWT.MULTI);
                    break;

                case 3: // date_time
                    SimpleDateFormat sdf = new SimpleDateFormat(
                        BioBankPlugin.DATE_TIME_FORMAT);

                    Date date = new Date();
                    if (value != null) {
                        try {
                            date = sdf.parse(value);
                        }
                        catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                    DateTimeWidget w = new DateTimeWidget(client, SWT.NONE,
                        date);
                    w.adaptToToolkit(toolkit);
                    combinedPvInfo.control = w;
                    break;

                case 4: // select_single
                    combinedPvInfo.control = createComboSection(client,
                        combinedPvInfo.pvInfo.getPossibleValues().split(";"),
                        value);
                    break;

                case 5: // select_single_and_quantity
                    break;

                case 6: // select_multiple
                    break;

                default:
                    Assert.isTrue(false, "Invalid pvInfo type: " + typeId);
            }

            if (combinedPvInfo.control != null) {
                GridData gd = new GridData(GridData.FILL_HORIZONTAL);
                if (typeId == 2) {
                    gd.heightHint = 40;
                }
                combinedPvInfo.control.setLayoutData(gd);
                controls.put(combinedPvInfo.pvInfo.getLabel(),
                    combinedPvInfo.control);
            }
        }
    }

    private Control createComboSection(Composite client, String [] values,
        String selected) {

        Combo combo = new Combo(client, SWT.READ_ONLY);
        combo.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
        combo.setItems(values);

        if (selected != null) {
            int count = 0;
            for (String value : values) {
                if (selected.equals(value)) {
                    combo.select(count);
                    break;
                }
                ++count;
            }
        }

        toolkit.adapt(combo, true, true);

        return combo;
    }

    private void createButtonsSection() {
        Composite client = toolkit.createComposite(form.getBody());
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 10;
        client.setLayout(layout);
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(client);

        initConfirmButton(client, true, false);
    }

    @Override
    protected String getOkMessage() {
        if (patientVisit.getId() == null) {
            return MSG_NEW_PATIENT_VISIT_OK;
        }
        return MSG_PATIENT_VISIT_OK;
    }

    @Override
    protected void handleStatusChanged(IStatus status) {
        if (status.getSeverity() == IStatus.OK) {
            form.setMessage(getOkMessage(), IMessageProvider.NONE);
            getConfirmButton().setEnabled(true);
        }
        else {
            form.setMessage(status.getMessage(), IMessageProvider.ERROR);
            getConfirmButton().setEnabled(false);
        }
    }

    @Override
    protected void saveForm() throws Exception {
        if ((patientVisit.getId() == null) && !checkVisitDateDrawnUnique()) {
            setDirty(true);
            return;
        }

        SDKQuery query;
        SDKQueryResult result;

        PatientAdapter patientAdapter = (PatientAdapter) patientVisitAdapter.getParent();
        patientVisit.setPatient(patientAdapter.getPatient());
        savePvInfoData();

        if ((patientVisit.getId() == null) || (patientVisit.getId() == 0)) {
            query = new InsertExampleQuery(patientVisit);
        }
        else {
            query = new UpdateExampleQuery(patientVisit);
        }

        result = appService.executeQuery(query);
        patientVisit = (PatientVisit) result.getObjectResult();

        patientAdapter.performExpand();
        getSite().getPage().closeEditor(this, false);
    }

    private void savePvInfoData() throws Exception {
        Collection<PvInfoData> pvDataCollection = new HashSet<PvInfoData>();

        MapIterator it = combinedPvInfoMap.mapIterator();
        while (it.hasNext()) {
            it.next();
            CombinedPvInfo combinedPvInfo = (CombinedPvInfo) it.getValue();
            String value = "";

            if (combinedPvInfo.control instanceof Text) {
                value = ((Text) combinedPvInfo.control).getText();
            }
            else if (combinedPvInfo.control instanceof Combo) {
                String [] options = combinedPvInfo.pvInfo.getPossibleValues().split(
                    ";");
                int index = ((Combo) combinedPvInfo.control).getSelectionIndex();
                if (index >= 0) {
                    Assert.isTrue(index < options.length,
                        "Invalid combo box selection " + index);
                    value = options[index];
                }
            }
            else if (combinedPvInfo.control instanceof DateTimeWidget) {
                value = ((DateTimeWidget) combinedPvInfo.control).getText();
            }

            if ((value == null) || (value.length() == 0)) continue;

            PvInfoData pvInfoData;

            if (combinedPvInfo.pvInfoData == null) {
                pvInfoData = new PvInfoData();
                pvInfoData.setPvInfo(combinedPvInfo.pvInfo);
                pvInfoData.setPatientVisit(patientVisit);
            }
            else {
                pvInfoData = combinedPvInfo.pvInfoData;
            }
            pvInfoData.setValue(value);
            pvDataCollection.add(pvInfoData);
        }

        if (pvDataCollection.size() == 0) return;

        SDKQuery query;
        SDKQueryResult result;
        Collection<PvInfoData> savedPvDataCollection = new HashSet<PvInfoData>();
        Iterator<PvInfoData> itr = pvDataCollection.iterator();

        while (itr.hasNext()) {
            PvInfoData pvInfoData = itr.next();
            if (pvInfoData.getId() == null) {
                query = new InsertExampleQuery(pvInfoData);
            }
            else {
                query = new UpdateExampleQuery(pvInfoData);
            }

            result = appService.executeQuery(query);
            savedPvDataCollection.add((PvInfoData) result.getObjectResult());
        }

        patientVisit.setPvInfoDataCollection(pvDataCollection);
    }

    private boolean checkVisitDateDrawnUnique() throws ApplicationException {
        WritableApplicationService appService = patientVisitAdapter.getAppService();
        Patient patient = ((PatientAdapter) patientVisitAdapter.getParent()).getPatient();

        HQLCriteria c = new HQLCriteria(
            "from edu.ualberta.med.biobank.model.PatientVisit as v "
                + "inner join fetch v.patient " + "where v.patient.id='"
                + patient.getId() + "' " + "and v.dateDrawn = '"
                + patientVisit.getDateDrawn() + "'");

        List<Object> results = appService.query(c);

        if (results.size() > 0) {
            Display.getDefault().asyncExec(new Runnable() {
                public void run() {
                    MessageDialog.openError(
                        PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                        "Patient Visit Number Problem",
                        "A patient visit with number \""
                            + patientVisit.getDateDrawn()
                            + "\" already exists.");
                }
            });
            return false;
        }

        return true;
    }

    @Override
    protected void cancelForm() {

    }
}
