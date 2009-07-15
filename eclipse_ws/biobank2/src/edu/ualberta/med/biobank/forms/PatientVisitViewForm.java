package edu.ualberta.med.biobank.forms;

import java.text.SimpleDateFormat;
import java.util.Collection;

import org.apache.commons.collections.MapIterator;
import org.apache.commons.collections.map.ListOrderedMap;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.forms.input.FormInput;
import edu.ualberta.med.biobank.model.ContainerPosition;
import edu.ualberta.med.biobank.model.ModelUtils;
import edu.ualberta.med.biobank.model.PatientVisit;
import edu.ualberta.med.biobank.model.PvInfo;
import edu.ualberta.med.biobank.model.PvInfoData;
import edu.ualberta.med.biobank.model.Sample;
import edu.ualberta.med.biobank.model.SamplePosition;
import edu.ualberta.med.biobank.model.StorageContainer;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.treeview.Node;
import edu.ualberta.med.biobank.treeview.PatientVisitAdapter;
import edu.ualberta.med.biobank.treeview.StudyAdapter;
import edu.ualberta.med.biobank.widgets.BiobankCollectionTable;

public class PatientVisitViewForm extends BiobankViewForm {

    public class SampleLabelProvider extends LabelProvider implements
        ITableLabelProvider {

        @Override
        public boolean isLabelProperty(Object element, String property) {
            return false;
        }

        @Override
        public Image getColumnImage(Object element, int columnIndex) {
            return null;
        }

        @Override
        public String getColumnText(Object element, int columnIndex) {
            final Sample sample = (Sample) element;
            switch (columnIndex) {
            case 0:
                return sample.getInventoryId();
            case 1:
                return sample.getSampleType() == null ? "" : sample
                    .getSampleType().getName();
            case 2:
                return getSamplePosition(sample);
            case 3:
                return sample.getProcessDate() == null ? ""
                    : new SimpleDateFormat(BioBankPlugin.DATE_TIME_FORMAT)
                        .format(sample.getProcessDate());
            case 4:
                return sample.getAvailable() == null ? "" : sample
                    .getAvailable().toString();
            case 5:
                return sample.getAvailable() == null ? "" : sample
                    .getAvailable().toString();
            case 6:
                return sample.getComment() == null ? "" : sample.getComment();
            }
            return null;
        }

    }

    public static final String ID = "edu.ualberta.med.biobank.forms.PatientVisitViewForm";

    private PatientVisitAdapter patientVisitAdapter;

    private PatientVisit patientVisit;

    private BiobankCollectionTable samplesTable;

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

    public String getSamplePosition(Sample sample) {
        SamplePosition position = sample.getSamplePosition();
        if (position == null) {
            return "no position assigned";
        } else {
            String positionString = position.getPositionDimensionOne() + ""
                + +position.getPositionDimensionTwo();
            StorageContainer container = position.getStorageContainer();
            System.out.println(container.getBarcode());
            ContainerPosition containerPosition = container
                .getLocatedAtPosition();
            StorageContainer parent = containerPosition.getParentContainer();
            while (parent != null) {
                positionString = containerPosition.getPositionDimensionOne()
                    + "" + +containerPosition.getPositionDimensionTwo() + ":"
                    + positionString;
                System.out.println("in container " + parent.getBarcode() + ":"
                    + positionString);
                container = parent;
                containerPosition = parent.getLocatedAtPosition();
                parent = containerPosition.getParentContainer();
            }
            positionString = container.getBarcode() + ":" + positionString;
            return positionString;
        }
    }

    @Override
    public void init(IEditorSite editorSite, IEditorInput input)
        throws PartInitException {
        super.init(editorSite, input);

        Node node = ((FormInput) input).getNode();
        Assert.isNotNull(node, "Null editor input");

        patientVisitAdapter = (PatientVisitAdapter) node;
        appService = patientVisitAdapter.getAppService();
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

        Study study = ((StudyAdapter) patientVisitAdapter.getParent()
            .getParent().getParent()).getStudy();

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

    }

    private void createSamplesSection() {
        Composite parent = createSectionWithClient("Samples");
        String[] headings = new String[] { "Inventory ID", "Type", "Position",
            "Process Date", "Available", "Available quantity", "Comment" };
        int[] bounds = new int[] { -1, 130, 150, 150, -1, -1, -1 };
        samplesTable = new BiobankCollectionTable(parent, SWT.NONE, headings,
            bounds, getSamples());
        samplesTable.getTableViewer().setLabelProvider(
            new SampleLabelProvider());
        GridData tableData = ((GridData) samplesTable.getLayoutData());
        tableData.horizontalSpan = 2;
        tableData.heightHint = 500;
        samplesTable.adaptToToolkit(toolkit);
        toolkit.paintBordersFor(samplesTable);

        // samplesTable.getTableViewer().addDoubleClickListener(
        // FormUtils.getBiobankCollectionDoubleClickListener());

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
        // FIXME update all pvinfos ?
        samplesTable.getTableViewer().setInput(getSamples());
    }

    private void retrievePatientVisit() {
        try {
            patientVisit = (PatientVisit) ModelUtils.getObjectWithId(
                patientVisitAdapter.getAppService(), PatientVisit.class,
                patientVisitAdapter.getPatientVisit().getId());
            patientVisitAdapter.setPatientVisit(patientVisit);
        } catch (Exception ex) {
            SessionManager.getLogger().error(
                "Error while retrieving patient visit "
                    + patientVisitAdapter.getPatientVisit().getDateDrawn(), ex);
        }
    }

    private Sample[] getSamples() {
        // hack required here because xxx.getXxxxCollection().toArray(new
        // Xxx[0])
        // returns Object[].
        Collection<Sample> sampleList = patientVisit.getSampleCollection();
        Sample[] samples = new Sample[sampleList.size()];
        int i = 0;
        for (Sample s : sampleList) {
            samples[i] = s;
            i++;
        }
        return samples;
    }
}
