package edu.ualberta.med.biobank.forms;

import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.Section;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.AliquotedSpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContactWrapper;
import edu.ualberta.med.biobank.common.wrappers.SourceSpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.model.AliquotedSpecimen;
import edu.ualberta.med.biobank.model.Contact;
import edu.ualberta.med.biobank.model.SourceSpecimen;
import edu.ualberta.med.biobank.mvp.presenter.impl.StudyEntryPresenter;
import edu.ualberta.med.biobank.mvp.user.ui.ListField;
import edu.ualberta.med.biobank.mvp.user.ui.ValueField;
import edu.ualberta.med.biobank.mvp.view.IView;
import edu.ualberta.med.biobank.mvp.view.form.AbstractEntryFormView;
import edu.ualberta.med.biobank.mvp.view.form.BaseForm;
import edu.ualberta.med.biobank.mvp.view.item.AdaptedListField;
import edu.ualberta.med.biobank.mvp.view.item.Adapter;
import edu.ualberta.med.biobank.mvp.view.item.TableItem;
import edu.ualberta.med.biobank.mvp.view.item.TextBox;
import edu.ualberta.med.biobank.mvp.view.util.InputTable;
import edu.ualberta.med.biobank.widgets.infotables.entry.ClinicAddInfoTable;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class StudyEntryFormView extends AbstractEntryFormView implements
    StudyEntryPresenter.View {

    private final TextBox name = new TextBox();
    private final TextBox nameShort = new TextBox();
    private IView activityStatusComboView;
    private final TableItem<ContactWrapper> contactWrappers =
        new TableItem<ContactWrapper>();
    private final TableItem<SourceSpecimenWrapper> srcSpcWrappers =
        new TableItem<SourceSpecimenWrapper>();
    private final TableItem<AliquotedSpecimenWrapper> aqSpcWrappers =
        new TableItem<AliquotedSpecimenWrapper>();

    private final AdaptedListField<Contact, ContactWrapper> contactsAdapter =
        new AdaptedListField<Contact, ContactWrapper>(contactWrappers,
            CONTACT_ADAPTER);

    private static final ContactAdapter CONTACT_ADAPTER = new ContactAdapter();

    private static class ContactAdapter implements
        Adapter<Contact, ContactWrapper> {
        @Override
        public Contact adapt(ContactWrapper contact) {
            return contact.getWrappedObject();
        }

        @Override
        public ContactWrapper unadapt(Contact adapted) {
            return new ContactWrapper(SessionManager.getAppService(), adapted);
        }
    }

    private final AdaptedListField<SourceSpecimen, SourceSpecimenWrapper> srcSpcsAdapter =
        new AdaptedListField<SourceSpecimen, SourceSpecimenWrapper>(
            srcSpcWrappers, SOURCE_SPC_ADAPTER);

    private static final SrcSpcTranslator SOURCE_SPC_ADAPTER =
        new SrcSpcTranslator();

    private static class SrcSpcTranslator
        implements Adapter<SourceSpecimen, SourceSpecimenWrapper> {
        @Override
        public SourceSpecimen adapt(SourceSpecimenWrapper unadapted) {
            return unadapted.getWrappedObject();
        }

        @Override
        public SourceSpecimenWrapper unadapt(SourceSpecimen adapted) {
            return new SourceSpecimenWrapper(SessionManager.getAppService(),
                adapted);
        }
    }

    private final AdaptedListField<AliquotedSpecimen, AliquotedSpecimenWrapper> aqSpcsTranslator =
        new AdaptedListField<AliquotedSpecimen, AliquotedSpecimenWrapper>(
            aqSpcWrappers, ALIQUOTED_SPC_ADAPTER);

    private static final AqSpcAdapter ALIQUOTED_SPC_ADAPTER =
        new AqSpcAdapter();

    private static class AqSpcAdapter
        implements Adapter<AliquotedSpecimen, AliquotedSpecimenWrapper> {
        @Override
        public AliquotedSpecimen adapt(AliquotedSpecimenWrapper unadapted) {
            return unadapted.getWrappedObject();
        }

        @Override
        public AliquotedSpecimenWrapper unadapt(AliquotedSpecimen adapted) {
            return new AliquotedSpecimenWrapper(SessionManager.getAppService(),
                adapted);
        }
    }

    private BaseForm baseForm;
    private WritableApplicationService appService;
    private StudyWrapper studyWrapper;
    private boolean isSuperAdmin;

    @Override
    public void setActivityStatusComboView(IView view) {
        this.activityStatusComboView = view;
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
    public ListField<Contact> getContacts() {
        return contactsAdapter;
    }

    @Override
    public ListField<SourceSpecimen> getSourceSpecimens() {
        return srcSpcsAdapter;
    }

    @Override
    public ListField<AliquotedSpecimen> getAliquotedSpecimens() {
        return aqSpcsTranslator;
    }

    @Override
    public void onCreate(BaseForm baseForm) {
        super.onCreate(baseForm);
        this.baseForm = baseForm;
        baseForm.setTitle(Messages.StudyEntryForm_main_title);
        editor.setPartName(NLS.bind(Messages.StudyEntryForm_title_edit,
            nameShort.getValue()));

        InputTable table = new InputTable(baseForm.getPage());

        name.setValidationControl(table.addLabel("name"));
        name.setText(table.addText());

        nameShort.setValidationControl(table.addLabel("nameShort"));
        nameShort.setText(table.addText());

        // TODO: what about ValidationControl?
        table.addLabel("activityStatus");
        activityStatusComboView.create(table);

        appService = SessionManager.getAppService();
        studyWrapper = new StudyWrapper(appService);
        isSuperAdmin = SessionManager.getUser().isSuperAdmin();

        createCommentsSection();
        createClinicSection();
        createSourceSpecimensSection();
        createAliquotedSpecimensSection();
        createStudyEventAttrSection();
    }

    private void createCommentsSection() {
        // TODO: fix comment section
        // comment.setText(widget.comment);
    }

    private void createClinicSection() {
        Section section =
            baseForm.createSection(Messages.StudyEntryForm_contacts_title);
        final ClinicAddInfoTable clinicsTable = null;
        // new ClinicAddInfoTable(section, studyWrapper);
        section.setClient(clinicsTable);
        if (isSuperAdmin) {
            BaseForm.addSectionToolbar(section,
                Messages.SiteEntryForm_studies_add, new SelectionAdapter() {
                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        clinicsTable.createClinicContact();
                    }
                }, null);
        }
        // contactWrappers.setTable(clinicsTable);
    }

    private void createSourceSpecimensSection() {
        // Section section =
        // baseForm
        // .createSection(Messages.StudyEntryForm_source_specimens_title);
        // final SourceSpecimenEntryInfoTable sourceSpecimenTable =
        // new SourceSpecimenEntryInfoTable(section, studyWrapper, null);
        // section.setClient(sourceSpecimenTable);
        // if (isSuperAdmin) {
        // BaseForm.addSectionToolbar(section,
        // Messages.SiteEntryForm_studies_add, new SelectionAdapter() {
        // @Override
        // public void widgetSelected(SelectionEvent e) {
        // sourceSpecimenTable.addSourceSpecimen();
        // }
        // }, null);
        // }
        // srcSpcWrappers.setTable(sourceSpecimenTable);
    }

    private void createAliquotedSpecimensSection() {
        // Section section = baseForm.createSection(
        // Messages.StudyEntryForm_aliquoted_specimens_title);
        // final AliquotedSpecimenEntryInfoTable aliquotedSpecimenTable =
        // new AliquotedSpecimenEntryInfoTable(section, studyWrapper);
        // section.setClient(aliquotedSpecimenTable);
        // if (isSuperAdmin) {
        // BaseForm.addSectionToolbar(section,
        // Messages.SiteEntryForm_studies_add, new SelectionAdapter() {
        // @Override
        // public void widgetSelected(SelectionEvent e) {
        // aliquotedSpecimenTable.addAliquotedSpecimen();
        // }
        // }, null);
        // }
        // aqSpcWrappers.setTable(aliquotedSpecimenTable);
    }

    private void createStudyEventAttrSection() {
        Composite client = baseForm.createSectionWithClient(
            Messages.StudyEntryForm_visit_info_title);
        baseForm.getToolkit().createLabel(client,
            "Select the information that is collected from a patient during a "
                + "collection event.");

        // TODO this needs implementation
    }

    @Override
    public String getOkMessage() {
        return "Everything is A-Okay";
    }
}