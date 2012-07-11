package edu.ualberta.med.biobank.common.peer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import edu.ualberta.med.biobank.common.util.TypeReference;
import edu.ualberta.med.biobank.common.wrappers.Property;
import edu.ualberta.med.biobank.model.Group;
import edu.ualberta.med.biobank.model.User;

public class GroupPeer  extends PrincipalPeer {
	public static final Property<String, Group> DESCRIPTION = Property.create(
		"description" //$NON-NLS-1$
		, Group.class
		, new TypeReference<String>() {}
		, new Property.Accessor<String, Group>() { private static final long serialVersionUID = 1L;
			@Override
			public String get(Group model) {
				return model.getDescription();
			}
			@Override
			public void set(Group model, String value) {
				model.setDescription(value);
			}
		});

	public static final Property<String, Group> NAME = Property.create(
		"name" //$NON-NLS-1$
		, Group.class
		, new TypeReference<String>() {}
		, new Property.Accessor<String, Group>() { private static final long serialVersionUID = 1L;
			@Override
			public String get(Group model) {
				return model.getName();
			}
			@Override
			public void set(Group model, String value) {
				model.setName(value);
			}
		});

	public static final Property<Collection<User>, Group> USERS = Property.create(
		"users" //$NON-NLS-1$
		, Group.class
		, new TypeReference<Collection<User>>() {}
		, new Property.Accessor<Collection<User>, Group>() { private static final long serialVersionUID = 1L;
			@Override
			public Collection<User> get(Group model) {
				return model.getUsers();
			}
			@Override
			public void set(Group model, Collection<User> value) {
				model.getUsers().clear();
				model.getUsers().addAll(value);
			}
		});

   public static final List<Property<?, ? super Group>> PROPERTIES;
   static {
      List<Property<?, ? super Group>> aList = new ArrayList<Property<?, ? super Group>>();
      aList.add(DESCRIPTION);
      aList.add(NAME);
      aList.add(USERS);
      PROPERTIES = Collections.unmodifiableList(aList);
   };
}
