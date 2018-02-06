package edu.ualberta.med.biobank.forms;

import java.util.ArrayList;
import java.util.Set;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PartInitException;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.common.util.StringUtil;
import edu.ualberta.med.biobank.forms.batchop.SpecimenBatchOpViewForm;
import edu.ualberta.med.biobank.gui.common.widgets.BgcLabelProvider;
import edu.ualberta.med.biobank.gui.common.widgets.BgcTableSorter;
import edu.ualberta.med.biobank.gui.common.widgets.DefaultAbstractInfoTableWidget;
import edu.ualberta.med.biobank.gui.common.widgets.IInfoTableDoubleClickItemListener;
import edu.ualberta.med.biobank.gui.common.widgets.InfoTableEvent;
import edu.ualberta.med.biobank.gui.common.widgets.InfoTableSelection;
import edu.ualberta.med.biobank.model.BatchOperation;
import edu.ualberta.med.biobank.widgets.infotables.SpecimenTableSimple;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class BatchOperationsTable extends DefaultAbstractInfoTableWidget<BatchOperation> {

    public static final I18n i18n = I18nFactory.getI18n(SpecimenTableSimple.class);

    public static final int ROWS_PER_PAGE = 10;

    @SuppressWarnings("nls")
    private static final String[] HEADINGS =
        new String[] {
                      i18n.tr("File name"),
                      i18n.tr("User"),
                      i18n.tr("Date"),
        };

    public BatchOperationsTable(Composite parent, Set<BatchOperation> batchOperations) {
        super(parent, HEADINGS, ROWS_PER_PAGE);
        setList(new ArrayList<BatchOperation>(batchOperations));

        this.addClickListener(new IInfoTableDoubleClickItemListener<BatchOperation>() {
            @Override
            public void doubleClick(InfoTableEvent<BatchOperation> event) {
                BatchOperation operation = ((BatchOperation)
                    ((InfoTableSelection) event.getSelection()).getObject());
                try {
                    SpecimenBatchOpViewForm.openForm(operation.getId(), true);
                } catch (PartInitException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @Override
    protected BgcLabelProvider getLabelProvider() {
        return new BgcLabelProvider() {
            @Override
            public String getColumnText(Object element, int columnIndex) {
                BatchOperation operation = (BatchOperation) element;
                switch (columnIndex) {
                case 0:
                    return operation.getInput().getMetaData().getName();
                case 1:
                    return operation.getExecutedBy().getFullName();
                case 2:
                    return operation.getTimeExecuted().toString();
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
    protected Boolean canView(BatchOperation target) throws ApplicationException {
        return true;
    }

    @Override
    protected Boolean canEdit(BatchOperation target) throws ApplicationException {
        return false;
    }

    @Override
    protected Boolean canDelete(BatchOperation target) throws ApplicationException {
        return false;
    }
}
