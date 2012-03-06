package edu.ualberta.med.biobank.common.peer;

import edu.ualberta.med.biobank.common.util.TypeReference;
import java.util.Collections;
import edu.ualberta.med.biobank.common.wrappers.Property;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

import edu.ualberta.med.biobank.model.EntityFilter;
import edu.ualberta.med.biobank.model.PropertyType;
import edu.ualberta.med.biobank.model.EntityColumn;
import edu.ualberta.med.biobank.model.EntityProperty;

public class EntityPropertyPeer {
	public static final Property<Integer, EntityProperty> ID = Property.create(
		"id" //$NON-NLS-1$
		, EntityProperty.class
		, new TypeReference<Integer>() {}
		, new Property.Accessor<Integer, EntityProperty>() { private static final long serialVersionUID = 1L;
			@Override
			public Integer get(EntityProperty model) {
				return model.getId();
			}
			@Override
			public void set(EntityProperty model, Integer value) {
				model.setId(value);
			}
		});

	public static final Property<String, EntityProperty> PROPERTY = Property.create(
		"property" //$NON-NLS-1$
		, EntityProperty.class
		, new TypeReference<String>() {}
		, new Property.Accessor<String, EntityProperty>() { private static final long serialVersionUID = 1L;
			@Override
			public String get(EntityProperty model) {
				return model.getProperty();
			}
			@Override
			public void set(EntityProperty model, String value) {
				model.setProperty(value);
			}
		});

	public static final Property<Set<EntityFilter>, EntityProperty> ENTITY_FILTERS = Property.create(
		"entityFilters" //$NON-NLS-1$
		, EntityProperty.class
		, new TypeReference<Set<EntityFilter>>() {}
		, new Property.Accessor<Set<EntityFilter>, EntityProperty>() { private static final long serialVersionUID = 1L;
			@Override
			public Set<EntityFilter> get(EntityProperty model) {
				return model.getEntityFilters();
			}
			@Override
			public void set(EntityProperty model, Set<EntityFilter> value) {
				model.setEntityFilters(value);
			}
		});

	public static final Property<PropertyType, EntityProperty> PROPERTY_TYPE = Property.create(
		"propertyType" //$NON-NLS-1$
		, EntityProperty.class
		, new TypeReference<PropertyType>() {}
		, new Property.Accessor<PropertyType, EntityProperty>() { private static final long serialVersionUID = 1L;
			@Override
			public PropertyType get(EntityProperty model) {
				return model.getPropertyType();
			}
			@Override
			public void set(EntityProperty model, PropertyType value) {
				model.setPropertyType(value);
			}
		});

	public static final Property<Set<EntityColumn>, EntityProperty> ENTITY_COLUMNS = Property.create(
		"entityColumns" //$NON-NLS-1$
		, EntityProperty.class
		, new TypeReference<Set<EntityColumn>>() {}
		, new Property.Accessor<Set<EntityColumn>, EntityProperty>() { private static final long serialVersionUID = 1L;
			@Override
			public Set<EntityColumn> get(EntityProperty model) {
				return model.getEntityColumns();
			}
			@Override
			public void set(EntityProperty model, Set<EntityColumn> value) {
				model.setEntityColumns(value);
			}
		});

   public static final List<Property<?, ? super EntityProperty>> PROPERTIES;
   static {
      List<Property<?, ? super EntityProperty>> aList = new ArrayList<Property<?, ? super EntityProperty>>();
      aList.add(ID);
      aList.add(PROPERTY);
      aList.add(ENTITY_FILTERS);
      aList.add(PROPERTY_TYPE);
      aList.add(ENTITY_COLUMNS);
      PROPERTIES = Collections.unmodifiableList(aList);
   };
}
