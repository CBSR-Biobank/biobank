package edu.ualberta.med.biobank.forms.reports;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.client.reports.AliquotRequest;
import edu.ualberta.med.biobank.widgets.FileBrowser;

public class AliquotRequestEditor extends ReportsEditor<AliquotRequest> {

    public static String ID = "edu.ualberta.med.biobank.editors.AliquotRequestEditor";

    private FileBrowser csvSelector;

    @Override
    protected int[] getColumnWidths() {
        return new int[] { 100, 100, 100, 100, 100 };
    }

    @Override
    protected void createOptionSection(Composite parent) {
        csvSelector = createFileBrowserOption("CSV File", parent);
    }

    @Override
    protected List<Object> getParams() throws Exception {
        return parseCSV();
    }

    protected FileBrowser createFileBrowserOption(String fieldLabel,
        Composite parent) {
        widgetCreator.createLabel(parent, fieldLabel);
        FileBrowser widget = new FileBrowser(parent, SWT.NONE);
        toolkit.adapt(widget, true, true);
        return widget;
    }

    protected List<Object> parseCSV() throws Exception {
        List<Object> params = new ArrayList<Object>();
        String csv = csvSelector.getText();
        if (csv != null) {
            StringTokenizer stnewline = new StringTokenizer(csv, "\n");
            int lines = 0;
            while (stnewline.hasMoreTokens()) {
                StringTokenizer stseparator = new StringTokenizer(
                    stnewline.nextToken(), ",\" ");
                lines++;
                if (stseparator.countTokens() != 4)
                    throw new Exception("Failed to parse CSV: Line " + lines
                        + " \n4 Columns Required: " + stseparator.countTokens()
                        + " found.");
                else {
                    while (stseparator.hasMoreTokens())
                        params.add(stseparator.nextToken());
                }
            }
        } else
            throw new Exception(
                "Not a valid file. Please select a valid CSV and try again.");
        return params;
    }
}
