package edu.ualberta.med.biobank.mvp.view.form;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.Section;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.action.info.StudyCountInfo;
import edu.ualberta.med.biobank.common.wrappers.ContactWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.forms.Messages;
import edu.ualberta.med.biobank.mvp.presenter.impl.SiteEntryPresenter;
import edu.ualberta.med.biobank.mvp.user.ui.HasField;
import edu.ualberta.med.biobank.mvp.view.IView;
import edu.ualberta.med.biobank.mvp.view.item.TableItem;
import edu.ualberta.med.biobank.mvp.view.item.TextItem;
import edu.ualberta.med.biobank.mvp.view.item.TranslatedItem;
import edu.ualberta.med.biobank.mvp.view.item.TranslatedItem.Translator;
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
    private final TextItem name = new TextItem();
    private final TextItem nameShort = new TextItem();
    private final TableItem<StudyWrapper> studyWrappers =
        new TableItem<StudyWrapper>();
    private final TranslatedItem<Collection<StudyCountInfo>, Collection<StudyWrapper>> studies =
        TranslatedItem.from(studyWrappers, STUDY_TRANSLATOR);

    private IView addressEntryView;
    private IView activityStatusComboView;

    private static final StudyTranslator STUDY_TRANSLATOR =
        new StudyTranslator();

    private static class StudyTranslator implements
        Translator<Collection<StudyCountInfo>, Collection<StudyWrapper>> {
        @Override
        public Collection<StudyCountInfo> fromDelegate(
            Collection<StudyWrapper> delegate) {
            Collection<StudyCountInfo> studies = new ArrayList<StudyCountInfo>();
            for (StudyWrapper study : delegate) {
                StudyCountInfo studyInfo =
                    new StudyCountInfo(study.getWrappedObject(), -1l, -1l);
                studies.add(studyInfo);
            }
            return studies;
        }

        @Override
        public Collection<StudyWrapper> toDelegate(Collection<StudyCountInfo> foreign) {
            Collection<StudyWrapper> studies = new ArrayList<StudyWrapper>();
            WritableApplicationService appService =
                SessionManager.getAppService();
            for (StudyCountInfo study : foreign) {
                StudyWrapper wrapper =
                    new StudyWrapper(appService, study.getStudy());
                studies.add(wrapper);
            }
            return studies;
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
    public HasField<String> getName() {
        return name;
    }

    @Override
    public HasField<String> getNameShort() {
        return nameShort;
    }

    @Override
    public HasField<Collection<StudyCountInfo>> getStudies() {
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
