package edu.ualberta.med.biobank.presenter;

import java.awt.TextField;
import java.util.Collection;

import edu.ualberta.med.biobank.common.action.site.GetSiteInfoAction;
import edu.ualberta.med.biobank.common.action.site.GetSiteInfoAction.SiteInfo;
import edu.ualberta.med.biobank.common.action.site.GetSiteStudyInfoAction.StudyInfo;
import edu.ualberta.med.biobank.common.action.site.UpdateSiteAction;
import edu.ualberta.med.biobank.event.ClickEvent;
import edu.ualberta.med.biobank.event.ClickHandler;
import edu.ualberta.med.biobank.event.HandlerRegistration;
import edu.ualberta.med.biobank.event.HasClickHandlers;
import edu.ualberta.med.biobank.event.HasValue;
import edu.ualberta.med.biobank.event.ValueChangeEvent;
import edu.ualberta.med.biobank.event.ValueChangeHandler;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.server.applicationservice.BiobankApplicationService;

public class SiteEditPresenter {
	private final Display display;
	private final AddressEditPresenter addressEditPresenter;
	private final Integer siteId;
	private BiobankApplicationService service; // TODO: get from somewhere.
	
	public SiteEditPresenter(Display display, Integer siteId) {
		this.display = display;
		this.siteId = siteId;
		this.addressEditPresenter = new AddressEditPresenter(display.getAddressEditDisplay());
		
		bindDisplay();
		init();
	}
	
	private void bindDisplay() {
		display.getSaveButton().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				doSave();
			}});
		
		// TODO: listen to Display properties for validation purposes.
	}
	
	private void init() {
		GetSiteInfoAction action = new GetSiteInfoAction(siteId);
		SiteInfo siteInfo = service.doAction(action);
		
		// TODO: seems error-prone :-(
		display.getName().setValue(siteInfo.site.getName());
		display.getNameShort().setValue(siteInfo.site.getNameShort());
		display.getComment().setValue(siteInfo.site.getComment());
		display.getActivityStatus().setValue(siteInfo.site.getActivityStatus());
		display.getStudies().setValue(siteInfo.studies);
		
		addressEditPresenter.editAddress(siteInfo.site.getAddress());
	}
	
	public void doSave() {
		UpdateSiteAction updateSite = new UpdateSiteAction(siteId);
		updateSite.setName(display.getName().getValue());
		updateSite.setNameShort(display.getNameShort().getValue());
		updateSite.setComment(display.getComment().getValue());
//		updateSite.setAddress(display.getAddressEditDisplay().g)
//		updateSite.setStudyIds(display.getStudyIds().getValue());
	}
	
	public interface Display {
		public HasClickHandlers getSaveButton();
		public HasClickHandlers getReloadButton();
		public HasClickHandlers getCloseButton();
		
		public void setGeneralErrors(Collection<Object> errors); // ??
		
		public AddressEditPresenter.Display getAddressEditDisplay();
		
		public HasValue<String> getName();
		public HasValue<String> getNameShort();
		public HasValue<String> getComment();
		public HasValue<ActivityStatus> getActivityStatus();
		public HasValue<Collection<StudyInfo>> getStudies();
	}
}
