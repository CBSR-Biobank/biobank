package edu.ualberta.med.biobank.presenter;

import java.awt.TextField;
import java.util.Collection;

import edu.ualberta.med.biobank.common.action.site.GetSiteInfoAction;
import edu.ualberta.med.biobank.common.action.site.GetSiteInfoAction.SiteInfo;
import edu.ualberta.med.biobank.common.action.site.UpdateSiteAction;
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
	}
	
	private void init() {
		GetSiteInfoAction action = new GetSiteInfoAction((Integer) null);
		
		service.doAction(action);
		
		SiteInfo siteInfo = null;
		
		display.getName().setValue(siteInfo.site.getName());
//		siteInfo.site.get
		
		addressEditPresenter.setAddress(siteInfo.site.getAddress());
	}
	
	public void doSave() {		
		UpdateSiteAction updateSite = new UpdateSiteAction(siteId);
		updateSite.setName(display.getName().getValue());
		updateSite.setNameShort(display.getNameShort().getValue());
		updateSite.setComment(display.getComment().getValue());
//		updateSite.setAddress(display.getAddressEditDisplay().g)
		updateSite.setStudyIds(display.getStudyIds().getValue());
	}
	
	public interface Display {
		public HasClickHandlers getSaveButton();
		public HasClickHandlers getReloadButton();
		public HasClickHandlers getCloseButton();
		
		public AddressEditPresenter.Display getAddressEditDisplay();
		
		public HasValue<String> getName();
		public HasValue<String> getNameShort();
		public HasValue<String> getActivityStatus();
		public HasValue<String> getComment();
		public HasValue<Collection<Integer>> getStudyIds();
	}
	
	public interface HasClickHandlers {
		public void addClickHandler(ClickHandler clickHandler);
	}
	
	public interface ClickHandler {
		public void onClick(ClickEvent event);
	}
	
	public interface ClickEvent {
		
	}
	
	public interface HasValue<T> {
		public T getValue();
		
		public void setValue(T value);
	}
	
	public interface HasChangeHandler<T> {
	}

	public interface ValidatableValue<T> extends HasChangeHandler<T>, HasValue<T> {
	}
	
	public class TextWrapper implements HasValue<String> {
		private final TextField text;
		
		public TextWrapper(TextField text) {
			this.text = text;
		}
		
		@Override
		public String getValue() {
			return text.getText();
		}

		@Override
		public void setValue(String value) {
			text.setText(value);
		}
		
	}
	
	// 1. listen to HasValue changes, validate as necessary
	// 2. 
}
