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
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.gui.common.widgets.BgcFileBrowser;
import edu.ualberta.med.biobank.gui.common.widgets.IBgcFileBrowserListener;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.server.reports.RequestData;

public class SpecimenReport3Editor extends ReportsEditor implements
    IBgcFileBrowserListener {
    private static final I18n i18n = I18nFactory
        .getI18n(SpecimenReport3Editor.class);

    @SuppressWarnings("nls")
    public static String ID =
        "edu.ualberta.med.biobank.editors.AliquotRequestEditor";

    private BgcFileBrowser csvSelector;

    @SuppressWarnings("nls")
    @Override
    protected void createOptionSection(Composite parent) {
        parent.setLayout(new GridLayout(1, false));
        parent.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true,
            false));
        csvSelector = new BgcFileBrowser(parent,
            // dialog title
            i18n.tr("CSV File"), SWT.NONE,
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

    @SuppressWarnings("nls")
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
            throw new Exception(
                // exception message
                i18n.tr("Parse error at line {0}\n{1}", reader.getLineNumber(),
                    e.getCsvContext()));
        } finally {
            reader.close();
        }
        return requests;
    }

    @SuppressWarnings("nls")
    @Override
    protected String[] getColumnNames() {
        return new String[] {
            Patient.NAME.format(1).toString(),
            Specimen.Property.INVENTORY_ID.toString(),
            // table column name
            i18n.tr("Date Drawn"),
            // table column name
            i18n.trc("specimen", "Type"),
            // table column name
            i18n.trc("specimen", "Location"),
            ActivityStatus.NAME.format(1).toString() };
    }

    @SuppressWarnings("nls")
    @Override
    protected List<String> getParamNames() {
        List<String> paramName = new ArrayList<String>();
        paramName.add(
            // label
            i18n.tr("CSV File"));
        return paramName;
    }

    @Override
    public void setValues() throws Exception {
        csvSelector.reset();
        super.setValues();
    }
}