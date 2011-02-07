package edu.ualberta.med.biobank.widgets.infotables;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.common.wrappers.ProcessingEventWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.widgets.BiobankLabelProvider;

public class PvListInfoTable extends InfoTableWidget<ProcessingEventWrapper> {

    private static final int PAGE_SIZE_ROWS = 24;

    protected class TableRowData {
        ProcessingEventWrapper pv;
        public String pnumber;
        public String studyNameShort;
        public String waybill;
        public Date departed;
        public String clinic;
        public Integer numSVs;
        public Integer numAliquots;

        @Override
        public String toString() {
            return StringUtils.join(new String[] { pnumber, studyNameShort,
                ((waybill == null) ? "None" : waybill), departed.toString(),
                clinic, numSVs.toString(), numAliquots.toString() }, "\t");
        }
    }

    private static final String[] HEADINGS = new String[] { "Patient Number",
        "Study", "Waybill", "Departed", "Clinic", "Source Vessels", "Aliquots" };

    public PvListInfoTable(Composite parent, List<ProcessingEventWrapper> pvs) {
        super(parent, pvs, HEADINGS, PAGE_SIZE_ROWS);
    }

    @Override
    protected BiobankLabelProvider getLabelProvider() {
        return new BiobankLabelProvider() {
            @Override
            public String getColumnText(Object element, int columnIndex) {
                TableRowData item = (TableRowData) ((BiobankCollectionModel) element).o;
                if (item == null) {
                    if (columnIndex == 0) {
                        return "loading...";
                    }
                    return "";
                }
                switch (columnIndex) {
                case 0:
                    return item.pnumber;
                case 1:
                    return item.studyNameShort;
                case 2:
                    return item.waybill;
                case 3:
                    return item.departed.toString();
                case 4:
                    return item.clinic;
                case 5:
                    return item.numSVs.toString();
                case 6:
                    return item.numAliquots.toString();
                default:
                    return "";
                }
            }
        };
    }

    @Override
    public TableRowData getCollectionModelObject(ProcessingEventWrapper pv)
        throws Exception {
        TableRowData info = new TableRowData();
        info.pv = pv;
        info.pnumber = pv.getPatient().getPnumber();
        StudyWrapper study = pv.getPatient().getStudy();
        if (study != null) {
            info.studyNameShort = study.getNameShort();
        } else {
            info.studyNameShort = new String();
        }
        info.waybill = pv.getShipment().getWaybill();
        if (info.waybill == null)
            info.waybill = "None";
        info.departed = pv.getShipment().getDeparted();
        info.clinic = pv.getShipment().getClinic().getNameShort();
        info.numSVs = pv.getPvSourceVesselCollection().size();
        info.numAliquots = pv.getAliquotCollection().size();
        return info;
    }

    @Override
    protected String getCollectionModelObjectToString(Object o) {
        if (o == null)
            return null;
        return ((TableRowData) o).toString();
    }

    @Override
    public ProcessingEventWrapper getSelection() {
        BiobankCollectionModel item = getSelectionInternal();
        if (item == null)
            return null;
        TableRowData row = (TableRowData) item.o;
        if (row != null) {
            return row.pv;
        }
        return null;
    }

    @Override
    protected BiobankTableSorter getComparator() {
        return null;
    }
}
