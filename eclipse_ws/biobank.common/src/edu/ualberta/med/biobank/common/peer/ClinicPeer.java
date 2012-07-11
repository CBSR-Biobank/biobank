package edu.ualberta.med.biobank.common.peer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import edu.ualberta.med.biobank.common.util.TypeReference;
import edu.ualberta.med.biobank.common.wrappers.Property;
import edu.ualberta.med.biobank.model.Clinic;
import edu.ualberta.med.biobank.model.Contact;

public class ClinicPeer  extends CenterPeer {
	public static final Property<Boolean, Clinic> SENDS_SHIPMENTS = Property.create(
		"sendsShipments" //$NON-NLS-1$
		, Clinic.class
		, new TypeReference<Boolean>() {}
		, new Property.Accessor<Boolean, Clinic>() { private static final long serialVersionUID = 1L;
			@Override
			public Boolean get(Clinic model) {
				return model.getSendsShipments();
			}
			@Override
			public void set(Clinic model, Boolean value) {
				model.setSendsShipments(value);
			}
		});

	public static final Property<Collection<Contact>, Clinic> CONTACTS = Property.create(
		"contacts" //$NON-NLS-1$
		, Clinic.class
		, new TypeReference<Collection<Contact>>() {}
		, new Property.Accessor<Collection<Contact>, Clinic>() { private static final long serialVersionUID = 1L;
			@Override
			public Collection<Contact> get(Clinic model) {
				return model.getContacts();
			}
			@Override
			public void set(Clinic model, Collection<Contact> value) {
				model.getContacts().clear();
				model.getContacts().addAll(value);
			}
		});

   public static final List<Property<?, ? super Clinic>> PROPERTIES;
   static {
      List<Property<?, ? super Clinic>> aList = new ArrayList<Property<?, ? super Clinic>>();
      aList.add(SENDS_SHIPMENTS);
      aList.add(CONTACTS);
      PROPERTIES = Collections.unmodifiableList(aList);
   };
}
