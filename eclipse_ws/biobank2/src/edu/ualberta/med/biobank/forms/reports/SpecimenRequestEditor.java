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

import edu.ualberta.med.biobank.server.reports.SpecimenRequest;
import edu.ualberta.med.biobank.widgets.FileBrowser;

public class SpecimenRequestEditor extends ReportsEditor {

    public static String ID = "edu.ualberta.med.biobank.editors.AliquotRequestEditor"; //$NON-NLS-1$

    private FileBrowser csvSelector;

    @Override
    protected void createOptionSection(Composite parent) {
        parent.setLayout(new GridLayout(1, false));
        parent.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true,
            false));
        csvSelector = new FileBrowser(parent,
            Messages.AliquotRequestEditor_browse_label, SWT.NONE);
        csvSelector.adaptToToolkit(toolkit, true);
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
            new StrNotNullOrEmpty(), new ParseDate("yyyy-MM-dd"), //$NON-NLS-1$
            new StrNotNullOrEmpty(), new LMinMax(1, Long.MAX_VALUE) };

        List<Object> requests = new ArrayList<Object>();

        try {
            String[] header = new String[] { "pnumber", "dateDrawn", //$NON-NLS-1$ //$NON-NLS-2$
                "specimenTypeNameShort", "maxAliquots" }; //$NON-NLS-1$ //$NON-NLS-2$
            SpecimenRequest request;
            while ((request = reader.read(SpecimenRequest.class, header,
                processors)) != null) {
                requests.add(request);
            }
        } catch (SuperCSVException e) {
            throw new Exception("Parse error at line " + reader.getLineNumber() //$NON-NLS-1$
                + "\n" + e.getCsvContext()); //$NON-NLS-1$
        } finally {
            reader.close();
        }
        return requests;
    }

    @Override
    protected String[] getColumnNames() {
        return new String[] { Messages.AliquotRequestEditor_patient_label,
            Messages.AliquotRequestEditor_inventory_id_label,
            Messages.AliquotRequestEditor_drawn_label,
            Messages.AliquotRequestEditor_type_label,
            Messages.AliquotRequestEditor_location_label,
            Messages.AliquotRequestEditor_status_label };
    }

    @Override
    protected List<String> getParamNames() {
        List<String> paramName = new ArrayList<String>();
        paramName.add(Messages.AliquotRequestEditor_browse_label);
        return paramName;
    }

    @Override
    protected void onReset() throws Exception {
        csvSelector.reset();
        super.onReset();
    }
}