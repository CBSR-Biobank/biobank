package edu.ualberta.med.biobank.common.peer;

import edu.ualberta.med.biobank.common.util.TypeReference;
import java.util.Collections;
import edu.ualberta.med.biobank.common.wrappers.Property;
import java.util.List;
import java.util.ArrayList;
import edu.ualberta.med.biobank.model.Permission;

public class PermissionPeer {
	public static final Property<Integer, Permission> ID = Property.create(
		"id" //$NON-NLS-1$
		, Permission.class
		, new TypeReference<Integer>() {}
		, new Property.Accessor<Integer, Permission>() { private static final long serialVersionUID = 1L;
			@Override
			public Integer get(Permission model) {
				return model.getId();
			}
			@Override
			public void set(Permission model, Integer value) {
				model.setId(value);
			}
		});

	public static final Property<String, Permission> CLASS_NAME = Property.create(
		"className" //$NON-NLS-1$
		, Permission.class
		, new TypeReference<String>() {}
		, new Property.Accessor<String, Permission>() { private static final long serialVersionUID = 1L;
			@Override
			public String get(Permission model) {
				return model.getClassName();
			}
			@Override
			public void set(Permission model, String value) {
				model.setClassName(value);
			}
		});

   public static final List<Property<?, ? super Permission>> PROPERTIES;
   static {
      List<Property<?, ? super Permission>> aList = new ArrayList<Property<?, ? super Permission>>();
      aList.add(ID);
      aList.add(CLASS_NAME);
      PROPERTIES = Collections.unmodifiableList(aList);
   };
}
