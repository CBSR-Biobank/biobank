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
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.gui.common.dialogs.BgcBaseDialog;
import edu.ualberta.med.biobank.widgets.infotables.StudyInfoTable;

public class SelectStudyDialog extends BgcBaseDialog {
    private static final I18n i18n = I18nFactory
        .getI18n(SelectStudyDialog.class);

    public static final int ADD_BTN_ID = 100;

    private StudyInfoTable studyInfoTable;

    private StudyWrapper selectedStudy;

    private final List<StudyWrapper> studies;

    public SelectStudyDialog(Shell parent, List<StudyWrapper> studies) {
        super(parent);
        this.studies = studies;
    }

    @SuppressWarnings("nls")
    @Override
    protected String getDialogShellTitle() {
        // TR: select study dialog title
        return i18n.tr("Associated Studies");
    }

    @SuppressWarnings("nls")
    @Override
    protected String getTitleAreaMessage() {
        // TR: select study dialog title area message
        return i18n.tr("Select a study");
    }

    @SuppressWarnings("nls")
    @Override
    protected String getTitleAreaTitle() {
        // TR: select study dialog title area title
        return i18n.tr("Add a study to this site");
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
