package edu.ualberta.med.biobank.common.peer;

import edu.ualberta.med.biobank.common.util.TypeReference;
import java.util.Collections;
import edu.ualberta.med.biobank.common.wrappers.Property;
import java.util.List;
import java.util.ArrayList;
import edu.ualberta.med.biobank.model.Contact;
import java.util.Collection;
import edu.ualberta.med.biobank.model.Clinic;

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

	public static final Property<Collection<Contact>, Clinic> CONTACT_COLLECTION = Property.create(
		"contactCollection" //$NON-NLS-1$
		, Clinic.class
		, new TypeReference<Collection<Contact>>() {}
		, new Property.Accessor<Collection<Contact>, Clinic>() { private static final long serialVersionUID = 1L;
			@Override
			public Collection<Contact> get(Clinic model) {
				return model.getContactCollection();
			}
			@Override
			public void set(Clinic model, Collection<Contact> value) {
				model.getContactCollection().clear();
				model.getContactCollection().addAll(value);
			}
		});

   public static final List<Property<?, ? super Clinic>> PROPERTIES;
   static {
      List<Property<?, ? super Clinic>> aList = new ArrayList<Property<?, ? super Clinic>>();
      aList.add(SENDS_SHIPMENTS);
      aList.add(CONTACT_COLLECTION);
      PROPERTIES = Collections.unmodifiableList(aList);
   };
}
