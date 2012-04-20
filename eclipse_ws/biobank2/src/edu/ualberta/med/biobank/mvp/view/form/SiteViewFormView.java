package edu.ualberta.med.biobank.mvp.view.form;

import java.util.Collection;

import edu.ualberta.med.biobank.common.action.info.SiteContainerTypeInfo;
import edu.ualberta.med.biobank.common.action.info.StudyCountInfo;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.Address;
import edu.ualberta.med.biobank.model.Comment;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.mvp.presenter.impl.SiteViewPresenter;
import edu.ualberta.med.biobank.mvp.user.ui.ValueField;
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

    @SuppressWarnings("nls")
    @Override
    protected void onCreate(BaseForm baseForm) {
        super.onCreate(baseForm);

        baseForm.setTitle("TODO: show right title");

        InputTable table = new InputTable(baseForm.getPage());

        table.addLabel("Name");
        name.setText(table.addReadOnlyText());

        table.addLabel("Name Short");
        nameShort.setText(table.addReadOnlyText());

        table.addLabel("Total studies");
        studyCount.setText(table.addReadOnlyText());

        table.addLabel("Container types");
        containerTypeCount.setText(table.addReadOnlyText());

        table.addLabel("Top level containers");
        topContainerCount.setText(table.addReadOnlyText());

        table.addLabel("Total patients");
        patientCount.setText(table.addReadOnlyText());

        table.addLabel("Total collection events");
        collectionEventCount.setText(table.addReadOnlyText());

        table.addLabel("Total specimens");
        aliquotedSpecimenCount.setText(table.addReadOnlyText());

        table.addLabel("Activity status");
        activityStatus.setText(table.addReadOnlyText());
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
    public ValueField<Long> getStudyCount() {
        return studyCount;
    }

    @Override
    public ValueField<Long> getContainerTypeCount() {
        return containerTypeCount;
    }

    @Override
    public ValueField<Long> getTopContainerCount() {
        return topContainerCount;
    }

    @Override
    public ValueField<Long> getPatientCount() {
        return patientCount;
    }

    @Override
    public ValueField<Long> getCollectionEventCount() {
        return collectionEventCount;
    }

    @Override
    public ValueField<Long> getAliquotedSpecimenCount() {
        return aliquotedSpecimenCount;
    }

    @Override
    public ValueField<ActivityStatus> getActivityStatus() {
        return activityStatus;
    }

    @Override
    public ValueField<Address> getAddress() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ValueField<Collection<Comment>> getCommentCollection() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ValueField<Collection<StudyCountInfo>> getStudyCollection() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ValueField<Collection<SiteContainerTypeInfo>> getContainerTypeCollection() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ValueField<Collection<Container>> getTopContainerCollection() {
        // TODO Auto-generated method stub
        return null;
    }

}
