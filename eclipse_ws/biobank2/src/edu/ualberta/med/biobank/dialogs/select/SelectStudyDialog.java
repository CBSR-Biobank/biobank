package edu.ualberta.med.biobank.dialogs.select;

import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.gui.common.dialogs.BgcBaseDialog;
import edu.ualberta.med.biobank.widgets.infotables.StudyInfoTable;

public class SelectStudyDialog extends BgcBaseDialog {

    public static final int ADD_BTN_ID = 100;

    private StudyInfoTable studyInfoTable;

    private StudyWrapper selectedStudy;

    private List<StudyWrapper> studies;

    public SelectStudyDialog(Shell parent, List<StudyWrapper> studies) {
        super(parent);
        this.studies = studies;
    }

    @Override
    protected String getDialogShellTitle() {
        return "Associated Studies";
    }

    @Override
    protected String getTitleAreaMessage() {
        return "Select a study";
    }

    @Override
    protected String getTitleAreaTitle() {
        return "Add a study to this site";
    }

    @Override
    protected void createDialogAreaInternal(Composite parent) throws Exception {
        Composite contents = new Composite(parent, SWT.NONE);
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
        studyInfoTable.setList(studies);
        studyInfoTable.setEnabled(true);
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
