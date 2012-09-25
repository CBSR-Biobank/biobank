package edu.ualberta.med.biobank.widgets.infotables;

import java.util.Collection;
import java.util.List;

import org.eclipse.swt.widgets.Composite;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.SessionManager;
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
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.treeview.AdapterBase;
import edu.ualberta.med.biobank.treeview.SpecimenAdapter;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class SimpleSpecimenTable
    extends DefaultAbstractInfoTableWidget<Specimen> {
    public static final I18n i18n = I18nFactory
        .getI18n(SimpleSpecimenTable.class);

    public static final int ROWS_PER_PAGE = 10;

    @SuppressWarnings("nls")
    private static final String[] HEADINGS = new String[] {
        i18n.tr("Inventory ID") };

    public SimpleSpecimenTable(Composite parent,        List<Specimen> specimens) {
        super(parent, HEADINGS, ROWS_PER_PAGE);

        setList(specimens);

        this.addClickListener(new IInfoTableDoubleClickItemListener<Specimen>() {
            @Override
            public void doubleClick(InfoTableEvent<Specimen> event) {
                Specimen s = ((Specimen) ((InfoTableSelection) event
                    .getSelection()).getObject());
                AdapterBase.openForm(
                    new FormInput(
                        new SpecimenAdapter(null,
                            new SpecimenWrapper(SessionManager
                                .getAppService(), s))),
                    SpecimenViewForm.ID);
            }
        });
    }

    @Override
    protected BgcLabelProvider getLabelProvider() {
        return new BgcLabelProvider() {
            @SuppressWarnings("nls")
            @Override
            public String getColumnText(Object element, int columnIndex) {
                Specimen specimen = (Specimen) element;
                switch (columnIndex) {
                case 0:
                    return specimen.getInventoryId();
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
    protected Boolean canEdit(Specimen target)
        throws ApplicationException {
        return false;
    }

    @Override
    protected Boolean canDelete(Specimen target)
        throws ApplicationException {
        return false;
    }

    @Override
    protected Boolean canView(Specimen target)
        throws ApplicationException {
        return true;
    };
}
