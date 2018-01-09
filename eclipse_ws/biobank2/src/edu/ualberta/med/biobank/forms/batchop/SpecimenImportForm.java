package edu.ualberta.med.biobank.forms.batchop;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PartInitException;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.common.action.batchoperation.IBatchOpInputPojo;
import edu.ualberta.med.biobank.common.batchoperation.IBatchOpPojoReader;
import edu.ualberta.med.biobank.common.batchoperation.specimen.GrandchildSpecimenBatchOpPojoReader;
import edu.ualberta.med.biobank.common.batchoperation.specimen.SpecimenPojoReaderFactory;
import edu.ualberta.med.biobank.model.Center;

public class SpecimenImportForm extends ImportForm {

    //private static Logger log = LoggerFactory.getLogger(SpecimenImportForm.class);

    private static final I18n i18n = I18nFactory.getI18n(SpecimenImportForm.class);

    @SuppressWarnings("nls")
    public static final String ID = "edu.ualberta.med.biobank.forms.SpecimenImportForm";

    @SuppressWarnings("nls")
    private static final String FORM_TITLE = i18n.tr("Specimen Import");

    @SuppressWarnings("nls")
    private static final String IMPORT_GRANDCHILD_SPECIMENS_LABEL = i18n.tr("Import grandchild specimens");

    private boolean importGrandchild;

    public SpecimenImportForm() {
        super(FORM_TITLE);
    }

    @Override
    protected void createFormContent() throws Exception {
        createCheckbox();
        super.createFormContent();
    }

    private void createCheckbox() {
        Composite client = toolkit.createComposite(page);
        client.setLayout(new GridLayout(2, false));
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(client);

        Button importGrandchildSpecimensButton = new Button(client, SWT.CHECK);
        importGrandchildSpecimensButton.setText(IMPORT_GRANDCHILD_SPECIMENS_LABEL);
        toolkit.adapt(importGrandchildSpecimensButton, true, true);

        importGrandchildSpecimensButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                Button btn = (Button) event.getSource();
                importGrandchild = btn.getSelection();
            }
        });
    }

    @Override
    protected IBatchOpPojoReader<? extends IBatchOpInputPojo> getCsvPojoReader(Center center,
                                                                               String csvFilename,
                                                                               String[] csvHeaders) {
        if (importGrandchild) {
            return new GrandchildSpecimenBatchOpPojoReader(center, csvFilename);
        }
        return SpecimenPojoReaderFactory.createPojoReader(center, csvFilename, csvHeaders);
    }

    @Override
    public void openForm(Integer batchOpId, boolean focusOnEditor) throws PartInitException {
        if (importGrandchild) {
            GrandchildSpecimenBatchOpViewForm.openForm(batchOpId, focusOnEditor);
            return;
        }
        SpecimenBatchOpViewForm.openForm(batchOpId, focusOnEditor);
    }

}