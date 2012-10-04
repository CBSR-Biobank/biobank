package edu.ualberta.med.biobank.forms;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.batchoperation.ClientBatchOpErrorsException;
import edu.ualberta.med.biobank.batchoperation.specimen.SpecimenBatchOpInterpreter;
import edu.ualberta.med.biobank.common.action.exception.BatchOpErrorsException;
import edu.ualberta.med.biobank.common.action.exception.BatchOpException;
import edu.ualberta.med.biobank.common.util.Holder;
import edu.ualberta.med.biobank.forms.input.FormInput;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.gui.common.widgets.FileBrowser;
import edu.ualberta.med.biobank.gui.common.widgets.IBgcFileBrowserListener;
import edu.ualberta.med.biobank.treeview.AdapterBase;
import edu.ualberta.med.biobank.widgets.infotables.BatchOpExceptionTable;

public class SpecimenImportForm extends BiobankViewForm {
    private static final I18n i18n = I18nFactory
        .getI18n(BiobankViewForm.class);
    @SuppressWarnings("nls")
    public static final String ID =
        "edu.ualberta.med.biobank.forms.SpecimenImportForm";

    private FileBrowser fileBrowser;
    private Button importButton;
    private BatchOpExceptionTable errorsTable;
    private Label errorsLabel;
    private Composite client;

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
        toolkit.adapt(fileBrowser);
    }

    private void createImportButton(Composite parent) {
        // take up a cell.
        new Label(parent, SWT.NONE);

        importButton = toolkit.createButton(parent,
            i18n.tr("Import"),
            SWT.NONE);
        importButton.setEnabled(false);
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

    private void createErrorsTable(Composite parent,
        List<BatchOpException<?>> errors) {
        if (errorsTable != null) errorsTable.dispose();
        if (errorsLabel != null) errorsLabel.dispose();
        if (errors == null || errors.isEmpty()) return;

        // TODO: add a label saying there were errors

        errorsLabel = new Label(parent, SWT.NONE);
        GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
        gd.horizontalSpan = 2;
        errorsLabel.setLayoutData(gd);

        errorsLabel.setText(i18n
            .tr("The following errors occurred when attempting to import:"));
        errorsLabel.setBackground(toolkit.getColors().getBackground());

        errorsTable = new BatchOpExceptionTable(parent, errors);
        gd = (GridData) errorsTable.getLayoutData();
        gd.horizontalSpan = 2;

        toolkit.adapt(errorsTable);
        book.reflow(true);
        form.layout(true, true);
    }

    @Override
    protected void createFormContent() throws Exception {
        form.setText(i18n.tr("Specimen Import"));
        page.setLayout(new GridLayout(1, false));

        client = toolkit.createComposite(page);
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 10;
        client.setLayout(layout);
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(client);

        createFileBrowser(client);
        createImportButton(client);
    }

    @Override
    public void setValues() throws Exception {
        fileBrowser.reset();
    }

    private void doImport() {
        final String filename = fileBrowser.getFilePath();
        IRunnableWithProgress op = new IRunnableWithProgress() {
            @SuppressWarnings("nls")
            @Override
            public void run(IProgressMonitor monitor) {
                monitor.beginTask(i18n.tr("Importing Specimens..."),
                    IProgressMonitor.UNKNOWN);

                final List<BatchOpException<?>> errors =
                    new ArrayList<BatchOpException<?>>();
                final Holder<Boolean> success = new Holder<Boolean>(false);
                final Holder<Integer> batchOpId = new Holder<Integer>(null);

                try {
                    SpecimenBatchOpInterpreter interpreter =
                        new SpecimenBatchOpInterpreter(filename);

                    monitor.beginTask(i18n.tr("Reading file..."),
                        IProgressMonitor.UNKNOWN);
                    interpreter.readPojos();

                    monitor.beginTask(i18n.tr("Saving data..."),
                        IProgressMonitor.UNKNOWN);
                    batchOpId.setValue(interpreter.savePojos());

                    success.setValue(true);
                } catch (ClientBatchOpErrorsException e) {
                    errors.addAll(e.getErrors());
                } catch (BatchOpErrorsException e) {
                    errors.addAll(e.getErrors());
                } catch (IllegalStateException e) {
                    BgcPlugin
                        .openAsyncError(
                            // dialog title
                            i18n.tr("File Format Error"),
                            // dialog error message
                            i18n.tr("The file is not a CSV file or has the wrong content."));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                } finally {
                    fileBrowser.getDisplay().asyncExec(new Runnable() {
                        @Override
                        public void run() {
                            if (success.getValue()) {
                                AdapterBase
                                    .closeEditor((FormInput) getEditorInput());
                                try {
                                    SpecimenBatchOpViewForm.openForm(
                                        batchOpId.getValue(), true);
                                } catch (PartInitException e) {
                                    throw new RuntimeException(e);
                                }
                            } else {
                                updateErrorsTable(errors);
                            }
                        }
                    });
                }

                monitor.done();
            }
        };

        try {
            new ProgressMonitorDialog(PlatformUI.getWorkbench()
                .getActiveWorkbenchWindow().getShell()).run(true, true, op);
        } catch (InvocationTargetException e) {
            MessageDialog.openError(PlatformUI.getWorkbench()
                .getActiveWorkbenchWindow().getShell(),
                i18n.tr("Import Error"), e.getTargetException().getMessage());
        } catch (InterruptedException e) {
            MessageDialog.openError(PlatformUI.getWorkbench()
                .getActiveWorkbenchWindow().getShell(),
                i18n.tr("Import Error"), e.getMessage());
        }
    }

    private void updateErrorsTable(List<BatchOpException<?>> errors) {
        createErrorsTable(client, errors);
    }
}