package edu.ualberta.med.biobank.mvp.presenter.impl;

public class SiteEditPresenter {
    // public abstract class SiteEditPresenter extends
    // BaseEntryPresenter<Display> {
    // protected final Integer siteId;
    // private final AddressEntryPresenter addressEditPresenter;
    // private final ActivityStatusComboPresenter selectActivityStatusPresenter;
    //
    // protected SiteEditPresenter(Integer siteId) {
    // this.siteId = siteId;
    // addressEditPresenter = new AddressEntryPresenter();
    // selectActivityStatusPresenter = new ActivityStatusComboPresenter();
    // }
    //
    // @Override
    // public void onBind() {
    // addressEditPresenter.bind();
    // selectActivityStatusPresenter.bind();
    //
    // // TODO: listen to Display properties for validation purposes.
    // registerHandler(display.getName().addValueChangeHandler(
    // new ValueChangeHandler<String>() {
    // @Override
    // public void onValueChange(ValueChangeEvent<String> event) {
    // // display.getName().set
    // }
    // }));
    // }
    //
    // @Override
    // protected void onUnbind() {
    // addressEditPresenter.unbind();
    // selectActivityStatusPresenter.unbind();
    // }
    //
    // @Override
    // public void doPopulate() {
    // SiteInfo siteInfo = getSiteInfo();
    //
    // display.getName().setValue(siteInfo.site.getName());
    // display.getNameShort().setValue(siteInfo.site.getNameShort());
    // display.getComment().setValue(siteInfo.site.getComment());
    // display.getActivityStatus().setValue(siteInfo.site.getActivityStatus());
    // display.getStudies().setValue(siteInfo.studies);
    //
    // addressEditPresenter.editAddress(siteInfo.site.getAddress());
    // }
    //
    // @Override
    // public void doSave() {
    // SaveSiteAction saveSite = new SaveSiteAction(siteId);
    //
    // saveSite.setName(display.getName().getValue());
    // saveSite.setNameShort(display.getNameShort().getValue());
    // saveSite.setComment(display.getComment().getValue());
    //
    // Integer aStatusId = display.getActivityStatus().getValue().getId();
    // saveSite.setActivityStatusId(aStatusId);
    //
    // saveSite.setAddress(addressEditPresenter.getAddress());
    // // updateSite.setStudyIds(display.getStudyIds().getValue());
    //
    // dispatcher.exec(saveSite, new ActionCallback<Integer>() {
    // @Override
    // public void onFailure(Throwable caught) {
    // // on failure:
    // // log exception
    // // have a listener to a DisplayExceptionEvent:
    // // e.g. eventBus.fireEvent(new ExceptionEvent(???));
    // }
    //
    // @Override
    // public void onSuccess(Integer siteId) {
    // // on success:
    //
    // // TODO: close this view
    // // TODO: listen for SiteSavedEvent to (1) FormManager open view
    // // form (2) TreeManager(s) update any trees that have this site.
    // // But wait, probably shouldn't open the view form on any site
    // // save event ... :-(
    //
    // eventBus.fireEvent(new SiteChangeEvent(siteId));
    // }
    // });
    // }
    //
    // public interface Display extends CloseableView, ReloadableView,
    // SaveableView, ActivityStatusComboPresenter.Display,
    // AddressEntryPresenter.View {
    // // TODO: have general validation errors
    // void setGeneralErrors(Collection<Object> errors);
    //
    // HasValue<String> getName();
    //
    // HasValue<String> getNameShort();
    //
    // HasValue<String> getComment();
    //
    // HasValue<Collection<StudyInfo>> getStudies();
    // }
    //
    // protected abstract SiteInfo getSiteInfo();
    //
    // public static class Update extends SiteEditPresenter {
    // public Update(Site site) {
    // super(site.getId());
    // }
    //
    // @Override
    // protected SiteInfo getSiteInfo() {
    // final Holder<SiteInfo> siteInfoHolder = new Holder<SiteInfo>(null);
    // GetSiteInfoAction getSiteInfo = new GetSiteInfoAction(siteId);
    // dispatcher.exec(getSiteInfo, new ActionCallback<SiteInfo>() {
    // @Override
    // public void onFailure(Throwable caught) {
    // // TODO: better error message and show or log exception?
    // eventBus.fireEvent(new AlertEvent("FAIL!"));
    // display.close();
    // unbind();
    // }
    //
    // @Override
    // public void onSuccess(SiteInfo result) {
    // siteInfoHolder.setValue(result);
    // }
    // });
    // return siteInfoHolder.getValue();
    // }
    // }
    //
    // public static class Create extends SiteEditPresenter {
    // public Create() {
    // super(null);
    // }
    //
    // @Override
    // protected SiteInfo getSiteInfo() {
    // SiteInfo siteInfo = new SiteInfo();
    // siteInfo.site = new Site();
    // siteInfo.site.setAddress(new Address());
    // siteInfo.studies = new ArrayList<StudyInfo>();
    // return null;
    // }
    // }
}
