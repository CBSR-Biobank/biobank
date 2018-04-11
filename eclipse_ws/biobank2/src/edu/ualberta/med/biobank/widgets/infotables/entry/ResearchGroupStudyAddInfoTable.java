package edu.ualberta.med.biobank.widgets.infotables.entry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.FormColors;
import org.eclipse.ui.forms.IFormColors;
import org.eclipse.ui.forms.widgets.Section;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.wrappers.ResearchGroupWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.dialogs.select.SelectStudyDialog;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.gui.common.widgets.IInfoTableAddItemListener;
import edu.ualberta.med.biobank.gui.common.widgets.IInfoTableDeleteItemListener;
import edu.ualberta.med.biobank.gui.common.widgets.InfoTableEvent;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.widgets.infotables.StudyInfoTable;

/**
 * Allows the user to select a Study and associate it with a Research Group
 * Used in ResearchGroupEntryForm class
 *
 * @author OHSDEV
 *
 */
public class ResearchGroupStudyAddInfoTable extends StudyInfoTable {
    public static final I18n i18n = I18nFactory
        .getI18n(ResearchGroupStudyAddInfoTable.class);

    private final ResearchGroupWrapper researchGroup;

    private Section section;

    @SuppressWarnings("nls")
    private static final String MSG_NO_STUDIES =
        i18n.tr(" (A Study needs to be associated with the Research Group)");

    public ResearchGroupStudyAddInfoTable(Composite parent, ResearchGroupWrapper researchGroup,
        boolean createDeleteSupport) {
        super(parent, researchGroup.getStudyCollection(true), true);
        section=(Section)parent;
        this.researchGroup = researchGroup;
        if (createDeleteSupport)
            addDeleteCreateSupport();
    }

    @Override
    protected boolean isEditMode() {
        return true;
    }

    @SuppressWarnings("nls")
    public void createStudyDlg() {
        SelectStudyDialog dlg;

        try {
            List<StudyWrapper> availableStudies = StudyWrapper
                .getAllStudies(SessionManager.getAppService());
            List<StudyWrapper> alreadyAddedStudies = researchGroup
                .getStudyCollection(false);
            if (alreadyAddedStudies != null) {
                availableStudies.removeAll(alreadyAddedStudies);
            }
            dlg = new SelectStudyDialog(PlatformUI.getWorkbench()
                .getActiveWorkbenchWindow().getShell(), availableStudies);
            if (dlg.open() == Dialog.OK) {
                notifyListeners();
                StudyWrapper study = dlg.getSelection();
                if (study != null) {
                    List<StudyWrapper> dummyList =
                        new ArrayList<StudyWrapper>();
                    dummyList.add(study);
                    researchGroup.addToStudyCollection(dummyList);
                    setList(researchGroup.getStudyCollection(true));
                    section.setText(i18n.tr(Study.NAME.plural().toString()));	//OHSDEV - Set the section header back to normal if a study is associated
                    section.setTitleBarForeground( new FormColors(section.getDisplay()).getColor(IFormColors.TB_TOGGLE) );
                }
            }
        } catch (Exception e) {
            BgcPlugin.openAsyncError(
                // dialog title.
                i18n.tr("Unable to retrieve available studies"), e);
        }
    }

    private void addDeleteCreateSupport() {
        addAddItemListener(new IInfoTableAddItemListener<StudyWrapper>() {
            @Override
            public void addItem(InfoTableEvent<StudyWrapper> event) {
                createStudyDlg();
            }
        });

        addDeleteItemListener(new IInfoTableDeleteItemListener<StudyWrapper>() {
            @SuppressWarnings("nls")
            @Override
            public void deleteItem(InfoTableEvent<StudyWrapper> event) {
                StudyWrapper study = getSelection();
                if (study == null)
			return;

                if (!BgcPlugin.openConfirm(
                    // dialog title.
                    i18n.tr("Remove Study"),
                    // dialog message.
                    i18n.tr("Are you sure you want to remove study \"{0}\"?",
                        study.getName()))) {
                    return;
                }

                try {
			researchGroup.removeFromStudyCollectionWithCheck(Arrays
                        .asList(study));
                    setList(researchGroup.getStudyCollection(true));
                    if(researchGroup.getStudyCollection().isEmpty()) {
			//OHSDEV - Set the section header with a message in RED if a study is NOT associated
			section.setText(Study.NAME.plural().toString() + MSG_NO_STUDIES);
			section.setTitleBarForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
                    }
                    notifyListeners();
                } catch (BiobankCheckException e) {
                    BgcPlugin.openAsyncError(
                        // dialog title.
                        i18n.tr("Delete failed"), e);
                }
            }
        });
    }

    public void setStudies(List<StudyWrapper> studies) {
        setList(studies);
    }

    @Override
    public void reload() {
        setList(researchGroup.getStudyCollection(true));
    }

}