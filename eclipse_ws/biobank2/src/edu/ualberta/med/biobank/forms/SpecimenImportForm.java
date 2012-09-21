package edu.ualberta.med.biobank.forms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.batchoperation.IBatchOpPojoReader;
import edu.ualberta.med.biobank.batchoperation.specimen.CbsrTecanSpecimenPojoReader;
import edu.ualberta.med.biobank.batchoperation.specimen.SpecimenBatchOpPojoReader;
import edu.ualberta.med.biobank.common.action.batchoperation.specimen.SpecimenBatchOpInputPojo;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.gui.common.widgets.FileBrowser;
import edu.ualberta.med.biobank.gui.common.widgets.IBgcFileBrowserListener;
import edu.ualberta.med.biobank.gui.common.widgets.utils.ComboSelectionUpdate;

public class SpecimenImportForm extends BiobankViewForm {
    private static final I18n i18n = I18nFactory
        .getI18n(BiobankViewForm.class);
    @SuppressWarnings("nls")
    public static final String ID =
        "edu.ualberta.med.biobank.forms.SpecimenImportForm";

    @SuppressWarnings("nls")
    public static final String OK_MESSAGE =
        i18n.tr("Add or edit a shipping method");

    private static final PojoReaderOption DEFAULT_READER =
        new PojoReaderOption("Default", new SpecimenBatchOpPojoReader());
    private static final List<PojoReaderOption> NAMED_READERS;

    static {
        List<PojoReaderOption> tmp = new ArrayList<PojoReaderOption>();
        tmp.add(DEFAULT_READER);
        tmp.add(new PojoReaderOption("CBSR TECAN",
            new CbsrTecanSpecimenPojoReader()));
        NAMED_READERS = Collections.unmodifiableList(tmp);
    }

    private FileBrowser fileBrowser;
    private ComboViewer pojoReaderCombo;
    private IBatchOpPojoReader<SpecimenBatchOpInputPojo> pojoReader;
    private Button importButton;

    @Override
    public void init() throws Exception {
    }

    @Override
    protected Image getFormImage() {
        return BgcPlugin.getDefault().getImageRegistry()
            .get(BgcPlugin.IMG_DATABASE_GO);
    }

    private void createFileBrowser(Composite parent) {
        final String[] extensions = new String[] { "*.csv", "*.*" };

        widgetCreator.createLabel(parent, i18n.tr("CSV File"));

        fileBrowser = new FileBrowser(parent, SWT.NONE, extensions);
        fileBrowser.addFileSelectedListener(new IBgcFileBrowserListener() {
            @Override
            public void fileSelected(String filename) {
                if (importButton != null && !importButton.isDisposed()) {
                    boolean enabled = filename != null && !filename.isEmpty();
                    importButton.setEnabled(enabled);
                }
            }
        });
    }

    private void createPojoReaderCombo(Composite parent) {
        pojoReaderCombo = widgetCreator.createComboViewer(
            parent,
            i18n.tr("CSV File Type"),
            NAMED_READERS,
            DEFAULT_READER,
            // validation error message.
            i18n.tr("A CSV file type should be selected."),
            new ComboSelectionUpdate() {
                @Override
                public void doSelection(Object selectedObject) {
                    pojoReader = ((PojoReaderOption) selectedObject).reader;
                }
            },
            new LabelProvider() {
                @Override
                public String getText(Object element) {
                    return ((PojoReaderOption) element).name;
                }
            });
    }

    private void createImportButton(Composite parent) {
        // take up a cell.
        new Label(parent, SWT.NONE);

        importButton = new Button(parent, SWT.NONE);
        importButton.setEnabled(false);
        importButton.setText(i18n.tr("Import"));
        importButton.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                doImport();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
            }
        });
    }

    @Override
    protected void createFormContent() throws Exception {
        form.setText(i18n.tr("Specimen Import"));
        page.setLayout(new GridLayout(1, false));

        Composite client = toolkit.createComposite(page);
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 10;
        client.setLayout(layout);
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(client);

        createFileBrowser(client);
        createPojoReaderCombo(client);
        createImportButton(client);
    }

    @Override
    public void setValues() throws Exception {
        fileBrowser.reset();
        pojoReaderCombo.setSelection(new StructuredSelection(DEFAULT_READER));
    }

    private void doImport() {
    }

    private static class PojoReaderOption {
        private final String name;
        private final IBatchOpPojoReader<SpecimenBatchOpInputPojo> reader;

        private PojoReaderOption(String name,
            IBatchOpPojoReader<SpecimenBatchOpInputPojo> reader) {
            this.name = name;
            this.reader = reader;
        }
    }
}
