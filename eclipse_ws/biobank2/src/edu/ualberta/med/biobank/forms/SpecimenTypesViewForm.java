package edu.ualberta.med.biobank.forms;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.ui.forms.widgets.Section;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.action.specimenType.SpecimenTypeGetAllAction;
import edu.ualberta.med.biobank.common.permission.specimenType.SpecimenTypeCreatePermission;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenTypeWrapper;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.model.SpecimenType;
import edu.ualberta.med.biobank.widgets.trees.infos.SpecimenTypeEntryInfoTree;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class SpecimenTypesViewForm extends BiobankFormBase {

    public static final String ID =
        "edu.ualberta.med.biobank.forms.SpecimenTypesViewForm"; //$NON-NLS-1$

    public static final String OK_MESSAGE =
        Messages.SpecimenTypesViewForm_ok_msg;

    private SpecimenTypeEntryInfoTree specimenWidget;

    private List<SpecimenTypeWrapper> globalSpecimenTypeWrappers =
        new ArrayList<SpecimenTypeWrapper>();

    private List<SpecimenType> globalSpecimenTypes;

    private Boolean createAllowed;

    @Override
    public void init() throws Exception {
        setPartName(Messages.SpecimenTypesViewForm_title);
        try {
            this.createAllowed = SessionManager.getAppService().isAllowed(
                new SpecimenTypeCreatePermission());
        } catch (ApplicationException e) {
            BgcPlugin.openAsyncError("Error", "Unable to retrieve permissions");
        }
    }

    @Override
    protected void createFormContent() throws Exception {
        form.setText(Messages.SpecimenTypesViewForm_title);
        page.setLayout(new GridLayout(1, false));

        updateSpecimenTypeInfo();
        createGlobalSpecimenTypeSection();
    }

    private void updateSpecimenTypeInfo() throws Exception {
        globalSpecimenTypes = SessionManager.getAppService().doAction(
            new SpecimenTypeGetAllAction()).getList();
        Assert.isNotNull(globalSpecimenTypes);
        globalSpecimenTypeWrappers =
            ModelWrapper.wrapModelCollection(SessionManager.getAppService(),
                globalSpecimenTypes, SpecimenTypeWrapper.class);
    }

    private void createGlobalSpecimenTypeSection() {
        Section section = createSection(Messages.SpecimenTypesViewForm_title);
        specimenWidget =
            new SpecimenTypeEntryInfoTree(section,
                globalSpecimenTypeWrappers,
                Messages.SpecimenTypesViewForm_add_type_label,
                Messages.SpecimenTypesViewForm_edit_type_label);
        specimenWidget.adaptToToolkit(toolkit, true);
        toolkit.paintBordersFor(specimenWidget);

        addSectionToolbar(section,
            Messages.SpecimenTypesViewForm_add_specimen_button,
            new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    specimenWidget.addSpecimenType();
                }
            }, SpecimenTypeWrapper.class);
        section.setClient(specimenWidget);
    }

    protected void checkEditAccess() {
        if (!createAllowed) {
            BgcPlugin.openAccessDeniedErrorMessage();
            throw new RuntimeException(
                Messages.SpecimenTypesViewForm_access_denied_error_msg);
        }
    }

    @Override
    public void setValues() throws Exception {
        // TODO Auto-generated method stub

    }

}
