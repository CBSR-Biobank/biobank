package edu.ualberta.med.biobank.forms.batchop;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.common.action.specimen.SpecimenBriefInfo;
import edu.ualberta.med.biobank.forms.BatchOperationsTable;
import edu.ualberta.med.biobank.forms.BiobankViewForm;
import edu.ualberta.med.biobank.forms.input.FormInput;

public class SpecimenMultipleBatchOpViewForm extends BiobankViewForm {

    private static final I18n i18n = I18nFactory.getI18n(SpecimenMultipleBatchOpViewForm.class);

    @SuppressWarnings("nls")
    public static final String ID = "edu.ualberta.med.biobank.forms.MultipleBatchOpViewForm";

    private SpecimenMultipleBatchOpViewFormInput formInput;

    private BatchOperationsTable operationsTable;

    @SuppressWarnings("nls")
    @Override
    protected void init() throws Exception {
        formInput = (SpecimenMultipleBatchOpViewFormInput) getEditorInput();
        setPartName(i18n.tr("{0} imports", formInput.getSpecimenInfo().getSpecimen().getInventoryId()));
    }

    @SuppressWarnings("nls")
    @Override
    protected void createFormContent() throws Exception {
        form.setText(i18n.tr("Imports for specimen {0}",
                             formInput.getSpecimenInfo().getSpecimen().getInventoryId()));
        GridLayout layout = new GridLayout(1, false);
        page.setLayout(layout);
        page.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        Composite client = toolkit.createComposite(page);
        layout = new GridLayout(1, false);
        layout.horizontalSpacing = 10;
        client.setLayout(layout);
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(client);

        operationsTable = new BatchOperationsTable(client,
                                                   formInput.getSpecimenInfo().getBatchOperations());
        operationsTable.adaptToToolkit(toolkit, true);
        operationsTable.layout(true, true);
        setValues();
    }

    @Override
    public void setValues() throws Exception {
        operationsTable.setCollection(formInput.getSpecimenInfo().getBatchOperations());
        operationsTable.reload();
    }

    public static void openForm(SpecimenBriefInfo specimenInfo, boolean focusOnEditor) throws PartInitException {
        SpecimenMultipleBatchOpViewFormInput input =
            new SpecimenMultipleBatchOpViewFormInput(specimenInfo);
        PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
            .openEditor(input, ID, focusOnEditor);
    }

    public static class SpecimenMultipleBatchOpViewFormInput extends FormInput {

        private final SpecimenBriefInfo specimenInfo;

        @SuppressWarnings("nls")
        public SpecimenMultipleBatchOpViewFormInput(SpecimenBriefInfo specimenInfo) {
            super(specimenInfo, i18n.tr("specimen information"));
            this.specimenInfo = specimenInfo;
        }

        public SpecimenBriefInfo getSpecimenInfo() {
            return specimenInfo;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + specimenInfo.getSpecimen().getId();
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            SpecimenMultipleBatchOpViewFormInput other = (SpecimenMultipleBatchOpViewFormInput) obj;
            return specimenInfo.getSpecimen().getId() == other.specimenInfo.getSpecimen().getId();
        }
    }

}
