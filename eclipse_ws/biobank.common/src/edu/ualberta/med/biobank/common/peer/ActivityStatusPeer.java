package edu.ualberta.med.biobank.common.peer;

import edu.ualberta.med.biobank.common.util.TypeReference;
import java.util.Collections;
import edu.ualberta.med.biobank.common.wrappers.Property;
import java.util.List;
import java.util.ArrayList;
import edu.ualberta.med.biobank.model.ActivityStatus;

public class ActivityStatusPeer {
	public static final Property<Integer, ActivityStatus> ID = Property.create(
		"id" //$NON-NLS-1$
		, ActivityStatus.class
		, new TypeReference<Integer>() {}
		, new Property.Accessor<Integer, ActivityStatus>() { private static final long serialVersionUID = 1L;
			@Override
			public Integer get(ActivityStatus model) {
				return model.getId();
			}
			@Override
			public void set(ActivityStatus model, Integer value) {
				model.setId(value);
			}
		});

	public static final Property<String, ActivityStatus> NAME = Property.create(
		"name" //$NON-NLS-1$
		, ActivityStatus.class
		, new TypeReference<String>() {}
		, new Property.Accessor<String, ActivityStatus>() { private static final long serialVersionUID = 1L;
			@Override
			public String get(ActivityStatus model) {
				return model.getName();
			}
			@Override
			public void set(ActivityStatus model, String value) {
				model.setName(value);
			}
		});

   public static final List<Property<?, ? super ActivityStatus>> PROPERTIES;
   static {
      List<Property<?, ? super ActivityStatus>> aList = new ArrayList<Property<?, ? super ActivityStatus>>();
      aList.add(ID);
      aList.add(NAME);
      PROPERTIES = Collections.unmodifiableList(aList);
   };
}
