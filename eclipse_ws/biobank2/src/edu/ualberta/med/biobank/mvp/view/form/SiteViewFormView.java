package edu.ualberta.med.biobank.mvp.view.form;

import java.util.Collection;


import edu.ualberta.med.biobank.common.action.info.ContainerTypeInfo;
import edu.ualberta.med.biobank.common.action.info.StudyInfo;
import edu.ualberta.med.biobank.forms.Messages;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.Address;
import edu.ualberta.med.biobank.model.Comment;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.mvp.presenter.impl.SiteViewPresenter;
import edu.ualberta.med.biobank.mvp.user.ui.HasField;
import edu.ualberta.med.biobank.mvp.view.item.LongTextItem;
import edu.ualberta.med.biobank.mvp.view.item.TextItem;
import edu.ualberta.med.biobank.mvp.view.item.TranslatedItem.Translator;
import edu.ualberta.med.biobank.mvp.view.item.TranslatedTextItem;
import edu.ualberta.med.biobank.mvp.view.util.InputTable;

public class SiteViewFormView extends AbstractViewFormView
    implements SiteViewPresenter.View {
    private static final Translator<ActivityStatus, String> ACTIVITY_STATUS_TRANSLATOR =
        new Translator<ActivityStatus, String>() {
            @Override
            public ActivityStatus fromDelegate(String delegateValue) {
                return null;
            }

            @Override
            public String toDelegate(ActivityStatus value) {
                return value.getName();
            }
        };

    private final TextItem name = new TextItem();
    private final TextItem nameShort = new TextItem();
    private final LongTextItem studyCount = new LongTextItem();
    private final LongTextItem containerTypeCount = new LongTextItem();
    private final LongTextItem topContainerCount = new LongTextItem();
    private final LongTextItem patientCount = new LongTextItem();
    private final LongTextItem collectionEventCount = new LongTextItem();
    private final LongTextItem aliquotedSpecimenCount = new LongTextItem();
    private final TranslatedTextItem<ActivityStatus> activityStatus =
        new TranslatedTextItem<ActivityStatus>(ACTIVITY_STATUS_TRANSLATOR);

    @Override
    protected void onCreate(BaseForm baseForm) {
        super.onCreate(baseForm);

        baseForm.setTitle("TODO: show right title");

        InputTable table = new InputTable(baseForm.getPage());

        table.addLabel(Messages.label_name);
        name.setText(table.addReadOnlyText());

        table.addLabel(Messages.label_nameShort);
        nameShort.setText(table.addReadOnlyText());

        table.addLabel(Messages.SiteViewForm_field_studyCount_label);
        studyCount.setText(table.addReadOnlyText());

        table.addLabel(Messages.site_field_type_label);
        containerTypeCount.setText(table.addReadOnlyText());

        table.addLabel(Messages.SiteViewForm_field_topLevelCount_label);
        topContainerCount.setText(table.addReadOnlyText());

        table.addLabel(Messages.SiteViewForm_field_patientCount_label);
        patientCount.setText(table.addReadOnlyText());

        table.addLabel(Messages.SiteViewForm_field_pvCount_label);
        collectionEventCount.setText(table.addReadOnlyText());

        table.addLabel(Messages.SiteViewForm_field_totalSpecimen);
        aliquotedSpecimenCount.setText(table.addReadOnlyText());

        table.addLabel(Messages.label_activity);
        activityStatus.setText(table.addReadOnlyText());
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
    public HasField<Long> getStudyCount() {
        return studyCount;
    }

    @Override
    public HasField<Long> getContainerTypeCount() {
        return containerTypeCount;
    }

    @Override
    public HasField<Long> getTopContainerCount() {
        return topContainerCount;
    }

    @Override
    public HasField<Long> getPatientCount() {
        return patientCount;
    }

    @Override
    public HasField<Long> getCollectionEventCount() {
        return collectionEventCount;
    }

    @Override
    public HasField<Long> getAliquotedSpecimenCount() {
        return aliquotedSpecimenCount;
    }

    @Override
    public HasField<ActivityStatus> getActivityStatus() {
        return activityStatus;
    }

    @Override
    public HasField<Address> getAddress() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public HasField<Collection<Comment>> getCommentCollection() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public HasField<Collection<StudyInfo>> getStudyCollection() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public HasField<Collection<ContainerTypeInfo>> getContainerTypeCollection() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public HasField<Collection<Container>> getTopContainerCollection() {
        // TODO Auto-generated method stub
        return null;
    }

}
