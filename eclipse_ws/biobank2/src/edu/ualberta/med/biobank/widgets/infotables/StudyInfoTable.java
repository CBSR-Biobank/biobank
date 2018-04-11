package edu.ualberta.med.biobank.widgets.infotables;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.formatters.NumberFormatter;
import edu.ualberta.med.biobank.common.permission.study.StudyDeletePermission;
import edu.ualberta.med.biobank.common.permission.study.StudyReadPermission;
import edu.ualberta.med.biobank.common.permission.study.StudyUpdatePermission;
import edu.ualberta.med.biobank.common.util.StringUtil;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.gui.common.widgets.AbstractInfoTableWidget;
import edu.ualberta.med.biobank.gui.common.widgets.BgcLabelProvider;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.CollectionEvent;
import edu.ualberta.med.biobank.model.HasName;
import edu.ualberta.med.biobank.model.HasNameShort;
import edu.ualberta.med.biobank.model.Patient;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class StudyInfoTable extends InfoTableWidget<StudyWrapper> {

    protected static class TableRowData {
        StudyWrapper study;
        String name;
        String nameShort;
        String status;
        Long patientCount;
        Long visitCount;

        @SuppressWarnings("nls")
        @Override
        public String toString() {
            return StringUtils.join(new String[] {
                name,
                nameShort,
                status,
                (patientCount != null) ? patientCount.toString()
                    : StringUtil.EMPTY_STRING,
                (visitCount != null) ? visitCount.toString()
                    : StringUtil.EMPTY_STRING }, "\t");
        }
    }

    private static final String[] HEADINGS = new String[] {
        HasName.PropertyName.NAME.toString(),
        HasNameShort.PropertyName.NAME_SHORT.toString(),
        ActivityStatus.NAME.singular().toString(),
        Patient.NAME.plural().toString(),
        CollectionEvent.NAME.plural().toString() };

    //OHSDEV - Research Group headings
    private static final String[] RG_HEADINGS = new String[] {
        HasName.PropertyName.NAME.toString(),
        HasNameShort.PropertyName.NAME_SHORT.toString(),
        ActivityStatus.NAME.singular().toString()};

    public StudyInfoTable(Composite parent, List<StudyWrapper> collection) {
        super(parent, collection, HEADINGS, 10, StudyWrapper.class);
    }

    //OHSDEV - Initialize the table with Research Group headings for form view
    public StudyInfoTable(Composite parent, List<StudyWrapper> collection, boolean isRG) {
        super(parent, collection, RG_HEADINGS, 10, StudyWrapper.class);
    }

    @Override
    protected BgcLabelProvider getLabelProvider() {
        return new BgcLabelProvider() {
            @Override
            public String getColumnText(Object element, int columnIndex) {
                TableRowData info =
                    (TableRowData) ((BiobankCollectionModel) element).o;
                if (info == null) {
                    if (columnIndex == 0) {
                        return AbstractInfoTableWidget.LOADING;
                    }
                    return StringUtil.EMPTY_STRING;
                }
                switch (columnIndex) {
                case 0:
                    return info.name;
                case 1:
                    return info.nameShort;
                case 2:
                    return (info.status != null) ? info.status
                        : StringUtil.EMPTY_STRING;
                case 3:
                    return NumberFormatter.format(info.patientCount);
                case 4:
                    return NumberFormatter.format(info.visitCount);
                default:
                    return StringUtil.EMPTY_STRING;
                }
            }
        };
    }

    @Override
    public Object getCollectionModelObject(Object study) throws Exception {
        TableRowData info = new TableRowData();
        info.study = (StudyWrapper) study;
        info.name = info.study.getName();
        info.nameShort = info.study.getNameShort();
        info.status = info.study.getActivityStatus().getName();
        if (info.status == null) {
            info.status = StringUtil.EMPTY_STRING;
        }
        info.patientCount = info.study.getPatientCount(true);
        info.visitCount = info.study.getCollectionEventCount();
        info.study.reload();
        return info;
    }

    @Override
    protected String getCollectionModelObjectToString(Object o) {
        if (o == null)
            return null;
        return ((TableRowData) o).toString();
    }

    @Override
    public StudyWrapper getSelection() {
        BiobankCollectionModel item = getSelectionInternal();
        if (item == null)
            return null;
        TableRowData row = (TableRowData) item.o;
        Assert.isNotNull(row);
        return row.study;
    }

    @Override
    protected BiobankTableSorter getComparator() {
        return null;
    }

    @Override
    protected Boolean canView(StudyWrapper target) throws ApplicationException {
        return SessionManager.getAppService().isAllowed(
            new StudyReadPermission(target.getId()));
    }

    @Override
    protected Boolean canEdit(StudyWrapper target) throws ApplicationException {
        return SessionManager.getAppService().isAllowed(
            new StudyUpdatePermission(target.getId()));
    }

    @Override
    protected Boolean canDelete(StudyWrapper target)
        throws ApplicationException {
        return SessionManager.getAppService().isAllowed(
            new StudyDeletePermission(target.getId()));
    }

}
