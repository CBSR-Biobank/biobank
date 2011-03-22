package edu.ualberta.med.biobank.exporters;

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.ITableLabelProvider;

public interface ReportExporter {
    public String getName();

    /**
     * @return null if no path is required. Return an array of extension filter
     *         strings that are allowed, e.g. { "*.csv", "*.*" }.
     */
    public String[] getPathFilterExtensions();

    public void canExport(Data data) throws Exception;

    /**
     * Called when data is to be exported.
     * 
     * @param data to export
     * @param path optional path to export to
     * @param labelProvider used to format the <code>Data.getResults</code>
     * @param monitor watch this in case the export is to be cancelled
     * @throws Exception
     */
    public void export(Data data, String path,
        ITableLabelProvider labelProvider, IProgressMonitor monitor)
        throws Exception;

    public static class Data {
        private List<String> headers;
        private List<String> comments;
        private String title;
        private List<?> results;

        public void setHeaders(List<String> headers) {
            this.headers = headers;
        }

        public List<String> getHeaders() {
            return headers;
        }

        public void setComments(List<String> comments) {
            this.comments = comments;
        }

        public List<String> getComments() {
            return comments;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getTitle() {
            return title;
        }

        public void setResults(List<?> results) {
            this.results = results;
        }

        public List<?> getResults() {
            return results;
        }
    }
}
