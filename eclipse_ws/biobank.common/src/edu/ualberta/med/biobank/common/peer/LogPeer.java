package edu.ualberta.med.biobank.common.peer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import edu.ualberta.med.biobank.common.util.TypeReference;
import edu.ualberta.med.biobank.common.wrappers.Property;
import edu.ualberta.med.biobank.model.Log;

public class LogPeer {
	public static final Property<String, Log> CENTER = Property.create(
		"center" //$NON-NLS-1$
		, Log.class
		, new TypeReference<String>() {}
		, new Property.Accessor<String, Log>() { private static final long serialVersionUID = 1L;
			@Override
			public String get(Log model) {
				return model.getCenter();
			}
			@Override
			public void set(Log model, String value) {
				model.setCenter(value);
			}
		});

	public static final Property<Integer, Log> ID = Property.create(
		"id" //$NON-NLS-1$
		, Log.class
		, new TypeReference<Integer>() {}
		, new Property.Accessor<Integer, Log>() { private static final long serialVersionUID = 1L;
			@Override
			public Integer get(Log model) {
				return model.getId();
			}
			@Override
			public void set(Log model, Integer value) {
				model.setId(value);
			}
		});

	public static final Property<String, Log> USERNAME = Property.create(
		"username" //$NON-NLS-1$
		, Log.class
		, new TypeReference<String>() {}
		, new Property.Accessor<String, Log>() { private static final long serialVersionUID = 1L;
			@Override
			public String get(Log model) {
				return model.getUsername();
			}
			@Override
			public void set(Log model, String value) {
				model.setUsername(value);
			}
		});

	public static final Property<String, Log> DETAILS = Property.create(
		"details" //$NON-NLS-1$
		, Log.class
		, new TypeReference<String>() {}
		, new Property.Accessor<String, Log>() { private static final long serialVersionUID = 1L;
			@Override
			public String get(Log model) {
				return model.getDetails();
			}
			@Override
			public void set(Log model, String value) {
				model.setDetails(value);
			}
		});

	public static final Property<Date, Log> CREATED_AT = Property.create(
		"createdAt" //$NON-NLS-1$
		, Log.class
		, new TypeReference<Date>() {}
		, new Property.Accessor<Date, Log>() { private static final long serialVersionUID = 1L;
			@Override
			public Date get(Log model) {
				return model.getCreatedAt();
			}
			@Override
			public void set(Log model, Date value) {
				model.setCreatedAt(value);
			}
		});

	public static final Property<String, Log> LOCATION_LABEL = Property.create(
		"locationLabel" //$NON-NLS-1$
		, Log.class
		, new TypeReference<String>() {}
		, new Property.Accessor<String, Log>() { private static final long serialVersionUID = 1L;
			@Override
			public String get(Log model) {
				return model.getLocationLabel();
			}
			@Override
			public void set(Log model, String value) {
				model.setLocationLabel(value);
			}
		});

	public static final Property<String, Log> PATIENT_NUMBER = Property.create(
		"patientNumber" //$NON-NLS-1$
		, Log.class
		, new TypeReference<String>() {}
		, new Property.Accessor<String, Log>() { private static final long serialVersionUID = 1L;
			@Override
			public String get(Log model) {
				return model.getPatientNumber();
			}
			@Override
			public void set(Log model, String value) {
				model.setPatientNumber(value);
			}
		});

	public static final Property<String, Log> ACTION = Property.create(
		"action" //$NON-NLS-1$
		, Log.class
		, new TypeReference<String>() {}
		, new Property.Accessor<String, Log>() { private static final long serialVersionUID = 1L;
			@Override
			public String get(Log model) {
				return model.getAction();
			}
			@Override
			public void set(Log model, String value) {
				model.setAction(value);
			}
		});

	public static final Property<String, Log> INVENTORY_ID = Property.create(
		"inventoryId" //$NON-NLS-1$
		, Log.class
		, new TypeReference<String>() {}
		, new Property.Accessor<String, Log>() { private static final long serialVersionUID = 1L;
			@Override
			public String get(Log model) {
				return model.getInventoryId();
			}
			@Override
			public void set(Log model, String value) {
				model.setInventoryId(value);
			}
		});

	public static final Property<String, Log> TYPE = Property.create(
		"type" //$NON-NLS-1$
		, Log.class
		, new TypeReference<String>() {}
		, new Property.Accessor<String, Log>() { private static final long serialVersionUID = 1L;
			@Override
			public String get(Log model) {
				return model.getType();
			}
			@Override
			public void set(Log model, String value) {
				model.setType(value);
			}
		});

   public static final List<Property<?, ? super Log>> PROPERTIES;
   static {
      List<Property<?, ? super Log>> aList = new ArrayList<Property<?, ? super Log>>();
      aList.add(CENTER);
      aList.add(ID);
      aList.add(USERNAME);
      aList.add(DETAILS);
      aList.add(CREATED_AT);
      aList.add(LOCATION_LABEL);
      aList.add(PATIENT_NUMBER);
      aList.add(ACTION);
      aList.add(INVENTORY_ID);
      aList.add(TYPE);
      PROPERTIES = Collections.unmodifiableList(aList);
   };
}
