package edu.ualberta.med.biobank.widgets.infotables;

import java.util.List;

import org.eclipse.swt.widgets.Composite;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.common.action.patient.PatientGetSimpleCollectionEventInfosAction.SimpleCEventInfo;
import edu.ualberta.med.biobank.common.util.StringUtil;
import edu.ualberta.med.biobank.forms.CollectionEventViewForm;
import edu.ualberta.med.biobank.forms.input.FormInput;
import edu.ualberta.med.biobank.gui.common.widgets.BgcLabelProvider;
import edu.ualberta.med.biobank.gui.common.widgets.BgcTableSorter;
import edu.ualberta.med.biobank.gui.common.widgets.DefaultAbstractInfoTableWidget;
import edu.ualberta.med.biobank.gui.common.widgets.IInfoTableDoubleClickItemListener;
import edu.ualberta.med.biobank.gui.common.widgets.InfoTableEvent;
import edu.ualberta.med.biobank.gui.common.widgets.InfoTableSelection;
import edu.ualberta.med.biobank.model.EventAttr;
import edu.ualberta.med.biobank.treeview.AdapterBase;
import edu.ualberta.med.biobank.treeview.patient.CollectionEventAdapter;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class EventAttrTable
    extends DefaultAbstractInfoTableWidget<EventAttr> {
    public static final I18n i18n = I18nFactory.getI18n(EventAttrTable.class);

    public static final int ROWS_PER_PAGE = 10;

    @SuppressWarnings("nls")
    private static final String[] HEADINGS = new String[] {
        i18n.tr("Patient Number"),
        i18n.tr("Visit Number"),
        i18n.tr("Attribute Name"),
        i18n.tr("Attribute Value")
    };

    public EventAttrTable(Composite parent, List<EventAttr> eventAttrs) {
        super(parent, HEADINGS, ROWS_PER_PAGE);

        setList(eventAttrs);

        this.addClickListener(new IInfoTableDoubleClickItemListener<EventAttr>() {
            @Override
            public void doubleClick(InfoTableEvent<EventAttr> event) {
                EventAttr eventAttr = (EventAttr) ((InfoTableSelection) event.getSelection()).getObject();
                SimpleCEventInfo ceventInfo = new SimpleCEventInfo();
                ceventInfo.cevent = eventAttr.getCollectionEvent();
                ceventInfo.cevent.setPatient(ceventInfo.cevent.getPatient());
                AdapterBase.openForm(new FormInput(new CollectionEventAdapter(null, ceventInfo)),
                    CollectionEventViewForm.ID);
            }
        });
    }

    @Override
    protected BgcLabelProvider getLabelProvider() {
        return new BgcLabelProvider() {
            @Override
            public String getColumnText(Object element, int columnIndex) {
                EventAttr eventAttr = (EventAttr) element;
                switch (columnIndex) {
                case 0:
                    return eventAttr.getCollectionEvent().getPatient().getPnumber();
                case 1:
                    return eventAttr.getCollectionEvent().getVisitNumber().toString();
                case 2:
                    return eventAttr.getStudyEventAttr().getGlobalEventAttr().getLabel();
                case 3:
                    return eventAttr.getValue();
                default:
                    return StringUtil.EMPTY_STRING;
                }
            }
        };
    }

    @Override
    protected BgcTableSorter getTableSorter() {
        return null;
    }

    @Override
    protected Boolean canEdit(EventAttr target)
        throws ApplicationException {
        return false;
    }

    @Override
    protected Boolean canDelete(EventAttr target)
        throws ApplicationException {
        return false;
    }

    @Override
    protected Boolean canView(EventAttr target)
        throws ApplicationException {
        return true;
    }

}
