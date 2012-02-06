package edu.ualberta.med.biobank.common.peer;

import edu.ualberta.med.biobank.common.util.TypeReference;
import java.util.Collections;
import edu.ualberta.med.biobank.common.wrappers.Property;
import java.util.List;
import java.util.ArrayList;
import edu.ualberta.med.biobank.model.EntityProperty;
import edu.ualberta.med.biobank.model.EntityColumn;

public class EntityColumnPeer {
	public static final Property<Integer, EntityColumn> ID = Property.create(
		"id" //$NON-NLS-1$
		, EntityColumn.class
		, new TypeReference<Integer>() {}
		, new Property.Accessor<Integer, EntityColumn>() { private static final long serialVersionUID = 1L;
			@Override
			public Integer get(EntityColumn model) {
				return model.getId();
			}
			@Override
			public void set(EntityColumn model, Integer value) {
				model.setId(value);
			}
		});

	public static final Property<String, EntityColumn> NAME = Property.create(
		"name" //$NON-NLS-1$
		, EntityColumn.class
		, new TypeReference<String>() {}
		, new Property.Accessor<String, EntityColumn>() { private static final long serialVersionUID = 1L;
			@Override
			public String get(EntityColumn model) {
				return model.getName();
			}
			@Override
			public void set(EntityColumn model, String value) {
				model.setName(value);
			}
		});

	public static final Property<EntityProperty, EntityColumn> ENTITY_PROPERTY = Property.create(
		"entityProperty" //$NON-NLS-1$
		, EntityColumn.class
		, new TypeReference<EntityProperty>() {}
		, new Property.Accessor<EntityProperty, EntityColumn>() { private static final long serialVersionUID = 1L;
			@Override
			public EntityProperty get(EntityColumn model) {
				return model.getEntityProperty();
			}
			@Override
			public void set(EntityColumn model, EntityProperty value) {
				model.setEntityProperty(value);
			}
		});

   public static final List<Property<?, ? super EntityColumn>> PROPERTIES;
   static {
      List<Property<?, ? super EntityColumn>> aList = new ArrayList<Property<?, ? super EntityColumn>>();
      aList.add(ID);
      aList.add(NAME);
      aList.add(ENTITY_PROPERTY);
      PROPERTIES = Collections.unmodifiableList(aList);
   };
}
