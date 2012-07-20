package edu.ualberta.med.biobank.common.peer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.ualberta.med.biobank.common.util.TypeReference;
import edu.ualberta.med.biobank.common.wrappers.Property;
import edu.ualberta.med.biobank.model.EntityColumn;
import edu.ualberta.med.biobank.model.EntityProperty;

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
