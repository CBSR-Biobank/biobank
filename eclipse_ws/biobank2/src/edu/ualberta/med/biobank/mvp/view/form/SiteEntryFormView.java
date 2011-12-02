package edu.ualberta.med.biobank.mvp.view.form;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.Section;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.action.info.StudyCountInfo;
import edu.ualberta.med.biobank.common.action.info.StudyInfo;
import edu.ualberta.med.biobank.common.wrappers.ContactWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.forms.Messages;
import edu.ualberta.med.biobank.mvp.presenter.impl.SiteEntryPresenter;
import edu.ualberta.med.biobank.mvp.user.ui.ListField;
import edu.ualberta.med.biobank.mvp.user.ui.ValueField;
import edu.ualberta.med.biobank.mvp.view.IView;
import edu.ualberta.med.biobank.mvp.view.item.AdaptedListField;
import edu.ualberta.med.biobank.mvp.view.item.Adapter;
import edu.ualberta.med.biobank.mvp.view.item.TableItem;
import edu.ualberta.med.biobank.mvp.view.item.TextBox;
import edu.ualberta.med.biobank.mvp.view.util.InputTable;
import edu.ualberta.med.biobank.widgets.infotables.entry.StudyAddInfoTable;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

/**
 * 
 * @author jferland
 * 
 */
public class SiteEntryFormView extends AbstractEntryFormView implements
    SiteEntryPresenter.View {
    private final TextBox name = new TextBox();
    private final TextBox nameShort = new TextBox();
    private final TableItem<StudyWrapper> studyWrappers =
        new TableItem<StudyWrapper>();
    private final AdaptedListField<StudyInfo, StudyWrapper> studies =
        new AdaptedListField<StudyInfo, StudyWrapper>(studyWrappers,
            STUDY_ADAPTER);

    private IView addressEntryView;
    private IView activityStatusComboView;

    private static final StudyAdapter STUDY_ADAPTER = new StudyAdapter();

    private static class StudyAdapter implements
        Adapter<StudyInfo, StudyWrapper> {
        @Override
        public StudyInfo adapt(StudyWrapper unadapted) {
            return new StudyInfo(unadapted.getWrappedObject(), -1l, -1l,
                null, null, null, null);
        }

        @Override
        public StudyWrapper unadapt(StudyInfo adapted) {
            return new StudyWrapper(SessionManager.getAppService(),
                adapted.getStudy());
        }
    }

    @Override
    public void setActivityStatusComboView(IView view) {
        this.activityStatusComboView = view;
    }

    @Override
    public void setAddressEditView(IView view) {
        this.addressEntryView = view;
    }

    @Override
    public ValueField<String> getName() {
        return name;
    }

    @Override
    public ValueField<String> getNameShort() {
        return nameShort;
    }

    @Override
    public ListField<StudyCountInfo> getStudies() {
        return studies;
    }

    @Override
    public void onCreate(BaseForm baseForm) {
        super.onCreate(baseForm);

        baseForm.setTitle(Messages.SiteEntryForm_main_title);

        InputTable table = new InputTable(baseForm.getPage());

        name.setValidationControl(table.addLabel("name"));
        name.setText(table.addText());

        nameShort.setValidationControl(table.addLabel("nameShort"));
        nameShort.setText(table.addText());

        // TODO: what about ValidationControl?
        table.addLabel("activityStatus");
        activityStatusComboView.create(table);

        Composite addressClient = baseForm.createSectionWithClient("Address");
        addressEntryView.create(addressClient);

        Section studySection = baseForm.createSection("Studies");
        WritableApplicationService appService =
            SessionManager.getAppService();
        SiteWrapper siteWrapper = new SiteWrapper(appService);
        boolean superAdmin = SessionManager.getUser().isSuperAdmin();
        final StudyAddInfoTable studiesTable =
            new StudyAddInfoTable(studySection, siteWrapper, superAdmin);
        studySection.setClient(studiesTable);
        if (superAdmin) {
            BaseForm.addSectionToolbar(studySection,
                Messages.SiteEntryForm_studies_add, new SelectionAdapter() {
                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        studiesTable.createStudyDlg();
                    }
                }, ContactWrapper.class, null);
        }
        studyWrappers.setTable(studiesTable);

        // TODO: fix comment section
        // comment.setText(widget.comment);
    }

    @Override
    public String getOkMessage() {
        return "Everything is A-Okay";
    }
}
