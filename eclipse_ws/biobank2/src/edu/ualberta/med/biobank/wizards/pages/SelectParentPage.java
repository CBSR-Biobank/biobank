package edu.ualberta.med.biobank.wizards.pages;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.common.action.specimen.SpecimenInfo;
import edu.ualberta.med.biobank.gui.common.dialogs.BgcWizardPage;
import edu.ualberta.med.biobank.validators.NotNullValidator;
import edu.ualberta.med.biobank.widgets.infotables.NewSpecimenInfoTable;
import edu.ualberta.med.biobank.widgets.infotables.NewSpecimenInfoTable.ColumnsShown;

public class SelectParentPage extends BgcWizardPage {
    private static final I18n i18n = I18nFactory
        .getI18n(SelectParentPage.class);

    public static final String PAGE_NAME = SelectParentPage.class
        .getCanonicalName();
    @SuppressWarnings("nls")
    private static final String PARENT_REQUIRED =
        i18n.tr("Please select its correct parent.");
    private Button isSourceSpecimen;
    private NewSpecimenInfoTable specTable;

    @SuppressWarnings("nls")
    public SelectParentPage() {
        super(PAGE_NAME, i18n.tr("Select a parent specimen"), null);
    }

    public void setParentSpecimenList(List<SpecimenInfo> specs) {
        specTable.setList(specs);
    }

    public SpecimenInfo getSpecimen() {
        return specTable.getSelection();
    }

    @SuppressWarnings("nls")
    @Override
    protected void createDialogAreaInternal(Composite parent) throws Exception {
        Composite content = new Composite(parent, SWT.NONE);
        content.setLayout(new GridLayout(1, false));
        content.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        isSourceSpecimen = new Button(content, SWT.CHECK);
        isSourceSpecimen.setText(
            // checkbox label.
            i18n.tr("Source Specimen"));
        isSourceSpecimen.setSelection(true);

        final Label label = new Label(content, SWT.None);
        label.setText(
            // label.
            i18n.tr("Select parent:"));
        label.setEnabled(false);

        final IObservableValue selection =
            new WritableValue(null, Object.class);

        isSourceSpecimen.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                label.setEnabled(!isSourceSpecimen.getSelection());
                specTable.setEnabled(!isSourceSpecimen.getSelection());
                if (isSourceSpecimen.getSelection())
                    selection.setValue(new Boolean(true));
                else
                    selection.setValue(null);
            }
        });

        specTable =
            new NewSpecimenInfoTable(content,
                new ArrayList<SpecimenInfo>(),
                ColumnsShown.CEVENT_SOURCE_SPECIMENS, 10
            ) {
                @Override
                public boolean isEditMode() {
                    return true;
                }
            };
        specTable.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                selection.setValue(getSpecimen());
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                selection.setValue(getSpecimen());
            }
        });

        specTable.setEnabled(false);

        UpdateValueStrategy uvs = new UpdateValueStrategy();
        uvs.setAfterGetValidator(new NotNullValidator(PARENT_REQUIRED));
        getWidgetCreator().bindValue(selection, new WritableValue(), uvs, null);
        selection.setValue(new Boolean(true));

        setControl(content);
    }
}