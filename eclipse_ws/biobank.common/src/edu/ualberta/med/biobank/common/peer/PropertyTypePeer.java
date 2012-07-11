package edu.ualberta.med.biobank.common.peer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import edu.ualberta.med.biobank.common.util.TypeReference;
import edu.ualberta.med.biobank.common.wrappers.Property;
import edu.ualberta.med.biobank.model.PropertyModifier;
import edu.ualberta.med.biobank.model.PropertyType;

public class PropertyTypePeer {
	public static final Property<Integer, PropertyType> ID = Property.create(
		"id" //$NON-NLS-1$
		, PropertyType.class
		, new TypeReference<Integer>() {}
		, new Property.Accessor<Integer, PropertyType>() { private static final long serialVersionUID = 1L;
			@Override
			public Integer get(PropertyType model) {
				return model.getId();
			}
			@Override
			public void set(PropertyType model, Integer value) {
				model.setId(value);
			}
		});

	public static final Property<String, PropertyType> NAME = Property.create(
		"name" //$NON-NLS-1$
		, PropertyType.class
		, new TypeReference<String>() {}
		, new Property.Accessor<String, PropertyType>() { private static final long serialVersionUID = 1L;
			@Override
			public String get(PropertyType model) {
				return model.getName();
			}
			@Override
			public void set(PropertyType model, String value) {
				model.setName(value);
			}
		});

	public static final Property<Collection<PropertyModifier>, PropertyType> PROPERTY_MODIFIERS = Property.create(
		"propertyModifiers" //$NON-NLS-1$
		, PropertyType.class
		, new TypeReference<Collection<PropertyModifier>>() {}
		, new Property.Accessor<Collection<PropertyModifier>, PropertyType>() { private static final long serialVersionUID = 1L;
			@Override
			public Collection<PropertyModifier> get(PropertyType model) {
				return model.getPropertyModifiers();
			}
			@Override
			public void set(PropertyType model, Collection<PropertyModifier> value) {
				model.getPropertyModifiers().clear();
				model.getPropertyModifiers().addAll(value);
			}
		});

   public static final List<Property<?, ? super PropertyType>> PROPERTIES;
   static {
      List<Property<?, ? super PropertyType>> aList = new ArrayList<Property<?, ? super PropertyType>>();
      aList.add(ID);
      aList.add(NAME);
      aList.add(PROPERTY_MODIFIERS);
      PROPERTIES = Collections.unmodifiableList(aList);
   };
}
