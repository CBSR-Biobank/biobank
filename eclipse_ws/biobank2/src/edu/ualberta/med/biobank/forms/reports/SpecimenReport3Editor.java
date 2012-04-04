package edu.ualberta.med.biobank.forms.reports;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.supercsv.cellprocessor.ParseDate;
import org.supercsv.cellprocessor.constraint.LMinMax;
import org.supercsv.cellprocessor.constraint.StrNotNullOrEmpty;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.exception.SuperCSVException;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.io.ICsvBeanReader;
import org.supercsv.prefs.CsvPreference;

import edu.ualberta.med.biobank.gui.common.widgets.BgcFileBrowser;
import edu.ualberta.med.biobank.gui.common.widgets.IBgcFileBrowserListener;
import edu.ualberta.med.biobank.server.reports.RequestData;

public class SpecimenReport3Editor extends ReportsEditor implements
    IBgcFileBrowserListener {

    public static String ID =
        "edu.ualberta.med.biobank.editors.AliquotRequestEditor";

    private BgcFileBrowser csvSelector;

    @Override
    protected void createOptionSection(Composite parent) {
        parent.setLayout(new GridLayout(1, false));
        parent.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true,
            false));
        csvSelector = new BgcFileBrowser(parent,
            "CSV File", SWT.NONE,
            new String[] { "*.csv" });
        csvSelector.addFileSelectedListener(this);
        csvSelector.adaptToToolkit(toolkit, true);
        generateButton.setEnabled(false);
    }

    @Override
    public void fileSelected(String filename) {
        // enable the button once a file has been selected
        generateButton.setEnabled(true);
    }

    @Override
    protected void initReport() throws Exception {
        report.setParams(parseCSV());
    }

    @Override
    protected List<Object> getPrintParams() {
        return Arrays.asList(new Object[] { csvSelector.getFilePath() });
    }

    protected List<Object> parseCSV() throws Exception {
        ICsvBeanReader reader = new CsvBeanReader(new FileReader(
            csvSelector.getFilePath()), CsvPreference.EXCEL_PREFERENCE);

        final CellProcessor[] processors = new CellProcessor[] {
            new StrNotNullOrEmpty(), new ParseDate("yyyy-MM-dd"),
            new StrNotNullOrEmpty(), new LMinMax(1, Long.MAX_VALUE) };

        List<Object> requests = new ArrayList<Object>();

        try {
            String[] header = new String[] { "pnumber", "dateDrawn",
                "specimenTypeNameShort", "maxAliquots" };
            RequestData request;
            while ((request = reader.read(RequestData.class, header,
                processors)) != null) {
                requests.add(request);
            }
        } catch (SuperCSVException e) {
            throw new Exception("Parse error at line " + reader.getLineNumber()
                + "\n" + e.getCsvContext());
        } finally {
            reader.close();
        }
        return requests;
    }

    @Override
    protected String[] getColumnNames() {
        return new String[] { "Patient",
            "Inventory ID",
            "Date Drawn",
            "Type",
            "Location",
            "Activity Status" };
    }

    @Override
    protected List<String> getParamNames() {
        List<String> paramName = new ArrayList<String>();
        paramName.add("CSV File");
        return paramName;
    }

    @Override
    public void setValues() throws Exception {
        csvSelector.reset();
        super.setValues();
    }
}