package edu.ualberta.med.biobank.wizards.pages;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.common.wrappers.CollectionEventWrapper;
import edu.ualberta.med.biobank.gui.common.dialogs.BgcWizardPage;
import edu.ualberta.med.biobank.validators.NotNullValidator;
import edu.ualberta.med.biobank.widgets.infotables.CollectionEventInfoTable;

public class SelectCollectionEventPage extends BgcWizardPage {
    public static final String PAGE_NAME = SelectCollectionEventPage.class
        .getCanonicalName();
    private static final String CEVENT_REQUIRED = Messages.SelectCollectionEventPage_required_msg;
    private CollectionEventInfoTable ceventsTable;

    public SelectCollectionEventPage() {
        super(PAGE_NAME, Messages.SelectCollectionEventPage_description, null);
    }

    public void setCollectionEventList(List<CollectionEventWrapper> cevents) {
        ceventsTable.setList(cevents);
    }

    public CollectionEventWrapper getCollectionEvent() {
        return ceventsTable.getSelection();
    }

    @Override
    protected void createDialogAreaInternal(Composite parent) throws Exception {
        Composite content = new Composite(parent, SWT.NONE);
        content.setLayout(new GridLayout(1, false));
        content.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        final IObservableValue selection = new WritableValue(null, Object.class);
        ceventsTable = new CollectionEventInfoTable(content,
            new ArrayList<CollectionEventWrapper>()) {
            @Override
            public boolean isEditMode() {
                return true;
            }
        };
        ceventsTable.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                selection.setValue(getCollectionEvent());
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                selection.setValue(getCollectionEvent());
            }
        });

        UpdateValueStrategy uvs = new UpdateValueStrategy();
        uvs.setAfterGetValidator(new NotNullValidator(CEVENT_REQUIRED));
        getWidgetCreator().bindValue(selection, new WritableValue(), uvs, null);

        setControl(content);
    }
}