package edu.ualberta.med.biobank.view;

import java.util.Collection;

import edu.ualberta.med.biobank.common.action.site.GetSiteStudyInfoAction.StudyInfo;
import edu.ualberta.med.biobank.event.HasClickHandlers;
import edu.ualberta.med.biobank.event.HasValue;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.presenter.AddressEditPresenter.Display;
import edu.ualberta.med.biobank.presenter.SiteEditPresenter;

public class SiteEntryForm implements SiteEditPresenter.Display {
    @Override
    public HasClickHandlers getSaveButton() {
        // TODO Auto-generated method stub
        return null;
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
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public HasValue<String> getNameShort() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public HasValue<String> getComment() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public HasValue<ActivityStatus> getActivityStatus() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public HasValue<Collection<StudyInfo>> getStudies() {
        // TODO Auto-generated method stub
        return null;
    }

}
