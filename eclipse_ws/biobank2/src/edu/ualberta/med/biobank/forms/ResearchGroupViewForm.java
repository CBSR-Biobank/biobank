package edu.ualberta.med.biobank.forms;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.supercsv.cellprocessor.ParseDate;
import org.supercsv.cellprocessor.constraint.LMinMax;
import org.supercsv.cellprocessor.constraint.StrNotNullOrEmpty;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.exception.SuperCSVException;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.io.ICsvBeanReader;
import org.supercsv.prefs.CsvPreference;

import edu.ualberta.med.biobank.Messages;
import edu.ualberta.med.biobank.common.wrappers.RequestWrapper;
import edu.ualberta.med.biobank.common.wrappers.ResearchGroupWrapper;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseText;
import edu.ualberta.med.biobank.server.reports.SpecimenRequest;
import edu.ualberta.med.biobank.treeview.admin.ResearchGroupAdapter;
import edu.ualberta.med.biobank.widgets.FileBrowser;

public class ResearchGroupViewForm extends AddressViewFormCommon {
    public static final String ID = "edu.ualberta.med.biobank.forms.ResearchGroupViewForm"; //$NON-NLS-1$

    private ResearchGroupAdapter researchGroupAdapter;

    private ResearchGroupWrapper researchGroup;

    // private ResearchGroupStudyInfoTable studiesTable;

    private BgcBaseText nameLabel;

    private BgcBaseText nameShortLabel;

    private BgcBaseText activityStatusLabel;

    private BgcBaseText commentLabel;

    private BgcBaseText studyLabel;

    private FileBrowser csvSelector;

    @Override
    protected void init() throws Exception {
        Assert.isTrue(adapter instanceof ResearchGroupAdapter,
            "Invalid editor input: object of type " //$NON-NLS-1$
                + adapter.getClass().getName());

        researchGroupAdapter = (ResearchGroupAdapter) adapter;
        researchGroup = researchGroupAdapter.getWrapper();
        researchGroup.reload();
        setPartName(Messages.getString("ResearchGroupViewForm.title", //$NON-NLS-1$
            researchGroup.getNameShort()));
    }

    @Override
    protected void createFormContent() throws Exception {
        form.setText(Messages.getString("ResearchGroupViewForm.title", //$NON-NLS-1$
            researchGroup.getName()));

        GridLayout layout = new GridLayout(1, false);
        page.setLayout(layout);
        page.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        createResearchGroupSection();
        createAddressSection(researchGroup);
        createUploadSection();
    }

    private void createUploadSection() {
        Composite client = toolkit.createComposite(page);
        client.setLayout(new GridLayout(2, false));
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(client);
        Label l = new Label(client, SWT.NONE);
        l.setText("Submit a request on behalf of this research group:");
        csvSelector = new FileBrowser(client, "CSV File", SWT.NONE);
    }

    public void saveRequest() throws Exception {
        RequestWrapper request = new RequestWrapper(appService);
        ICsvBeanReader reader = new CsvBeanReader(new FileReader(
            csvSelector.getFilePath()), CsvPreference.EXCEL_PREFERENCE);

        final CellProcessor[] processors = new CellProcessor[] {
            new StrNotNullOrEmpty(), new ParseDate("yyyy-MM-dd"),
            new StrNotNullOrEmpty(), new LMinMax(1, Long.MAX_VALUE) };

        List<Object> requests = new ArrayList<Object>();

        try {
            String[] header = new String[] { "pnumber", "dateDrawn",
                "specimenTypeNameShort", "maxAliquots" };
            SpecimenRequest srequest;
            while ((srequest = reader.read(SpecimenRequest.class, header,
                processors)) != null) {
                requests.add(srequest);
            }
        } catch (SuperCSVException e) {
            throw new Exception("Parse error at line " + reader.getLineNumber()
                + "\n" + e.getCsvContext());
        } finally {
            reader.close();
        }

        request.persist();

    }

    private void createResearchGroupSection() throws Exception {
        Composite client = toolkit.createComposite(page);
        client.setLayout(new GridLayout(2, false));
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(client);

        nameLabel = createReadOnlyLabelledField(client, SWT.NONE,
            Messages.getString("label.name")); //$NON-NLS-1$
        nameShortLabel = createReadOnlyLabelledField(client, SWT.NONE,
            Messages.getString("label.nameShort")); //$NON-NLS-1$
        studyLabel = createReadOnlyLabelledField(client, SWT.NONE,
            Messages.getString("label.study")); //$NON-NLS-1$
        activityStatusLabel = createReadOnlyLabelledField(client, SWT.NONE,
            Messages.getString("label.activity")); //$NON-NLS-1$
        commentLabel = createReadOnlyLabelledField(client, SWT.MULTI,
            Messages.getString("label.comments")); //$NON-NLS-1$

        setResearchGroupValues();
    }

    private void setResearchGroupValues() throws Exception {
        setTextValue(nameLabel, researchGroup.getName());
        setTextValue(nameShortLabel, researchGroup.getNameShort());
        setTextValue(studyLabel, researchGroup.getStudy());
        setTextValue(activityStatusLabel, researchGroup.getActivityStatus());
        setTextValue(commentLabel, researchGroup.getComment());
    }

    @Override
    public void reload() throws Exception {
        researchGroup.reload();
        setPartName(Messages.getString(
            "ResearchGroupViewForm.title", researchGroup.getName())); //$NON-NLS-1$
        form.setText(Messages.getString("ResearchGroupViewForm.title", //$NON-NLS-1$
            researchGroup.getName()));
        setResearchGroupValues();
        setAddressValues(researchGroup);
    }

}
