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
import edu.ualberta.med.biobank.mvp.user.ui.HasValueField;
import edu.ualberta.med.biobank.mvp.view.item.Adapter;
import edu.ualberta.med.biobank.mvp.view.item.LongBox;
import edu.ualberta.med.biobank.mvp.view.item.TextBox;
import edu.ualberta.med.biobank.mvp.view.item.TranslatedTextBox;
import edu.ualberta.med.biobank.mvp.view.util.InputTable;

public class SiteViewFormView extends AbstractViewFormView
    implements SiteViewPresenter.View {
    private static final Adapter<ActivityStatus, String> ACTIVITY_STATUS_ADAPTER =
        new Adapter<ActivityStatus, String>() {
            @Override
            public ActivityStatus adapt(String delegateValue) {
                return null;
            }

            @Override
            public String unadapt(ActivityStatus value) {
                return value.getName();
            }
        };

    private final TextBox name = new TextBox();
    private final TextBox nameShort = new TextBox();
    private final LongBox studyCount = new LongBox();
    private final LongBox containerTypeCount = new LongBox();
    private final LongBox topContainerCount = new LongBox();
    private final LongBox patientCount = new LongBox();
    private final LongBox collectionEventCount = new LongBox();
    private final LongBox aliquotedSpecimenCount = new LongBox();
    private final TranslatedTextBox<ActivityStatus> activityStatus =
        new TranslatedTextBox<ActivityStatus>(ACTIVITY_STATUS_ADAPTER);

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
    public HasValueField<String> getName() {
        return name;
    }

    @Override
    public HasValueField<String> getNameShort() {
        return nameShort;
    }

    @Override
    public HasValueField<Long> getStudyCount() {
        return studyCount;
    }

    @Override
    public HasValueField<Long> getContainerTypeCount() {
        return containerTypeCount;
    }

    @Override
    public HasValueField<Long> getTopContainerCount() {
        return topContainerCount;
    }

    @Override
    public HasValueField<Long> getPatientCount() {
        return patientCount;
    }

    @Override
    public HasValueField<Long> getCollectionEventCount() {
        return collectionEventCount;
    }

    @Override
    public HasValueField<Long> getAliquotedSpecimenCount() {
        return aliquotedSpecimenCount;
    }

    @Override
    public HasValueField<ActivityStatus> getActivityStatus() {
        return activityStatus;
    }

    @Override
    public HasValueField<Address> getAddress() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public HasValueField<Collection<Comment>> getCommentCollection() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public HasValueField<Collection<StudyInfo>> getStudyCollection() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public HasValueField<Collection<ContainerTypeInfo>> getContainerTypeCollection() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public HasValueField<Collection<Container>> getTopContainerCollection() {
        // TODO Auto-generated method stub
        return null;
    }

}
