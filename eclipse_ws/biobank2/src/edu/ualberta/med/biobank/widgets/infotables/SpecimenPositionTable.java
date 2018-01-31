package edu.ualberta.med.biobank.widgets.infotables;

import java.util.List;

import org.eclipse.swt.widgets.Composite;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.action.batchoperation.specimen.position.SpecimenPositionBatchOpGetResult.SpecimenInfo;
import edu.ualberta.med.biobank.common.util.StringUtil;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import edu.ualberta.med.biobank.forms.SpecimenViewForm;
import edu.ualberta.med.biobank.forms.input.FormInput;
import edu.ualberta.med.biobank.gui.common.widgets.BgcLabelProvider;
import edu.ualberta.med.biobank.gui.common.widgets.BgcTableSorter;
import edu.ualberta.med.biobank.gui.common.widgets.DefaultAbstractInfoTableWidget;
import edu.ualberta.med.biobank.gui.common.widgets.IInfoTableDoubleClickItemListener;
import edu.ualberta.med.biobank.gui.common.widgets.InfoTableEvent;
import edu.ualberta.med.biobank.gui.common.widgets.InfoTableSelection;
import edu.ualberta.med.biobank.treeview.AdapterBase;
import edu.ualberta.med.biobank.treeview.SpecimenAdapter;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class SpecimenPositionTable extends DefaultAbstractInfoTableWidget<SpecimenInfo> {
    public static final I18n i18n = I18nFactory
        .getI18n(SpecimenTableSimple.class);

    public static final int ROWS_PER_PAGE = 10;

    @SuppressWarnings("nls")
    private static final String[] HEADINGS =
    new String[] {
                   i18n.tr("Inventory ID"),
                   i18n.tr("Label")
                   };

    public SpecimenPositionTable(Composite parent, List<SpecimenInfo> specimens) {
        super(parent, HEADINGS, ROWS_PER_PAGE);

        setList(specimens);

        this.addClickListener(new IInfoTableDoubleClickItemListener<SpecimenInfo>() {
            @Override
            public void doubleClick(InfoTableEvent<SpecimenInfo> event) {
                SpecimenInfo s = ((SpecimenInfo) ((InfoTableSelection) event.getSelection()).getObject());
                SpecimenWrapper wrapper = new SpecimenWrapper(SessionManager.getAppService(), s.specimen);
                SpecimenAdapter adapter = new SpecimenAdapter(null, wrapper);
                AdapterBase.openForm(new FormInput(adapter), SpecimenViewForm.ID);
            }
        });
    }

    @Override
    protected BgcLabelProvider getLabelProvider() {
        return new BgcLabelProvider() {
            @Override
            public String getColumnText(Object element, int columnIndex) {
                SpecimenInfo specimenInfo = (SpecimenInfo) element;
                switch (columnIndex) {
                case 0:
                    return specimenInfo.specimen.getInventoryId();
                case 1:
                    return specimenInfo.fullPositionString;
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
    protected Boolean canEdit(SpecimenInfo target) throws ApplicationException {
        return false;
    }

    @Override
    protected Boolean canDelete(SpecimenInfo target) throws ApplicationException {
        return false;
    }

    @Override
    protected Boolean canView(SpecimenInfo target) throws ApplicationException {
        return true;
    }
}
