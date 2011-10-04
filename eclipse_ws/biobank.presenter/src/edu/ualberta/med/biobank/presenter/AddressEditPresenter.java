package edu.ualberta.med.biobank.presenter;


import edu.ualberta.med.biobank.event.HasValue;
import edu.ualberta.med.biobank.model.Address;

public class AddressEditPresenter {
	private final Display display;
	private Address address;
	
	public AddressEditPresenter(Display display) {
		this.display = display;
	}
	
	public void editAddress(Address address) {
		this.address = address;
		
		display.getStreet1().setValue(address.getStreet1());
		display.getStreet2().setValue(address.getStreet2());
		display.getCity().setValue(address.getCity());
		display.getProvince().setValue(address.getProvince());
		display.getPostalCode().setValue(address.getPostalCode());
		display.getPhoneNumber().setValue(address.getPhoneNumber());
		display.getFaxNumber().setValue(address.getFaxNumber());
		display.getCountry().setValue(address.getCountry());
	}
	
	public interface Display {
		public HasValue<String> getStreet1();
		public HasValue<String> getStreet2();
		public HasValue<String> getCity();
		public HasValue<String> getProvince();
		public HasValue<String> getPostalCode();
		public HasValue<String> getPhoneNumber();
		public HasValue<String> getFaxNumber();
		public HasValue<String> getCountry();
	}
}
