package edu.ualberta.med.biobank.common.peer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.ualberta.med.biobank.common.util.TypeReference;
import edu.ualberta.med.biobank.common.wrappers.Property;
import edu.ualberta.med.biobank.model.EntityFilter;
import edu.ualberta.med.biobank.model.EntityProperty;

public class EntityFilterPeer {
	public static final Property<Integer, EntityFilter> ID = Property.create(
		"id" //$NON-NLS-1$
		, EntityFilter.class
		, new TypeReference<Integer>() {}
		, new Property.Accessor<Integer, EntityFilter>() { private static final long serialVersionUID = 1L;
			@Override
			public Integer get(EntityFilter model) {
				return model.getId();
			}
			@Override
			public void set(EntityFilter model, Integer value) {
				model.setId(value);
			}
		});

	public static final Property<String, EntityFilter> NAME = Property.create(
		"name" //$NON-NLS-1$
		, EntityFilter.class
		, new TypeReference<String>() {}
		, new Property.Accessor<String, EntityFilter>() { private static final long serialVersionUID = 1L;
			@Override
			public String get(EntityFilter model) {
				return model.getName();
			}
			@Override
			public void set(EntityFilter model, String value) {
				model.setName(value);
			}
		});

	public static final Property<Integer, EntityFilter> FILTER_TYPE = Property.create(
		"filterType" //$NON-NLS-1$
		, EntityFilter.class
		, new TypeReference<Integer>() {}
		, new Property.Accessor<Integer, EntityFilter>() { private static final long serialVersionUID = 1L;
			@Override
			public Integer get(EntityFilter model) {
				return model.getFilterType();
			}
			@Override
			public void set(EntityFilter model, Integer value) {
				model.setFilterType(value);
			}
		});

	public static final Property<EntityProperty, EntityFilter> ENTITY_PROPERTY = Property.create(
		"entityProperty" //$NON-NLS-1$
		, EntityFilter.class
		, new TypeReference<EntityProperty>() {}
		, new Property.Accessor<EntityProperty, EntityFilter>() { private static final long serialVersionUID = 1L;
			@Override
			public EntityProperty get(EntityFilter model) {
				return model.getEntityProperty();
			}
			@Override
			public void set(EntityFilter model, EntityProperty value) {
				model.setEntityProperty(value);
			}
		});

   public static final List<Property<?, ? super EntityFilter>> PROPERTIES;
   static {
      List<Property<?, ? super EntityFilter>> aList = new ArrayList<Property<?, ? super EntityFilter>>();
      aList.add(ID);
      aList.add(NAME);
      aList.add(FILTER_TYPE);
      aList.add(ENTITY_PROPERTY);
      PROPERTIES = Collections.unmodifiableList(aList);
   };
}
