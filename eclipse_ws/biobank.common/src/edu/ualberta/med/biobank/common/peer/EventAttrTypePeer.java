package edu.ualberta.med.biobank.common.peer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.ualberta.med.biobank.common.util.TypeReference;
import edu.ualberta.med.biobank.common.wrappers.Property;
import edu.ualberta.med.biobank.model.EventAttrType;

public class EventAttrTypePeer {
	public static final Property<Integer, EventAttrType> ID = Property.create(
		"id" //$NON-NLS-1$
		, EventAttrType.class
		, new TypeReference<Integer>() {}
		, new Property.Accessor<Integer, EventAttrType>() { private static final long serialVersionUID = 1L;
			@Override
			public Integer get(EventAttrType model) {
				return model.getId();
			}
			@Override
			public void set(EventAttrType model, Integer value) {
				model.setId(value);
			}
		});

	public static final Property<String, EventAttrType> NAME = Property.create(
		"name" //$NON-NLS-1$
		, EventAttrType.class
		, new TypeReference<String>() {}
		, new Property.Accessor<String, EventAttrType>() { private static final long serialVersionUID = 1L;
			@Override
			public String get(EventAttrType model) {
				return model.getName();
			}
			@Override
			public void set(EventAttrType model, String value) {
				model.setName(value);
			}
		});

   public static final List<Property<?, ? super EventAttrType>> PROPERTIES;
   static {
      List<Property<?, ? super EventAttrType>> aList = new ArrayList<Property<?, ? super EventAttrType>>();
      aList.add(ID);
      aList.add(NAME);
      PROPERTIES = Collections.unmodifiableList(aList);
   };
}
