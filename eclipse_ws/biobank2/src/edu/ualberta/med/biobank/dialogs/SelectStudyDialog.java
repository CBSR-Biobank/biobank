package edu.ualberta.med.biobank.dialogs;

import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.widgets.infotables.StudyInfoTable;

public class SelectStudyDialog extends TitleAreaDialog {

    public static final int ADD_BTN_ID = 100;

    private static final String TITLE = "Associated Studies";

    private StudyInfoTable studyInfoTable;

    private StudyWrapper selectedStudy;

    private List<StudyWrapper> studies;

    public SelectStudyDialog(Shell parent, List<StudyWrapper> studies) {
        super(parent);
        this.studies = studies;
    }

    @Override
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        shell.setText(TITLE);
    }

    @Override
    protected Control createContents(Composite parent) {
        Control contents = super.createContents(parent);
        setTitle("Add a study to this site");
        setMessage("Select a study");
        return contents;
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite parentComposite = (Composite) super.createDialogArea(parent);
        Composite contents = new Composite(parentComposite, SWT.NONE);
        contents.setLayout(new GridLayout(1, false));
        contents.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        studyInfoTable = new StudyInfoTable(contents, null);
        studyInfoTable.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (studyInfoTable.getSelection() != null)
                    SelectStudyDialog.this.getButton(IDialogConstants.OK_ID)
                        .setEnabled(true);
            }
        });
        studyInfoTable.setCollection(studies);
        studyInfoTable.setEnabled(true);
        return contents;
    }

    @Override
    protected void okPressed() {
        selectedStudy = studyInfoTable.getSelection();
        super.okPressed();
    }

    public StudyWrapper getSelection() {
        return selectedStudy;
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        super.createButtonsForButtonBar(parent);
        getButton(IDialogConstants.OK_ID).setEnabled(false);
    }
}
