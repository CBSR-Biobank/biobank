package edu.ualberta.med.biobank.forms.batchop;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.MessageFormat;
import java.util.Date;

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
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.Section;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.action.SimpleResult;
import edu.ualberta.med.biobank.common.action.file.FileDataGetAction;
import edu.ualberta.med.biobank.common.util.Holder;
import edu.ualberta.med.biobank.forms.BiobankViewForm;
import edu.ualberta.med.biobank.forms.input.FormInput;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseText;
import edu.ualberta.med.biobank.model.FileData;
import edu.ualberta.med.biobank.model.FileMetaData;

public abstract class BatchOpViewForm extends BiobankViewForm {
    private static final I18n i18n = I18nFactory.getI18n(BatchOpViewForm.class);

    private final String formTitle;

    private Integer batchId;
    private String executedBy;
    private Date timeExecuted;
    private FileMetaData fileMetaData;

    private BgcBaseText executedByText;
    private BgcBaseText timeExecutedText;

    private BgcBaseText fileNameText;
    private BgcBaseText fileSizeText;
    private Button fileDownloadButton;

    private Section inputSection;

    public BatchOpViewForm(String formTitle) {
        this.formTitle = formTitle;
    }

    @Override
    protected Image getFormImage() {
        return BgcPlugin.getDefault().getImageRegistry().get(BgcPlugin.IMG_DATABASE_GO);
    }

    @SuppressWarnings("nls")
    @Override
    protected void createFormContent() throws Exception {
        form.setText(formTitle);
        page.setLayout(new GridLayout(1, false));

        Composite client = toolkit.createComposite(page);
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 10;
        client.setLayout(layout);
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(client);

        executedByText = createReadOnlyLabelledField(client, SWT.NONE, i18n.tr("Executed By"));
        timeExecutedText = createReadOnlyLabelledField(client, SWT.NONE, i18n.tr("Time Executed"));

        createFileInfo();
        createFormContents();
        setValues();
    }

    @SuppressWarnings("nls")
    private void createFileInfo() {
        Composite client = createSectionWithClient(i18n.tr("File Input"));
        inputSection = (Section) client.getParent();
        inputSection.setExpanded(true);

        fileNameText = createReadOnlyLabelledField(client, SWT.NONE, i18n.tr("File Name"));
        fileSizeText = createReadOnlyLabelledField(client, SWT.NONE, i18n.tr("Size"));

        new Label(client, SWT.NONE);

        fileDownloadButton = new Button(client, SWT.NONE);
        fileDownloadButton.setText(i18n.tr("Download"));
        fileDownloadButton.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                try {
                    // choose output file.
                    FileDialog fd = new FileDialog(PlatformUI.getWorkbench()
                        .getActiveWorkbenchWindow().getShell(), SWT.SAVE);
                    fd.setText(i18n.tr("Download the file to..."));
                    fd.setFileName(fileMetaData.getName());

                    final String path = fd.open();
                    if (path == null || path.isEmpty()) return;

                    // create output file.
                    File file = new File(path);
                    if (!file.exists()) {
                        file.createNewFile();
                    } else {
                        boolean overwrite = MessageDialog.openConfirm(
                            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                            i18n.tr("File Already Exists"),
                            MessageFormat.format(
                                i18n.tr("The file {0} already exists. Would you like to overwrite it?"),
                                path));
                        if (!overwrite) return;
                    }

                    // download file
                    final Holder<FileData> data = new Holder<FileData>(null);
                    IRunnableWithProgress op = new IRunnableWithProgress() {
                        @Override
                        public void run(IProgressMonitor monitor) {
                            monitor.beginTask(i18n.tr("Downloading file..."),
                                IProgressMonitor.UNKNOWN);

                            SimpleResult<FileData> dataResult;
                            try {
                                dataResult = SessionManager.getAppService().doAction(
                                    new FileDataGetAction(fileMetaData));
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                            data.setValue(dataResult.getResult());

                            monitor.done();
                        }
                    };

                    new ProgressMonitorDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow()
                        .getShell()).run(true, false, op);

                    // write data to file.
                    BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
                    bos.write(data.getValue().getBytes());
                    bos.flush();
                    bos.close();
                } catch (Exception e) {
                    MessageDialog.openError(PlatformUI.getWorkbench()
                        .getActiveWorkbenchWindow().getShell(),
                        i18n.tr("Problem downloading file"), e.getMessage());
                }
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
            }
        });
    }

    protected abstract void createFormContents();

    @Override
    public void setValues() throws Exception {
        setTextValue(executedByText, executedBy);
        setTextValue(timeExecutedText, timeExecuted);

        if (fileMetaData != null) {
            inputSection.setVisible(true);
            setTextValue(fileNameText, fileMetaData.getName());
            setTextValue(fileSizeText,
                humanReadableByteCount(fileMetaData.getSize(), false));
        } else {
            inputSection.setVisible(false);
        }
    }

    @SuppressWarnings("nls")
    public static String humanReadableByteCount(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

    protected static void openForm(Integer batchOpId, String formId, boolean focusOnEditor)
        throws PartInitException {
        SpecimenBatchOpViewFormInput input = new SpecimenBatchOpViewFormInput(batchOpId);
        PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
            .openEditor(input, formId, focusOnEditor);
    }

    public Integer getBatchId() {
        return batchId;
    }

    public void setBatchId(Integer batchId) {
        this.batchId = batchId;
    }

    public String getExecutedBy() {
        return executedBy;
    }

    public void setExecutedBy(String executedBy) {
        this.executedBy = executedBy;
    }

    public Date getTimeExecuted() {
        return timeExecuted;
    }

    public void setTimeExecuted(Date timeExecuted) {
        this.timeExecuted = timeExecuted;
    }

    public FileMetaData getFileMetaData() {
        return fileMetaData;
    }

    public void setFileMetaData(FileMetaData fileMetaData) {
        this.fileMetaData = fileMetaData;
    }

    public static class SpecimenBatchOpViewFormInput
        extends FormInput {

        @SuppressWarnings("nls")
        public SpecimenBatchOpViewFormInput(Integer batchOpId) {
            super(batchOpId, i18n.tr("Specimen Import"));
            this.batchOpId = batchOpId;
        }

        private final Integer batchOpId;

        public Integer getBatchOpId() {
            return batchOpId;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((batchOpId == null) ? 0 : batchOpId.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            SpecimenBatchOpViewFormInput other = (SpecimenBatchOpViewFormInput) obj;
            if (batchOpId == null) {
                if (other.batchOpId != null) return false;
            } else if (!batchOpId.equals(other.batchOpId)) return false;
            return true;
        }
    }
}
