package edu.ualberta.med.biobank.forms;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.ui.forms.widgets.Section;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.permission.specimenType.SpecimenTypeCreatePermission;
import edu.ualberta.med.biobank.common.wrappers.SpecimenTypeWrapper;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.model.SpecimenType;
import edu.ualberta.med.biobank.widgets.trees.infos.SpecimenTypeEntryInfoTree;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class SpecimenTypesViewForm extends BiobankViewForm {
    private static final I18n i18n = I18nFactory
        .getI18n(SpecimenTypesViewForm.class);

    @SuppressWarnings("nls")
    public static final String ID =
        "edu.ualberta.med.biobank.forms.SpecimenTypesViewForm";

    @SuppressWarnings("nls")
    public static final String OK_MESSAGE =
        "Add or edit a specimen type";

    private SpecimenTypeEntryInfoTree specimenWidget;

    private Boolean createAllowed;

    @SuppressWarnings("nls")
    @Override
    public void init() throws Exception {
        setPartName(SpecimenType.NAME.plural().toString());
        try {
            this.createAllowed = SessionManager.getAppService().isAllowed(
                new SpecimenTypeCreatePermission());
        } catch (ApplicationException e) {
            BgcPlugin.openAsyncError(
                i18n.tr("Unable to retrieve permissions"));
        }
    }

    @Override
    protected void createFormContent() throws Exception {
        form.setText(SpecimenType.NAME.plural().toString());
        page.setLayout(new GridLayout(1, false));

        createGlobalSpecimenTypeSection();
    }

    @SuppressWarnings("nls")
    private void createGlobalSpecimenTypeSection() {
        Section section = createSection(SpecimenType.NAME.plural().toString());
        specimenWidget =
            new SpecimenTypeEntryInfoTree(section,
                i18n.tr("Add a new global specimen type"),
                i18n.tr("Edit the global specimen type"));
        specimenWidget.adaptToToolkit(toolkit, true);
        toolkit.paintBordersFor(specimenWidget);

        addSectionToolbar(section,
            i18n.tr("Add a specimen type"),
            new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    specimenWidget.addSpecimenType();
                }
            }, SpecimenTypeWrapper.class);
        section.setClient(specimenWidget);
    }

    @SuppressWarnings("nls")
    protected void checkEditAccess() {
        if (!createAllowed) {
            BgcPlugin.openAccessDeniedErrorMessage();
            throw new RuntimeException(
                i18n.tr("Cannot access Specimen Type editor. Access Denied."));
        }
    }

    @Override
    public void setValues() throws Exception {
        specimenWidget.reload();
    }
}
