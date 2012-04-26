package edu.ualberta.med.biobank.forms;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.Section;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.action.study.StudyGetInfoAction;
import edu.ualberta.med.biobank.common.action.study.StudyInfo;
import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.EventAttrTypeEnum;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.forms.input.FormInput;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseText;
import edu.ualberta.med.biobank.gui.common.widgets.IInfoTableDoubleClickItemListener;
import edu.ualberta.med.biobank.gui.common.widgets.IInfoTableEditItemListener;
import edu.ualberta.med.biobank.gui.common.widgets.InfoTableEvent;
import edu.ualberta.med.biobank.gui.common.widgets.InfoTableSelection;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.AliquotedSpecimen;
import edu.ualberta.med.biobank.model.CollectionEvent;
import edu.ualberta.med.biobank.model.Comment;
import edu.ualberta.med.biobank.model.EventAttrCustom;
import edu.ualberta.med.biobank.model.HasName;
import edu.ualberta.med.biobank.model.HasNameShort;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.SourceSpecimen;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.model.StudyEventAttr;
import edu.ualberta.med.biobank.treeview.AdapterBase;
import edu.ualberta.med.biobank.treeview.admin.ClinicAdapter;
import edu.ualberta.med.biobank.treeview.admin.StudyAdapter;
import edu.ualberta.med.biobank.treeview.patient.StudyWithPatientAdapter;
import edu.ualberta.med.biobank.widgets.infotables.AliquotedSpecimenInfoTable;
import edu.ualberta.med.biobank.widgets.infotables.CommentsInfoTable;
import edu.ualberta.med.biobank.widgets.infotables.SourceSpecimenInfoTable;
import edu.ualberta.med.biobank.widgets.infotables.StudyContactInfoTable;
import edu.ualberta.med.biobank.widgets.infotables.StudyContactInfoTable.ClinicContacts;

public class StudyViewForm extends BiobankViewForm {
    public static final I18n i18n = I18nFactory.getI18n(StudyViewForm.class);

    @SuppressWarnings("nls")
    public static final String ID =
        "edu.ualberta.med.biobank.forms.StudyViewForm";

    @SuppressWarnings("nls")
    private static final String DATE_PROCESSED_INFO_FIELD_NAME =
        i18n.tr(":Date Processed");

    private final StudyWrapper study =
        new StudyWrapper(SessionManager.getAppService());

    private BgcBaseText nameLabel;
    private BgcBaseText nameShortLabel;
    private BgcBaseText activityStatusLabel;
    private BgcBaseText patientTotal;
    private BgcBaseText visitTotal;

    private StudyContactInfoTable contactsTable;
    private AliquotedSpecimenInfoTable aliquotedSpecimenTable;
    private SourceSpecimenInfoTable sourceSpecimenTable;

    private StudyInfo studyInfo;

    private static class StudyEventAttrCustomInfo extends EventAttrCustom {
        public BgcBaseText widget;
    }

    private List<StudyEventAttrCustomInfo> pvCustomInfoList;

    private CommentsInfoTable commentTable;

    @SuppressWarnings("nls")
    @Override
    public void init() throws Exception {
        Assert
            .isTrue(
                (adapter instanceof StudyAdapter || adapter instanceof StudyWithPatientAdapter),
                "Invalid editor input: object of type {0}"
                    + adapter.getClass().getName());

        updateStudyInfo();
        setPartName(i18n.tr("Study {0}", study.getNameShort()));
        pvCustomInfoList = new ArrayList<StudyEventAttrCustomInfo>();
    }

    private void updateStudyInfo() throws Exception {
        studyInfo =
            SessionManager.getAppService().doAction(
                new StudyGetInfoAction(adapter.getId()));
        Study s = studyInfo.getStudy();
        Set<AliquotedSpecimen> as = studyInfo.getAliquotedSpcs();
        Set<SourceSpecimen> ss = studyInfo.getSourceSpecimens();
        Set<StudyEventAttr> ea = studyInfo.getStudyEventAttrs();
        s.setAliquotedSpecimens(as);
        s.setSourceSpecimens(ss);
        s.setStudyEventAttrs(ea);
        study.setWrappedObject(s);
    }

    @SuppressWarnings("nls")
    @Override
    protected void createFormContent() throws Exception {
        if (study.getName() != null) {
            form.setText(i18n.tr("Study {0}", study.getName()));
        }

        GridLayout layout = new GridLayout(1, false);
        page.setLayout(layout);
        page.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        Composite client = toolkit.createComposite(page);
        client.setLayout(new GridLayout(2, false));
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(client);

        nameLabel = createReadOnlyLabelledField(client, SWT.NONE,
            HasName.PropertyName.NAME.toString());
        nameShortLabel = createReadOnlyLabelledField(client, SWT.NONE,
            HasNameShort.PropertyName.NAME_SHORT.toString());
        activityStatusLabel = createReadOnlyLabelledField(client, SWT.NONE,
            ActivityStatus.NAME.singular().toString());
        patientTotal = createReadOnlyLabelledField(client, SWT.NONE,
            i18n.tr("Total {0}", Patient.NAME.plural().toString()));
        visitTotal = createReadOnlyLabelledField(client, SWT.NONE,
            i18n.tr("Total {0}", CollectionEvent.NAME.plural().toString()));

        createCommentsSection();
        createClinicSection();
        createSourceSpecimenSection();
        createAliquotedSpecimenSection();
        createStudyEventAttrSection();
        setStudySectionValues();
        setStudyEventAttrValues();
    }

    private void createCommentsSection() {
        Composite client =
            createSectionWithClient(Comment.NAME.plural().toString());
        commentTable =
            new CommentsInfoTable(client,
                study.getCommentCollection(false));
        commentTable.adaptToToolkit(toolkit, true);
        toolkit.paintBordersFor(commentTable);
    }

    private void createClinicSection() {
        @SuppressWarnings("nls")
        Composite client =
            createSectionWithClient(i18n.tr("Clinic Information"));

        contactsTable = new StudyContactInfoTable(client, study);
        contactsTable
            .addClickListener(new IInfoTableDoubleClickItemListener<ClinicContacts>() {

                @Override
                public void doubleClick(InfoTableEvent<ClinicContacts> event) {
                    ClinicWrapper c =
                        ((ClinicContacts) ((InfoTableSelection) event
                            .getSelection()).getObject()).getClinic();
                    AdapterBase.openForm(
                        new FormInput(
                            new ClinicAdapter(null,
                                c)),
                        ClinicViewForm.ID);
                }
            });
        contactsTable
            .addEditItemListener(new IInfoTableEditItemListener<ClinicContacts>() {
                @Override
                public void editItem(InfoTableEvent<ClinicContacts> event) {
                    ClinicWrapper c =
                        ((ClinicContacts) ((InfoTableSelection) event
                            .getSelection()).getObject()).getClinic();
                    AdapterBase.openForm(
                        new FormInput(
                            new ClinicAdapter(null,
                                c)),
                        ClinicEntryForm.ID);
                }
            });
        contactsTable.adaptToToolkit(toolkit, true);
        toolkit.paintBordersFor(contactsTable);
    }

    private void setStudySectionValues() throws Exception {
        setTextValue(nameLabel, study.getName());
        setTextValue(nameShortLabel, study.getNameShort());
        setTextValue(activityStatusLabel, study.getActivityStatus());
        setTextValue(patientTotal, study.getPatientCount(true));
        setTextValue(visitTotal, study.getCollectionEventCount());
    }

    private void createAliquotedSpecimenSection() {
        @SuppressWarnings("nls")
        Section section = createSection(i18n.tr("Aliquoted specimen types"));

        aliquotedSpecimenTable =
            new AliquotedSpecimenInfoTable(section,
                study.getAliquotedSpecimenCollection(true));
        section.setClient(aliquotedSpecimenTable);
        aliquotedSpecimenTable.adaptToToolkit(toolkit, true);
        toolkit.paintBordersFor(aliquotedSpecimenTable);
    }

    private void createSourceSpecimenSection() {
        @SuppressWarnings("nls")
        Section section = createSection(i18n.tr("Source specimen types"));

        sourceSpecimenTable =
            new SourceSpecimenInfoTable(section,
                study.getSourceSpecimenCollection(true));
        section.setClient(sourceSpecimenTable);
        sourceSpecimenTable.adaptToToolkit(toolkit, true);
        toolkit.paintBordersFor(sourceSpecimenTable);
    }

    @SuppressWarnings("nls")
    private void createStudyEventAttrSection() throws Exception {
        Composite client =
            createSectionWithClient(i18n
                .tr("Patient Visit Information Collected"));
        client.setLayout(new GridLayout(1, false));

        StudyEventAttrCustomInfo combinedStudyEventAttrInfo;

        combinedStudyEventAttrInfo = new StudyEventAttrCustomInfo();
        combinedStudyEventAttrInfo.setLabel(DATE_PROCESSED_INFO_FIELD_NAME);
        combinedStudyEventAttrInfo.setType(EventAttrTypeEnum.DATE_TIME);
        pvCustomInfoList.add(combinedStudyEventAttrInfo);

        for (String label : study.getStudyEventAttrLabels()) {
            if (!study.getStudyEventAttrActivityStatus(label).equals(
                ActivityStatus.ACTIVE)) {
                continue;
            }
            combinedStudyEventAttrInfo = new StudyEventAttrCustomInfo();
            combinedStudyEventAttrInfo.setLabel(label);
            combinedStudyEventAttrInfo.setType(study
                .getStudyEventAttrType(label));
            combinedStudyEventAttrInfo.setAllowedValues(study
                .getStudyEventAttrPermissible(label));
            pvCustomInfoList.add(combinedStudyEventAttrInfo);
        }

        if (pvCustomInfoList.size() == 0) {
            toolkit
                .createLabel(
                    client,
                    i18n.tr("Study does not collect additional patient visit information"));
            return;
        }

        Composite subcomp;
        for (StudyEventAttrCustomInfo pvCustomInfo : pvCustomInfoList) {
            subcomp = toolkit.createComposite(client);
            subcomp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

            if (pvCustomInfo.getAllowedValues() != null) {
                subcomp.setLayout(new GridLayout(2, false));

                pvCustomInfo.widget =
                    createReadOnlyLabelledField(subcomp, SWT.NONE,
                        pvCustomInfo.getLabel());
            } else {
                subcomp.setLayout(new GridLayout(1, false));
                toolkit.createLabel(subcomp, pvCustomInfo.getLabel());
            }
        }
    }

    @SuppressWarnings("nls")
    private void setStudyEventAttrValues() throws Exception {
        for (StudyEventAttrCustomInfo pvCustomInfo : pvCustomInfoList) {
            String label = pvCustomInfo.getLabel();
            if (label.equals(DATE_PROCESSED_INFO_FIELD_NAME)) {
                // skip this attribute since its already part of PatientVisit
                continue;
            }
            setTextValue(pvCustomInfo.widget, StringUtils.join(
                study.getStudyEventAttrPermissible(label), ";"));
        }
    }

    @SuppressWarnings("nls")
    @Override
    public void setValues() throws Exception {
        setPartName(i18n.tr("Study {0}", study.getNameShort()));
        form.setText(i18n.tr("Study {0}", study.getName()));
        setStudySectionValues();
        setStudyEventAttrValues();
        aliquotedSpecimenTable.setList(study
            .getAliquotedSpecimenCollection(true));
        sourceSpecimenTable
            .setList(study.getSourceSpecimenCollection(true));
        contactsTable.setCollectionByStudy(study);
        commentTable.setList(study.getCommentCollection(false));
    }
}
