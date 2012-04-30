package edu.ualberta.med.biobank.common.peer;

import edu.ualberta.med.biobank.common.util.TypeReference;
import java.util.Collections;
import edu.ualberta.med.biobank.common.wrappers.Property;
import java.util.List;
import java.util.ArrayList;
import edu.ualberta.med.biobank.model.PropertyModifier;
import edu.ualberta.med.biobank.model.EntityColumn;
import edu.ualberta.med.biobank.model.ReportColumn;

public class ReportColumnPeer {
	public static final Property<Integer, ReportColumn> POSITION = Property.create(
		"position" //$NON-NLS-1$
		, ReportColumn.class
		, new TypeReference<Integer>() {}
		, new Property.Accessor<Integer, ReportColumn>() { private static final long serialVersionUID = 1L;
			@Override
			public Integer get(ReportColumn model) {
				return model.getPosition();
			}
			@Override
			public void set(ReportColumn model, Integer value) {
				model.setPosition(value);
			}
		});

	public static final Property<Integer, ReportColumn> ID = Property.create(
		"id" //$NON-NLS-1$
		, ReportColumn.class
		, new TypeReference<Integer>() {}
		, new Property.Accessor<Integer, ReportColumn>() { private static final long serialVersionUID = 1L;
			@Override
			public Integer get(ReportColumn model) {
				return model.getId();
			}
			@Override
			public void set(ReportColumn model, Integer value) {
				model.setId(value);
			}
		});

	public static final Property<PropertyModifier, ReportColumn> PROPERTY_MODIFIER = Property.create(
		"propertyModifier" //$NON-NLS-1$
		, ReportColumn.class
		, new TypeReference<PropertyModifier>() {}
		, new Property.Accessor<PropertyModifier, ReportColumn>() { private static final long serialVersionUID = 1L;
			@Override
			public PropertyModifier get(ReportColumn model) {
				return model.getPropertyModifier();
			}
			@Override
			public void set(ReportColumn model, PropertyModifier value) {
				model.setPropertyModifier(value);
			}
		});

	public static final Property<EntityColumn, ReportColumn> ENTITY_COLUMN = Property.create(
		"entityColumn" //$NON-NLS-1$
		, ReportColumn.class
		, new TypeReference<EntityColumn>() {}
		, new Property.Accessor<EntityColumn, ReportColumn>() { private static final long serialVersionUID = 1L;
			@Override
			public EntityColumn get(ReportColumn model) {
				return model.getEntityColumn();
			}
			@Override
			public void set(ReportColumn model, EntityColumn value) {
				model.setEntityColumn(value);
			}
		});

   public static final List<Property<?, ? super ReportColumn>> PROPERTIES;
   static {
      List<Property<?, ? super ReportColumn>> aList = new ArrayList<Property<?, ? super ReportColumn>>();
      aList.add(POSITION);
      aList.add(ID);
      aList.add(PROPERTY_MODIFIER);
      aList.add(ENTITY_COLUMN);
      PROPERTIES = Collections.unmodifiableList(aList);
   };
}
