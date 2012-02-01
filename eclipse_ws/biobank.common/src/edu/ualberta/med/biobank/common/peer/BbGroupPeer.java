package edu.ualberta.med.biobank.common.peer;

import edu.ualberta.med.biobank.common.util.TypeReference;
import java.util.Collections;
import edu.ualberta.med.biobank.common.wrappers.Property;
import java.util.List;
import java.util.ArrayList;
import edu.ualberta.med.biobank.model.User;
import java.util.Collection;
import edu.ualberta.med.biobank.model.BbGroup;

public class BbGroupPeer  extends PrincipalPeer {
	public static final Property<String, BbGroup> DESCRIPTION = Property.create(
		"description" //$NON-NLS-1$
		, BbGroup.class
		, new TypeReference<String>() {}
		, new Property.Accessor<String, BbGroup>() { private static final long serialVersionUID = 1L;
			@Override
			public String get(BbGroup model) {
				return model.getDescription();
			}
			@Override
			public void set(BbGroup model, String value) {
				model.setDescription(value);
			}
		});

	public static final Property<String, BbGroup> NAME = Property.create(
		"name" //$NON-NLS-1$
		, BbGroup.class
		, new TypeReference<String>() {}
		, new Property.Accessor<String, BbGroup>() { private static final long serialVersionUID = 1L;
			@Override
			public String get(BbGroup model) {
				return model.getName();
			}
			@Override
			public void set(BbGroup model, String value) {
				model.setName(value);
			}
		});

	public static final Property<Collection<User>, BbGroup> USER_COLLECTION = Property.create(
		"userCollection" //$NON-NLS-1$
		, BbGroup.class
		, new TypeReference<Collection<User>>() {}
		, new Property.Accessor<Collection<User>, BbGroup>() { private static final long serialVersionUID = 1L;
			@Override
			public Collection<User> get(BbGroup model) {
				return model.getUserCollection();
			}
			@Override
			public void set(BbGroup model, Collection<User> value) {
				model.getUserCollection().clear();
				model.getUserCollection().addAll(value);
			}
		});

   public static final List<Property<?, ? super BbGroup>> PROPERTIES;
   static {
      List<Property<?, ? super BbGroup>> aList = new ArrayList<Property<?, ? super BbGroup>>();
      aList.add(DESCRIPTION);
      aList.add(NAME);
      aList.add(USER_COLLECTION);
      PROPERTIES = Collections.unmodifiableList(aList);
   };
}
