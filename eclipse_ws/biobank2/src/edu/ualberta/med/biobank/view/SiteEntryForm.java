package edu.ualberta.med.biobank.view;

import java.util.Collection;

import edu.ualberta.med.biobank.common.action.site.GetSiteStudyInfoAction.StudyInfo;
import edu.ualberta.med.biobank.event.HasClickHandlers;
import edu.ualberta.med.biobank.event.HasValue;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.presenter.AddressEditPresenter.Display;
import edu.ualberta.med.biobank.presenter.SiteEditPresenter;
import edu.ualberta.med.biobank.view.item.ButtonItem;
import edu.ualberta.med.biobank.view.item.ComboItem;
import edu.ualberta.med.biobank.view.item.TextItem;

public class SiteEntryForm implements SiteEditPresenter.Display {
    private ButtonItem saveButton;
    private TextItem name;
    private TextItem nameShort;
    private TextItem comment;
    private ComboItem<ActivityStatus> activityStatus;

    @Override
    public HasClickHandlers getSaveButton() {
        return saveButton;
    }

    @Override
    public HasClickHandlers getReloadButton() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public HasClickHandlers getCloseButton() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setGeneralErrors(Collection<Object> errors) {
        // TODO Auto-generated method stub

    }

    @Override
    public Display getAddressEditDisplay() {
        // TODO Auto-generated method stub
        return null;
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
    public HasValue<String> getComment() {
        return comment;
    }

    @Override
    public HasValue<ActivityStatus> getActivityStatus() {
        return activityStatus;
    }

    @Override
    public HasValue<Collection<StudyInfo>> getStudies() {
        return null;
    }

}
