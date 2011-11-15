package edu.ualberta.med.biobank.forms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.ui.forms.widgets.Section;

import com.google.gwt.user.client.ui.HasValue;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.action.study.StudyGetClinicInfoAction.ClinicInfo;
import edu.ualberta.med.biobank.common.wrappers.ContactWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.model.Contact;
import edu.ualberta.med.biobank.mvp.presenter.impl.StudyEntryPresenter;
import edu.ualberta.med.biobank.mvp.view.IView;
import edu.ualberta.med.biobank.mvp.view.form.AbstractEntryFormView;
import edu.ualberta.med.biobank.mvp.view.form.BaseForm;
import edu.ualberta.med.biobank.mvp.view.item.TableItem;
import edu.ualberta.med.biobank.mvp.view.item.TextItem;
import edu.ualberta.med.biobank.mvp.view.item.TranslatedItem;
import edu.ualberta.med.biobank.mvp.view.item.TranslatedItem.Translator;
import edu.ualberta.med.biobank.mvp.view.util.InputTable;
import edu.ualberta.med.biobank.widgets.infotables.entry.ClinicAddInfoTable;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class StudyEntryFormView extends AbstractEntryFormView implements
    StudyEntryPresenter.View {

    private final TextItem name = new TextItem();
    private final TextItem nameShort = new TextItem();
    private IView activityStatusComboView;
    private final TableItem<ContactWrapper> contactWrappers =
        new TableItem<ContactWrapper>();
    private final TranslatedItem<Collection<ClinicInfo>, Collection<ContactWrapper>> clinics =
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
            Collection<ContactWrapper> contacts =
                new ArrayList<ContactWrapper>();
            WritableApplicationService appService =
                SessionManager.getAppService();
            for (ClinicInfo info : foreign) {
                for (Contact c : info.getContacts()) {
                    ContactWrapper wrapper = new ContactWrapper(appService, c);
                    contacts.add(wrapper);
                }
            }
            return contacts;
        }
    }

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
        return clinics;
    }

    @Override
    public void onCreate(BaseForm baseForm) {
        super.onCreate(baseForm);

        baseForm.setTitle(Messages.StudyEntryForm_main_title);

        InputTable table = new InputTable(baseForm.getPage());

        name.setValidationControl(table.addLabel("name"));
        name.setText(table.addText());

        nameShort.setValidationControl(table.addLabel("nameShort"));
        nameShort.setText(table.addText());

        // TODO: what about ValidationControl?
        table.addLabel("activityStatus");
        activityStatusComboView.create(table);

        Section studySection = baseForm.createSection("Clinics");
        WritableApplicationService appService =
            SessionManager.getAppService();
        StudyWrapper studyWrapper = new StudyWrapper(appService);
        boolean superAdmin = SessionManager.getUser().isSuperAdmin();
        final ClinicAddInfoTable clinicsTable =
            new ClinicAddInfoTable(studySection, studyWrapper);
        studySection.setClient(clinicsTable);
        if (superAdmin) {
            BaseForm.addSectionToolbar(studySection,
                Messages.SiteEntryForm_studies_add, new SelectionAdapter() {
                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        clinicsTable.createClinicContact();
                    }
                }, ContactWrapper.class, null);
        }
        contactWrappers.setTable(clinicsTable);

        // TODO: fix comment section
        // comment.setText(widget.comment);
    }

    @Override
    public String getOkMessage() {
        return "Everything is A-Okay";
    }
}