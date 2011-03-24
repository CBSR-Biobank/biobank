package edu.ualberta.med.biobank.export;

import org.eclipse.jface.viewers.ITableLabelProvider;

public interface DataExporter {
    public String getName();

    /**
     * 
     * @param data to check if it can be exported
     * @throws DataExportException if this <code>DataExporter</code> does not
     *             expect to be able to export the given <code>Data</code>
     */
    public void canExport(Data data) throws DataExportException;

    /**
     * Called when data is to be exported.
     * 
     * @param data to export
     * @param labelProvider used to format the <code>Data.getResults</code>
     * @throws DataExportException
     */
    public void export(Data data, ITableLabelProvider labelProvider)
        throws DataExportException;
}
