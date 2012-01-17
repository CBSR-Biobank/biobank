/*
 * HEADER
 * 
 */

package edu.ualberta.med.biobank.forms;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.acegisecurity.AccessDeniedException;
import org.apache.commons.io.FileUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.springframework.remoting.RemoteAccessException;
import org.springframework.remoting.RemoteConnectFailureException;

import edu.ualberta.med.biobank.Messages;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.dialogs.TecanScanLinkDialog;
import edu.ualberta.med.biobank.forms.linkassign.AbstractSpecimenAdminForm;
import edu.ualberta.med.biobank.gui.common.BgcLogger;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseText;
import edu.ualberta.med.biobank.gui.common.widgets.utils.ComboSelectionUpdate;

/*
 * Auth
 */
public class TecanScanLinkEntryForm extends AbstractSpecimenAdminForm {

    private static BgcLogger logger = BgcLogger
        .getLogger(TecanScanLinkEntryForm.class.getName());

    public static final String ID = "edu.ualberta.med.biobank.forms.TecanScanLinkEntryForm"; //$NON-NLS-1$

    // DFE
    private Button uploadButton;
    private String path;
    private byte[] csvfile;
    private ComboViewer studiesViewer;
    private Button processButton;
    private int enableProcessButton;
    private List<String> processed;
    private BgcBaseText processComment;
    private BgcBaseText processWorkSheet;
    private String pComment;
    private String pWorkSheet;
    private StudyWrapper selectedStudy;
    private int pStudy;

    @Override
    protected void init() throws Exception {
        super.init();
        setPartName("Tecan Scan Link");
        appendLog("Start Process");
    }

    @Override
    protected void createFormContent() throws Exception {
        form.setText(Messages.getString("TecanScanLink.form.title")); //$NON-NLS-1$
        form.setMessage(getOkMessage(), IMessageProvider.NONE);
        page.setLayout(new GridLayout(1, false));
        createMainSection();
    }

    private void createMainSection() {

        Composite leftComposite = toolkit.createComposite(page);
        GridLayout gl = new GridLayout(2, false);
        leftComposite.setLayout(gl);
        toolkit.paintBordersFor(leftComposite);
        GridData gd = new GridData();
        gd.widthHint = 520;
        gd.horizontalAlignment = SWT.FILL;
        gd.verticalAlignment = SWT.TOP;
        leftComposite.setLayoutData(gd);

        BgcBaseText bbt = createReadOnlyLabelledField(leftComposite, SWT.NONE,
            Messages.getString("ProcessingEvent.field.center.label"),
            SessionManager.getUser().getCurrentWorkingSite().getName());

        List<StudyWrapper> studies = SessionManager.getUser()
            .getCurrentWorkingCenter().getStudyCollection();
        selectedStudy = null;
        // if (patient.isNew()) {
        // if (studies.size() == 1) {
        // selectedStudy = studies.get(0);
        // patient.setStudy(selectedStudy);
        // }
        // } else {
        // selectedStudy = patient.getStudy();
        // }
        //
        // studiesViewer = createComboViewer(client,
        // Messages.getString("PatientEntryForm.field.study.label"), studies,
        // selectedStudy,
        // Messages.getString("PatientEntryForm.field.study.validation.msg"),
        // new ComboSelectionUpdate() {
        // @Override
        // public void doSelection(Object selectedObject) {
        // patient.setStudy((StudyWrapper) selectedObject);
        // }
        // });

        studiesViewer = createComboViewer(leftComposite,
            Messages.getString("PatientEntryForm.field.study.label"), studies,
            selectedStudy,
            Messages.getString("PatientEntryForm.field.study.validation.msg"),
            new ComboSelectionUpdate() {
                @Override
                public void doSelection(Object selectedObject) {
                    selectedStudy = (StudyWrapper) selectedObject;
                    int pStudy = selectedStudy.getId();
                    enableProcessButton = enableProcessButton + 1;
                    if (enableProcessButton >= 2)
                        processButton.setEnabled(true);
                }
            });

        final Label uploadtLabel = widgetCreator.createLabel(leftComposite,
            "CSV File");
        uploadtLabel.setLayoutData(new GridData(
            GridData.VERTICAL_ALIGN_BEGINNING));
        createUpload(leftComposite);

        processWorkSheet = (BgcBaseText) createLabelledWidget(leftComposite,
            BgcBaseText.class, SWT.SINGLE, "WorkSheet");
        processWorkSheet.addListener(SWT.Modify, new Listener() {
            // org.eclipse.swt.widgets.Event conflicts with the other Event
            @Override
            public void handleEvent(org.eclipse.swt.widgets.Event event) {
                if (processWorkSheet.getText().trim().isEmpty()) {
                    pWorkSheet = "";
                } else {
                    pWorkSheet = processWorkSheet.getText().trim();
                }
            }
        });

        processComment = (BgcBaseText) createLabelledWidget(leftComposite,
            BgcBaseText.class, SWT.MULTI, "Comment");
        processComment.addListener(SWT.Modify, new Listener() {
            // org.eclipse.swt.widgets.Event conflicts with the other Event
            @Override
            public void handleEvent(org.eclipse.swt.widgets.Event event) {
                if (processComment.getText().trim().isEmpty()) {
                    pComment = "";
                } else {
                    pComment = processComment.getText().trim();
                }
            }
        });

        processButton = createSendButton();

        setFirstControl(bbt);

    }

    private Composite createUpload(Composite parent) {

        Composite composite = toolkit.createComposite(parent);
        GridLayout layout = new GridLayout(3, false);
        layout.horizontalSpacing = 10;
        layout.marginHeight = 0;
        layout.verticalSpacing = 0;
        layout.marginLeft = -5;
        composite.setLayout(layout);
        composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(composite);

        final BgcBaseText fileToUpload = widgetCreator.createReadOnlyField(
            composite, SWT.NONE, "CSV File", true);

        uploadButton = toolkit.createButton(composite, "Select", SWT.NONE);
        uploadButton.addListener(SWT.Selection, new Listener() {
            // org.eclipse.swt.widgets.Event conflicts with the other Event
            @Override
            public void handleEvent(org.eclipse.swt.widgets.Event event) {
                String[] filterExt = new String[] { "*.csv" };
                File fl = new File("");
                path = runFileDialog("home", filterExt);
                if (path != null) {
                    fileToUpload.setText(path);
                    fl = new File(path);
                    enableProcessButton = enableProcessButton + 1;
                    if (enableProcessButton >= 2)
                        processButton.setEnabled(true);

                }
                try {
                    csvfile = FileUtils.readFileToByteArray(fl);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }

            }
        });
        return composite;
    }

    private String runFileDialog(String name, String[] exts) {
        FileDialog fd = new FileDialog(form.getShell(), SWT.OPEN);
        fd.setOverwrite(true);
        fd.setText("Select CSV");
        fd.setFilterExtensions(exts);
        fd.setFileName(name);
        return fd.open();
    }

    private Button createSendButton() {
        final Button sendButton = toolkit.createButton(page, "Send", SWT.PUSH);
        sendButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (new TecanScanLinkDialog(Display.getDefault()
                    .getActiveShell(), path).open() == Dialog.OK) {
                    IRunnableContext context = new ProgressMonitorDialog(
                        Display.getDefault().getActiveShell());
                    try {
                        context.run(true, true, new IRunnableWithProgress() {
                            @Override
                            public void run(final IProgressMonitor monitor) {
                                monitor.beginTask("Processing...",
                                    IProgressMonitor.UNKNOWN);

                                try {
                                    // SEND DATA TO SERVER
                                    int cCenter = SessionManager.getUser()
                                        .getCurrentWorkingSite().getId();
                                    processed = appService.tecanloadFile(
                                        csvfile, pStudy, pWorkSheet, pComment,
                                        cCenter);
                                } catch (final RemoteConnectFailureException exp) {
                                    BgcPlugin
                                        .openRemoteConnectErrorMessage(exp);
                                    return;
                                } catch (final RemoteAccessException exp) {
                                    BgcPlugin.openRemoteAccessErrorMessage(exp);
                                    return;
                                } catch (final AccessDeniedException ade) {
                                    BgcPlugin.openAccessDeniedErrorMessage(ade);
                                    return;
                                } catch (Exception ex) {
                                    BgcPlugin.openAsyncError("Save error", ex);
                                    return;
                                }
                                monitor.done();
                            }
                        });
                    } catch (Exception e1) {
                        BgcPlugin.openAsyncError("Save error", e1);
                    }
                    int i = 0;
                    while (i < processed.size()) {
                        appendLog(processed.get(i));
                        i++;
                    }
                    appendLog("END Process");
                }
            }
        });
        sendButton.setEnabled(false);
        return sendButton;
    }

    @Override
    protected void saveForm() throws Exception {
        // pEvent.persist();
        // SessionManager.updateAllSimilarNodes(pEventAdapter, true);
        appendLog("TESTMESSAGE");
    }

    @Override
    protected String getOkMessage() {
        // return (pEvent.isNew()) ? MSG_NEW_PEVENT_OK : MSG_PEVENT_OK;
        return "";
    }

    @Override
    public String getNextOpenedFormID() {
        return TecanScanLinkEntryForm.ID;
    }

    @Override
    protected void onReset() throws Exception {
        // CenterWrapper<?> center = pEvent.getCenter();
        // pEvent.reset();
        // pEvent.setCenter(center);
        // if (pEvent.isNew()) {
        // pEvent.setActivityStatus(ActivityStatusWrapper
        // .getActiveActivityStatus(appService));
        // }
        // GuiUtil.reset(activityStatusComboViewer, pEvent.getActivityStatus());
        // specimenEntryWidget.setSpecimens(pEvent.getSpecimenCollection(true));
    }

    @Override
    protected String getActivityTitle() {
        // TODO Change message
        return Messages.getString("SpecimenLink.activity.title"); //$NON-NLS-1$
    }

    @Override
    public BgcLogger getErrorLogger() {
        // TODO Auto-generated method stub
        return logger;
    }
}
