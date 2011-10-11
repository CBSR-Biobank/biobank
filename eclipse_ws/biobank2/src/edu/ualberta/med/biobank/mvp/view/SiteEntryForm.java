package edu.ualberta.med.biobank.mvp.view;

import java.util.Collection;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.HasValue;

import edu.ualberta.med.biobank.common.action.site.GetSiteStudyInfoAction.StudyInfo;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.mvp.presenter.impl.SiteEditPresenter;
import edu.ualberta.med.biobank.mvp.user.ui.HasSelectedValue;
import edu.ualberta.med.biobank.mvp.view.item.ButtonItem;
import edu.ualberta.med.biobank.mvp.view.item.ComboItem;
import edu.ualberta.med.biobank.mvp.view.item.TextItem;

public class SiteEntryForm implements SiteEditPresenter.Display {
    private ButtonItem save;
    private ButtonItem reload;
    private ButtonItem close;
    private TextItem name;
    private TextItem nameShort;
    private TextItem comment;
    private TextItem street1;
    private TextItem street2;
    private TextItem city;
    private TextItem province;
    private TextItem postalCode;
    private TextItem phoneNumber;
    private TextItem faxNumber;
    private TextItem country;
    private ComboItem<ActivityStatus> activityStatus;

    @Override
    public HasClickHandlers getSave() {
        return save;
    }

    @Override
    public void setGeneralErrors(Collection<Object> errors) {
        // TODO Auto-generated method stub
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
    public HasSelectedValue<ActivityStatus> getActivityStatus() {
        return activityStatus;
    }

    @Override
    public HasValue<Collection<StudyInfo>> getStudies() {
        return null;
    }

    @Override
    public HasValue<String> getStreet1() {
        return street1;
    }

    @Override
    public HasValue<String> getStreet2() {
        return street2;
    }

    @Override
    public HasValue<String> getCity() {
        return city;
    }

    @Override
    public HasValue<String> getProvince() {
        return province;
    }

    @Override
    public HasValue<String> getPostalCode() {
        return postalCode;
    }

    @Override
    public HasValue<String> getPhoneNumber() {
        return phoneNumber;
    }

    @Override
    public HasValue<String> getFaxNumber() {
        return faxNumber;
    }

    @Override
    public HasValue<String> getCountry() {
        return country;
    }

    @Override
    public void close() {
        // TODO: something?
    }

    @Override
    public HasClickHandlers getClose() {
        return close;
    }

    @Override
    public HasClickHandlers getReload() {
        return reload;
    }

}
