package edu.ualberta.med.biobank.common.peer;

import edu.ualberta.med.biobank.common.util.TypeReference;
import java.util.Collections;
import edu.ualberta.med.biobank.common.wrappers.Property;
import java.util.List;
import java.util.ArrayList;
import edu.ualberta.med.biobank.model.ShippingMethod;

public class ShippingMethodPeer {
	public static final Property<Integer, ShippingMethod> ID = Property.create(
		"id" //$NON-NLS-1$
		, ShippingMethod.class
		, new TypeReference<Integer>() {}
		, new Property.Accessor<Integer, ShippingMethod>() { private static final long serialVersionUID = 1L;
			@Override
			public Integer get(ShippingMethod model) {
				return model.getId();
			}
			@Override
			public void set(ShippingMethod model, Integer value) {
				model.setId(value);
			}
		});

	public static final Property<String, ShippingMethod> NAME = Property.create(
		"name" //$NON-NLS-1$
		, ShippingMethod.class
		, new TypeReference<String>() {}
		, new Property.Accessor<String, ShippingMethod>() { private static final long serialVersionUID = 1L;
			@Override
			public String get(ShippingMethod model) {
				return model.getName();
			}
			@Override
			public void set(ShippingMethod model, String value) {
				model.setName(value);
			}
		});

   public static final List<Property<?, ? super ShippingMethod>> PROPERTIES;
   static {
      List<Property<?, ? super ShippingMethod>> aList = new ArrayList<Property<?, ? super ShippingMethod>>();
      aList.add(ID);
      aList.add(NAME);
      PROPERTIES = Collections.unmodifiableList(aList);
   };
}
