package edu.ualberta.med.biobank.mvp.view.form;

import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;

import edu.ualberta.med.biobank.model.CollectionEvent;
import edu.ualberta.med.biobank.model.ProcessingEvent;
import edu.ualberta.med.biobank.mvp.presenter.impl.SpecimenLinkPresenter;
import edu.ualberta.med.biobank.mvp.user.ui.HasButton;
import edu.ualberta.med.biobank.mvp.user.ui.SelectedValueField;
import edu.ualberta.med.biobank.mvp.user.ui.ValueField;
import edu.ualberta.med.biobank.mvp.view.item.ButtonItem;
import edu.ualberta.med.biobank.mvp.view.item.CheckBox;
import edu.ualberta.med.biobank.mvp.view.item.ComboBox;
import edu.ualberta.med.biobank.mvp.view.item.TextBox;
import edu.ualberta.med.biobank.mvp.view.util.InputTable;

/**
 * 
 * @author jferland
 * 
 */
public class SpecimenLinkView extends AbstractEntryFormView implements
    SpecimenLinkPresenter.View {
    private final TextBox patientNumber = new TextBox();
    private final ComboBox<ProcessingEvent> processingEvent =
        new ComboBox<ProcessingEvent>();
    private final CheckBox isRecentProcessingEvent = new CheckBox();
    private final ComboBox<CollectionEvent> collectionEvent =
        new ComboBox<CollectionEvent>();
    private final ButtonItem confim = new ButtonItem();

    @Override
    public ValueField<String> getPatientNumber() {
        return patientNumber;
    }

    @Override
    public SelectedValueField<ProcessingEvent> getProcessingEvent() {
        return processingEvent;
    }

    @Override
    public ValueField<Boolean> isRecentProcessingEvent() {
        return isRecentProcessingEvent;
    }

    @Override
    public SelectedValueField<CollectionEvent> getCollectionEvent() {
        return collectionEvent;
    }

    @Override
    public HasButton getConfirm() {
        return confim;
    }

    @SuppressWarnings("nls")
    @Override
    public void onCreate(BaseForm baseForm) {
        super.onCreate(baseForm);

        baseForm.setTitle("Specimen Link");

        InputTable table = new InputTable(baseForm.getPage());

        patientNumber.setValidationControl(table.addLabel("pnumber"));
        patientNumber.setText(table.addText());

        processingEvent.setValidationControl(table.addLabel("pEvent"));
        processingEvent.setComboViewer(new ComboViewer(table));

        isRecentProcessingEvent.create(baseForm.getPage());

        Button button = new Button(baseForm.getPage(), SWT.NONE);
        button.setText("confirm");

        confim.setButton(button);
    }

    @SuppressWarnings("nls")
    @Override
    public String getOkMessage() {
        return "Everything is A-Okay";
    }
}
