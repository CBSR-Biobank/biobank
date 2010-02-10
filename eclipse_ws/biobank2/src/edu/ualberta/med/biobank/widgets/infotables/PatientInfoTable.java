package edu.ualberta.med.biobank.widgets.infotables;

import java.util.Collection;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.views.ShipmentAdministrationView;
import edu.ualberta.med.biobank.widgets.BiobankLabelProvider;

public class PatientInfoTable extends InfoTableWidget<PatientWrapper> {

    class TableRowData {
        PatientWrapper patient;
        String pnumber;
        String studyNameShort;

        @Override
        public String toString() {
            return StringUtils.join(new String[] { pnumber, studyNameShort },
                "\t");
        }
    }

    class TableSorter extends BiobankTableSorter {
        @Override
        public int compare(Viewer viewer, Object e1, Object e2) {
            TableRowData c1 = (TableRowData) ((BiobankCollectionModel) e1).o;
            TableRowData c2 = (TableRowData) ((BiobankCollectionModel) e2).o;
            if ((c1 == null) || (c2 == null)) {
                return -1;
            }
            int rc = 0;
            switch (propertyIndex) {
            case 0:
                rc = c1.pnumber.compareTo(c2.pnumber);
                break;
            case 1:
                rc = c1.studyNameShort.compareTo(c2.studyNameShort);
                break;
            default:
                rc = 0;
            }
            // If descending order, flip the direction
            if (direction == 1) {
                rc = -rc;
            }
            return rc;
        }

    }

    private static final String[] HEADINGS = new String[] { "Patient Number",
        "Study" };

    private static final int[] BOUNDS = new int[] { 150, 150, -1, -1, -1, -1 };

    public PatientInfoTable(Composite parent,
        Collection<PatientWrapper> patients) {
        super(parent, true, patients, HEADINGS, BOUNDS);
        setSorter(new TableSorter());
        addClipboadCopySupport();

        tableViewer.addDoubleClickListener(new IDoubleClickListener() {
            @Override
            public void doubleClick(DoubleClickEvent event) {
                Object selection = event.getSelection();
                BiobankCollectionModel obj = (BiobankCollectionModel) ((StructuredSelection) selection)
                    .getFirstElement();
                Assert
                    .isTrue(obj.o instanceof TableRowData,
                        "Invalid class where patient expected: "
                            + obj.o.getClass());

                TableRowData item = (TableRowData) obj.o;
                ShipmentAdministrationView.currentInstance
                    .displayPatient(item.patient);
            }
        });
    }

    @Override
    public BiobankLabelProvider getLabelProvider() {
        return new BiobankLabelProvider() {
            @Override
            public String getColumnText(Object element, int columnIndex) {
                TableRowData contact = (TableRowData) ((BiobankCollectionModel) element).o;
                if (contact == null)
                    return null;
                switch (columnIndex) {
                case 0:
                    return contact.pnumber;
                case 1:
                    return contact.studyNameShort;
                case 3:
                default:
                    return "";
                }
            }
        };
    }

    @Override
    public Object getCollectionModelObject(PatientWrapper patient)
        throws Exception {
        TableRowData info = new TableRowData();
        info.patient = patient;
        info.pnumber = patient.getPnumber();
        StudyWrapper study = patient.getStudy();
        if (study != null) {
            info.studyNameShort = study.getNameShort();
        } else {
            info.studyNameShort = new String();
        }
        return info;
    }

    @Override
    protected String getCollectionModelObjectToString(Object o) {
        if (o == null)
            return null;
        return ((TableRowData) o).toString();
    }
}
