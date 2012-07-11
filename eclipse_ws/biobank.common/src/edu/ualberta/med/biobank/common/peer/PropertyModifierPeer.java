package edu.ualberta.med.biobank.common.peer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.ualberta.med.biobank.common.util.TypeReference;
import edu.ualberta.med.biobank.common.wrappers.Property;
import edu.ualberta.med.biobank.model.PropertyModifier;

public class PropertyModifierPeer {
	public static final Property<Integer, PropertyModifier> ID = Property.create(
		"id" //$NON-NLS-1$
		, PropertyModifier.class
		, new TypeReference<Integer>() {}
		, new Property.Accessor<Integer, PropertyModifier>() { private static final long serialVersionUID = 1L;
			@Override
			public Integer get(PropertyModifier model) {
				return model.getId();
			}
			@Override
			public void set(PropertyModifier model, Integer value) {
				model.setId(value);
			}
		});

	public static final Property<String, PropertyModifier> NAME = Property.create(
		"name" //$NON-NLS-1$
		, PropertyModifier.class
		, new TypeReference<String>() {}
		, new Property.Accessor<String, PropertyModifier>() { private static final long serialVersionUID = 1L;
			@Override
			public String get(PropertyModifier model) {
				return model.getName();
			}
			@Override
			public void set(PropertyModifier model, String value) {
				model.setName(value);
			}
		});

	public static final Property<String, PropertyModifier> PROPERTY_MODIFIER = Property.create(
		"propertyModifier" //$NON-NLS-1$
		, PropertyModifier.class
		, new TypeReference<String>() {}
		, new Property.Accessor<String, PropertyModifier>() { private static final long serialVersionUID = 1L;
			@Override
			public String get(PropertyModifier model) {
				return model.getPropertyModifier();
			}
			@Override
			public void set(PropertyModifier model, String value) {
				model.setPropertyModifier(value);
			}
		});

   public static final List<Property<?, ? super PropertyModifier>> PROPERTIES;
   static {
      List<Property<?, ? super PropertyModifier>> aList = new ArrayList<Property<?, ? super PropertyModifier>>();
      aList.add(ID);
      aList.add(NAME);
      aList.add(PROPERTY_MODIFIER);
      PROPERTIES = Collections.unmodifiableList(aList);
   };
}
