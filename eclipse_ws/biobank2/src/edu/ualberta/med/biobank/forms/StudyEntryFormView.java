package edu.ualberta.med.biobank.forms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.Section;

import com.google.gwt.user.client.ui.HasValue;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.action.study.StudyGetClinicInfoAction.ClinicInfo;
import edu.ualberta.med.biobank.common.wrappers.AliquotedSpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContactWrapper;
import edu.ualberta.med.biobank.common.wrappers.SourceSpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.model.AliquotedSpecimen;
import edu.ualberta.med.biobank.model.Contact;
import edu.ualberta.med.biobank.model.SourceSpecimen;
import edu.ualberta.med.biobank.mvp.presenter.impl.StudyEntryPresenter;
import edu.ualberta.med.biobank.mvp.view.IView;
import edu.ualberta.med.biobank.mvp.view.form.AbstractEntryFormView;
import edu.ualberta.med.biobank.mvp.view.form.BaseForm;
import edu.ualberta.med.biobank.mvp.view.item.TableItem;
import edu.ualberta.med.biobank.mvp.view.item.TextItem;
import edu.ualberta.med.biobank.mvp.view.item.TranslatedItem;
import edu.ualberta.med.biobank.mvp.view.item.TranslatedItem.Translator;
import edu.ualberta.med.biobank.mvp.view.util.InputTable;
import edu.ualberta.med.biobank.widgets.infotables.entry.AliquotedSpecimenEntryInfoTable;
import edu.ualberta.med.biobank.widgets.infotables.entry.ClinicAddInfoTable;
import edu.ualberta.med.biobank.widgets.infotables.entry.SourceSpecimenEntryInfoTable;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class StudyEntryFormView extends AbstractEntryFormView implements
    StudyEntryPresenter.View {

    private final TextItem name = new TextItem();
    private final TextItem nameShort = new TextItem();
    private IView activityStatusComboView;
    private final TableItem<ContactWrapper> contactWrappers =
        new TableItem<ContactWrapper>();
    private final TableItem<SourceSpecimenWrapper> srcSpcWrappers =
        new TableItem<SourceSpecimenWrapper>();
    private final TableItem<AliquotedSpecimenWrapper> aqSpcWrappers =
        new TableItem<AliquotedSpecimenWrapper>();

    private final TranslatedItem<Collection<ClinicInfo>, Collection<ContactWrapper>> clinicsTranslator =
        TranslatedItem.from(contactWrappers, CONTACT_TRANSLATOR);

    private static final ContactTranslator CONTACT_TRANSLATOR =
        new ContactTranslator();

    private static class ContactTranslator implements
        Translator<Collection<ClinicInfo>, Collection<ContactWrapper>> {
        @Override
        public Collection<ClinicInfo> fromDelegate(
            Collection<ContactWrapper> delegate) {
            Collection<ClinicInfo> clinicInfos = new ArrayList<ClinicInfo>();
            for (ContactWrapper contact : delegate) {
                ClinicInfo clinicInfo =
                    new ClinicInfo(contact.getClinic().getWrappedObject(), -1l,
                        -1l, Arrays.asList(contact.getWrappedObject()));
                clinicInfos.add(clinicInfo);
            }
            return clinicInfos;
        }

        @Override
        public Collection<ContactWrapper> toDelegate(
            Collection<ClinicInfo> foreign) {
            Collection<ContactWrapper> contactWrappers =
                new ArrayList<ContactWrapper>();
            WritableApplicationService appService =
                SessionManager.getAppService();
            for (ClinicInfo info : foreign) {
                for (Contact c : info.getContacts()) {
                    ContactWrapper wrapper = new ContactWrapper(appService, c);
                    contactWrappers.add(wrapper);
                }
            }
            return contactWrappers;
        }
    }

    private final TranslatedItem<Collection<SourceSpecimen>, Collection<SourceSpecimenWrapper>> srcSpcsTranslator =
        TranslatedItem.from(srcSpcWrappers, SOURCE_SPC_TRANSLATOR);

    private static final SrcSpcTranslator SOURCE_SPC_TRANSLATOR =
        new SrcSpcTranslator();

    private static class SrcSpcTranslator
        implements
        Translator<Collection<SourceSpecimen>, Collection<SourceSpecimenWrapper>> {
        @Override
        public Collection<SourceSpecimen> fromDelegate(
            Collection<SourceSpecimenWrapper> delegate) {
            Collection<SourceSpecimen> srcSpcs =
                new ArrayList<SourceSpecimen>();
            for (SourceSpecimenWrapper ssWrapper : delegate) {
                srcSpcs.add(ssWrapper.getWrappedObject());
            }
            return srcSpcs;
        }

        @Override
        public Collection<SourceSpecimenWrapper> toDelegate(
            Collection<SourceSpecimen> foreign) {
            Collection<SourceSpecimenWrapper> ssWrappers =
                new ArrayList<SourceSpecimenWrapper>();
            WritableApplicationService appService =
                SessionManager.getAppService();
            for (SourceSpecimen ss : foreign) {
                SourceSpecimenWrapper wrapper =
                    new SourceSpecimenWrapper(appService, ss);
                ssWrappers.add(wrapper);
            }
            return ssWrappers;
        }
    }

    private final TranslatedItem<Collection<AliquotedSpecimen>, Collection<AliquotedSpecimenWrapper>> aqSpcsTranslator =
        TranslatedItem.from(aqSpcWrappers, ALIQUOTED_SPC_TRANSLATOR);

    private static final AqSpcTranslator ALIQUOTED_SPC_TRANSLATOR =
        new AqSpcTranslator();

    private static class AqSpcTranslator
        implements
        Translator<Collection<AliquotedSpecimen>, Collection<AliquotedSpecimenWrapper>> {
        @Override
        public Collection<AliquotedSpecimen> fromDelegate(
            Collection<AliquotedSpecimenWrapper> delegate) {
            Collection<AliquotedSpecimen> aqSpcs =
                new ArrayList<AliquotedSpecimen>();
            for (AliquotedSpecimenWrapper ssWrapper : delegate) {
                aqSpcs.add(ssWrapper.getWrappedObject());
            }
            return aqSpcs;
        }

        @Override
        public Collection<AliquotedSpecimenWrapper> toDelegate(
            Collection<AliquotedSpecimen> foreign) {
            Collection<AliquotedSpecimenWrapper> asWrappers =
                new ArrayList<AliquotedSpecimenWrapper>();
            WritableApplicationService appService =
                SessionManager.getAppService();
            for (AliquotedSpecimen ss : foreign) {
                AliquotedSpecimenWrapper wrapper =
                    new AliquotedSpecimenWrapper(appService, ss);
                asWrappers.add(wrapper);
            }
            return asWrappers;
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
    public HasValue<String> getName() {
        return name;
    }

    @Override
    public HasValue<String> getNameShort() {
        return nameShort;
    }

    @Override
    public HasValue<Collection<ClinicInfo>> getClinics() {
        return clinicsTranslator;
    }

    @Override
    public HasValue<Collection<SourceSpecimen>> getSourceSpecimens() {
        return srcSpcsTranslator;
    }

    @Override
    public HasValue<Collection<AliquotedSpecimen>> getAliquotedSpecimens() {
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
        final ClinicAddInfoTable clinicsTable =
            new ClinicAddInfoTable(section, studyWrapper);
        section.setClient(clinicsTable);
        if (isSuperAdmin) {
            BaseForm.addSectionToolbar(section,
                Messages.SiteEntryForm_studies_add, new SelectionAdapter() {
                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        clinicsTable.createClinicContact();
                    }
                }, ContactWrapper.class, null);
        }
        contactWrappers.setTable(clinicsTable);
    }

    private void createSourceSpecimensSection() {
        Section section =
            baseForm
                .createSection(Messages.StudyEntryForm_source_specimens_title);
        final SourceSpecimenEntryInfoTable sourceSpecimenTable =
            new SourceSpecimenEntryInfoTable(section, studyWrapper);
        section.setClient(sourceSpecimenTable);
        if (isSuperAdmin) {
            BaseForm.addSectionToolbar(section,
                Messages.SiteEntryForm_studies_add, new SelectionAdapter() {
                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        sourceSpecimenTable.addSourceSpecimen();
                    }
                }, SourceSpecimenWrapper.class, null);
        }
        srcSpcWrappers.setTable(sourceSpecimenTable);
    }

    private void createAliquotedSpecimensSection() {
        Section section = baseForm.createSection(
            Messages.StudyEntryForm_aliquoted_specimens_title);
        final AliquotedSpecimenEntryInfoTable aliquotedSpecimenTable =
            new AliquotedSpecimenEntryInfoTable(section, studyWrapper);
        section.setClient(aliquotedSpecimenTable);
        if (isSuperAdmin) {
            BaseForm.addSectionToolbar(section,
                Messages.SiteEntryForm_studies_add, new SelectionAdapter() {
                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        aliquotedSpecimenTable.addAliquotedSpecimen();
                    }
                }, SourceSpecimenWrapper.class, null);
        }
        aqSpcWrappers.setTable(aliquotedSpecimenTable);
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