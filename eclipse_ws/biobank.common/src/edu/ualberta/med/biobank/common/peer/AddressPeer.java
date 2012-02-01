package edu.ualberta.med.biobank.common.peer;

import edu.ualberta.med.biobank.common.util.TypeReference;
import java.util.Collections;
import edu.ualberta.med.biobank.common.wrappers.Property;
import java.util.List;
import java.util.ArrayList;
import edu.ualberta.med.biobank.model.Address;

public class AddressPeer {
	public static final Property<String, Address> STREET2 = Property.create(
		"street2" //$NON-NLS-1$
		, Address.class
		, new TypeReference<String>() {}
		, new Property.Accessor<String, Address>() { private static final long serialVersionUID = 1L;
			@Override
			public String get(Address model) {
				return model.getStreet2();
			}
			@Override
			public void set(Address model, String value) {
				model.setStreet2(value);
			}
		});

	public static final Property<Integer, Address> ID = Property.create(
		"id" //$NON-NLS-1$
		, Address.class
		, new TypeReference<Integer>() {}
		, new Property.Accessor<Integer, Address>() { private static final long serialVersionUID = 1L;
			@Override
			public Integer get(Address model) {
				return model.getId();
			}
			@Override
			public void set(Address model, Integer value) {
				model.setId(value);
			}
		});

	public static final Property<String, Address> STREET1 = Property.create(
		"street1" //$NON-NLS-1$
		, Address.class
		, new TypeReference<String>() {}
		, new Property.Accessor<String, Address>() { private static final long serialVersionUID = 1L;
			@Override
			public String get(Address model) {
				return model.getStreet1();
			}
			@Override
			public void set(Address model, String value) {
				model.setStreet1(value);
			}
		});

	public static final Property<String, Address> FAX_NUMBER = Property.create(
		"faxNumber" //$NON-NLS-1$
		, Address.class
		, new TypeReference<String>() {}
		, new Property.Accessor<String, Address>() { private static final long serialVersionUID = 1L;
			@Override
			public String get(Address model) {
				return model.getFaxNumber();
			}
			@Override
			public void set(Address model, String value) {
				model.setFaxNumber(value);
			}
		});

	public static final Property<String, Address> POSTAL_CODE = Property.create(
		"postalCode" //$NON-NLS-1$
		, Address.class
		, new TypeReference<String>() {}
		, new Property.Accessor<String, Address>() { private static final long serialVersionUID = 1L;
			@Override
			public String get(Address model) {
				return model.getPostalCode();
			}
			@Override
			public void set(Address model, String value) {
				model.setPostalCode(value);
			}
		});

	public static final Property<String, Address> PHONE_NUMBER = Property.create(
		"phoneNumber" //$NON-NLS-1$
		, Address.class
		, new TypeReference<String>() {}
		, new Property.Accessor<String, Address>() { private static final long serialVersionUID = 1L;
			@Override
			public String get(Address model) {
				return model.getPhoneNumber();
			}
			@Override
			public void set(Address model, String value) {
				model.setPhoneNumber(value);
			}
		});

	public static final Property<String, Address> PROVINCE = Property.create(
		"province" //$NON-NLS-1$
		, Address.class
		, new TypeReference<String>() {}
		, new Property.Accessor<String, Address>() { private static final long serialVersionUID = 1L;
			@Override
			public String get(Address model) {
				return model.getProvince();
			}
			@Override
			public void set(Address model, String value) {
				model.setProvince(value);
			}
		});

	public static final Property<String, Address> EMAIL_ADDRESS = Property.create(
		"emailAddress" //$NON-NLS-1$
		, Address.class
		, new TypeReference<String>() {}
		, new Property.Accessor<String, Address>() { private static final long serialVersionUID = 1L;
			@Override
			public String get(Address model) {
				return model.getEmailAddress();
			}
			@Override
			public void set(Address model, String value) {
				model.setEmailAddress(value);
			}
		});

	public static final Property<String, Address> COUNTRY = Property.create(
		"country" //$NON-NLS-1$
		, Address.class
		, new TypeReference<String>() {}
		, new Property.Accessor<String, Address>() { private static final long serialVersionUID = 1L;
			@Override
			public String get(Address model) {
				return model.getCountry();
			}
			@Override
			public void set(Address model, String value) {
				model.setCountry(value);
			}
		});

	public static final Property<String, Address> CITY = Property.create(
		"city" //$NON-NLS-1$
		, Address.class
		, new TypeReference<String>() {}
		, new Property.Accessor<String, Address>() { private static final long serialVersionUID = 1L;
			@Override
			public String get(Address model) {
				return model.getCity();
			}
			@Override
			public void set(Address model, String value) {
				model.setCity(value);
			}
		});

   public static final List<Property<?, ? super Address>> PROPERTIES;
   static {
      List<Property<?, ? super Address>> aList = new ArrayList<Property<?, ? super Address>>();
      aList.add(STREET2);
      aList.add(ID);
      aList.add(STREET1);
      aList.add(FAX_NUMBER);
      aList.add(POSTAL_CODE);
      aList.add(PHONE_NUMBER);
      aList.add(PROVINCE);
      aList.add(EMAIL_ADDRESS);
      aList.add(COUNTRY);
      aList.add(CITY);
      PROPERTIES = Collections.unmodifiableList(aList);
   };
}
