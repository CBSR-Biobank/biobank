package edu.ualberta.med.biobank.widgets.infotables;

import java.util.List;

import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.action.patient.PatientGetCollectionEventInfosAction.PatientCEventInfo;
import edu.ualberta.med.biobank.common.formatters.NumberFormatter;
import edu.ualberta.med.biobank.common.permission.collectionEvent.CollectionEventDeletePermission;
import edu.ualberta.med.biobank.common.permission.collectionEvent.CollectionEventReadPermission;
import edu.ualberta.med.biobank.common.permission.collectionEvent.CollectionEventUpdatePermission;
import edu.ualberta.med.biobank.common.util.StringUtil;
import edu.ualberta.med.biobank.gui.common.widgets.AbstractInfoTableWidget;
import edu.ualberta.med.biobank.gui.common.widgets.BgcLabelProvider;
import edu.ualberta.med.biobank.model.CollectionEvent;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class NewCollectionEventInfoTable extends
    InfoTableWidget<PatientCEventInfo> {

    @SuppressWarnings("nls")
    private static final String[] HEADINGS = new String[] {
        i18n.tr("Visit"),
        i18n.tr("Sources"),
        i18n.tr("Aliquots"),
        i18n.tr("Comment") };

    public NewCollectionEventInfoTable(Composite parent,
        List<PatientCEventInfo> collection) {
        super(parent, collection, HEADINGS, 10, CollectionEvent.class);
    }

    @Override
    protected BgcLabelProvider getLabelProvider() {
        return new BgcLabelProvider() {
            @Override
            public String getColumnText(Object element, int columnIndex) {
                PatientCEventInfo info =
                    (PatientCEventInfo) ((BiobankCollectionModel) element).o;
                if (info == null) {
                    if (columnIndex == 0) {
                        return AbstractInfoTableWidget.LOADING;
                    }
                    return StringUtil.EMPTY_STRING;
                }
                switch (columnIndex) {
                case 0:
                    return info.cevent.getVisitNumber().toString();
                case 1:
                    return NumberFormatter.format(info.sourceSpecimenCount);
                case 2:
                    return NumberFormatter.format(info.aliquotedSpecimenCount);
                case 3:
                    return info.cevent.getComments().size() == 0 ? StringUtil.EMPTY_STRING
                        : StringUtil.EMPTY_STRING;

                default:
                    return StringUtil.EMPTY_STRING;
                }
            }
        };
    }

    @Override
    protected String getCollectionModelObjectToString(Object o) {
        if (o == null) return null;
        return ((PatientCEventInfo) o).toString();
    }

    @Override
    public PatientCEventInfo getSelection() {
        BiobankCollectionModel item = getSelectionInternal();
        if (item == null) return null;
        return (PatientCEventInfo) item.o;
    }

    @Override
    protected BiobankTableSorter getComparator() {
        return new BiobankTableSorter() {
            private static final long serialVersionUID = 1L;

            @Override
            public int compare(Object o1, Object o2) {
                if (o1 instanceof PatientCEventInfo
                    && o2 instanceof PatientCEventInfo) {
                    PatientCEventInfo p1 = (PatientCEventInfo) o1;
                    PatientCEventInfo p2 = (PatientCEventInfo) o2;
                    return p1.cevent.getVisitNumber()
                        .compareTo(p2.cevent.getVisitNumber());
                }
                return super.compare(01, o2);
            }
        };
    }

    @Override
    protected Boolean canEdit(PatientCEventInfo target)
        throws ApplicationException {
        return SessionManager.getAppService().isAllowed(
            new CollectionEventUpdatePermission(target.cevent.getId()));
    }

    @Override
    protected Boolean canDelete(PatientCEventInfo target)
        throws ApplicationException {
        return SessionManager.getAppService().isAllowed(
            new CollectionEventDeletePermission(target.cevent.getId()));
    }

    @Override
    protected Boolean canView(PatientCEventInfo target)
        throws ApplicationException {
        return SessionManager.getAppService().isAllowed(
            new CollectionEventReadPermission(target.cevent.getId()));
    }

}
