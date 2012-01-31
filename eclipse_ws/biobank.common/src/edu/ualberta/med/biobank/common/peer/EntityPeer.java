package edu.ualberta.med.biobank.common.peer;

import edu.ualberta.med.biobank.common.util.TypeReference;
import java.util.Collections;
import edu.ualberta.med.biobank.common.wrappers.Property;
import java.util.List;
import java.util.ArrayList;
import edu.ualberta.med.biobank.model.EntityProperty;
import edu.ualberta.med.biobank.model.Report;
import java.util.Collection;
import edu.ualberta.med.biobank.model.Entity;

public class EntityPeer {
	public static final Property<Integer, Entity> ID = Property.create(
		"id" //$NON-NLS-1$
		, Entity.class
		, new TypeReference<Integer>() {}
		, new Property.Accessor<Integer, Entity>() { private static final long serialVersionUID = 1L;
			@Override
			public Integer get(Entity model) {
				return model.getId();
			}
			@Override
			public void set(Entity model, Integer value) {
				model.setId(value);
			}
		});

	public static final Property<String, Entity> NAME = Property.create(
		"name" //$NON-NLS-1$
		, Entity.class
		, new TypeReference<String>() {}
		, new Property.Accessor<String, Entity>() { private static final long serialVersionUID = 1L;
			@Override
			public String get(Entity model) {
				return model.getName();
			}
			@Override
			public void set(Entity model, String value) {
				model.setName(value);
			}
		});

	public static final Property<String, Entity> CLASS_NAME = Property.create(
		"className" //$NON-NLS-1$
		, Entity.class
		, new TypeReference<String>() {}
		, new Property.Accessor<String, Entity>() { private static final long serialVersionUID = 1L;
			@Override
			public String get(Entity model) {
				return model.getClassName();
			}
			@Override
			public void set(Entity model, String value) {
				model.setClassName(value);
			}
		});

	public static final Property<Collection<EntityProperty>, Entity> ENTITY_PROPERTY_COLLECTION = Property.create(
		"entityPropertyCollection" //$NON-NLS-1$
		, Entity.class
		, new TypeReference<Collection<EntityProperty>>() {}
		, new Property.Accessor<Collection<EntityProperty>, Entity>() { private static final long serialVersionUID = 1L;
			@Override
			public Collection<EntityProperty> get(Entity model) {
				return model.getEntityPropertyCollection();
			}
			@Override
			public void set(Entity model, Collection<EntityProperty> value) {
				model.getEntityPropertyCollection().clear();
				model.getEntityPropertyCollection().addAll(value);
			}
		});

	public static final Property<Collection<Report>, Entity> REPORT_COLLECTION = Property.create(
		"reportCollection" //$NON-NLS-1$
		, Entity.class
		, new TypeReference<Collection<Report>>() {}
		, new Property.Accessor<Collection<Report>, Entity>() { private static final long serialVersionUID = 1L;
			@Override
			public Collection<Report> get(Entity model) {
				return model.getReportCollection();
			}
			@Override
			public void set(Entity model, Collection<Report> value) {
				model.getReportCollection().clear();
				model.getReportCollection().addAll(value);
			}
		});

   public static final List<Property<?, ? super Entity>> PROPERTIES;
   static {
      List<Property<?, ? super Entity>> aList = new ArrayList<Property<?, ? super Entity>>();
      aList.add(ID);
      aList.add(NAME);
      aList.add(CLASS_NAME);
      aList.add(ENTITY_PROPERTY_COLLECTION);
      aList.add(REPORT_COLLECTION);
      PROPERTIES = Collections.unmodifiableList(aList);
   };
}
