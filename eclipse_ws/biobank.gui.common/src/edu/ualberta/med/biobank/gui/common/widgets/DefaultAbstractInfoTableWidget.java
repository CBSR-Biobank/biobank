package edu.ualberta.med.biobank.gui.common.widgets;

import gov.nih.nci.system.applicationservice.ApplicationException;

import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Composite;

public abstract class DefaultAbstractInfoTableWidget<T>
    extends AbstractInfoTableWidget<T> {
    public DefaultAbstractInfoTableWidget(Composite parent, String[] headings,
        int rowsPerPage) {
        super(parent, headings, null, rowsPerPage);
    }

    public void setSelection(T selection) {
        if (selection != null) {
            tableViewer.setSelection(new StructuredSelection(selection));
        }
    }

    @Override
    public void firstPage() {
        setList(getList());
    }

    @Override
    public void prevPage() {
        setList(getList());
    }

    @Override
    public void nextPage() {
        setList(getList());
    }

    @Override
    public void lastPage() {
        setList(getList());
    }

    @Override
    protected BgcTableSorter getTableSorter() {
        return null;
    }

    @Override
    public void reload() throws ApplicationException {
        setList(getList());
    }
}
