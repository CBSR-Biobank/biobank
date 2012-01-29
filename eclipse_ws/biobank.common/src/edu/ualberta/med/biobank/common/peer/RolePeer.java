package edu.ualberta.med.biobank.common.peer;

import edu.ualberta.med.biobank.common.permission.PermissionEnum;
import edu.ualberta.med.biobank.common.util.TypeReference;
import java.util.Collections;
import edu.ualberta.med.biobank.common.wrappers.Property;
import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import edu.ualberta.med.biobank.model.Role;

public class RolePeer {
	public static final Property<Integer, Role> ID = Property.create(
		"id" //$NON-NLS-1$
		, Role.class
		, new TypeReference<Integer>() {}
		, new Property.Accessor<Integer, Role>() { private static final long serialVersionUID = 1L;
			@Override
			public Integer get(Role model) {
				return model.getId();
			}
			@Override
			public void set(Role model, Integer value) {
				model.setId(value);
			}
		});

	public static final Property<String, Role> NAME = Property.create(
		"name" //$NON-NLS-1$
		, Role.class
		, new TypeReference<String>() {}
		, new Property.Accessor<String, Role>() { private static final long serialVersionUID = 1L;
			@Override
			public String get(Role model) {
				return model.getName();
			}
			@Override
			public void set(Role model, String value) {
				model.setName(value);
			}
		});

	public static final Property<Collection<PermissionEnum>, Role> PERMISSION_COLLECTION = Property.create(
		"permissionCollection" //$NON-NLS-1$
		, Role.class
		, new TypeReference<Collection<PermissionEnum>>() {}
		, new Property.Accessor<Collection<PermissionEnum>, Role>() { private static final long serialVersionUID = 1L;
			@Override
			public Collection<PermissionEnum> get(Role model) {
				return model.getPermissionCollection();
			}
			@Override
			public void set(Role model, Collection<PermissionEnum> value) {
				model.setPermissionCollection(value);
			}
		});

   public static final List<Property<?, ? super Role>> PROPERTIES;
   static {
      List<Property<?, ? super Role>> aList = new ArrayList<Property<?, ? super Role>>();
      aList.add(ID);
      aList.add(NAME);
      aList.add(PERMISSION_COLLECTION);
      PROPERTIES = Collections.unmodifiableList(aList);
   };
}
