package edu.ualberta.med.biobank.common.peer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import edu.ualberta.med.biobank.common.util.TypeReference;
import edu.ualberta.med.biobank.common.wrappers.Property;
import edu.ualberta.med.biobank.model.Entity;
import edu.ualberta.med.biobank.model.EntityProperty;
import edu.ualberta.med.biobank.model.Report;

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

	public static final Property<Collection<EntityProperty>, Entity> ENTITY_PROPERTIES = Property.create(
		"entityProperties" //$NON-NLS-1$
		, Entity.class
		, new TypeReference<Collection<EntityProperty>>() {}
		, new Property.Accessor<Collection<EntityProperty>, Entity>() { private static final long serialVersionUID = 1L;
			@Override
			public Collection<EntityProperty> get(Entity model) {
				return model.getEntityProperties();
			}
			@Override
			public void set(Entity model, Collection<EntityProperty> value) {
				model.getEntityProperties().clear();
				model.getEntityProperties().addAll(value);
			}
		});

	public static final Property<Collection<Report>, Entity> REPORTS = Property.create(
		"reports" //$NON-NLS-1$
		, Entity.class
		, new TypeReference<Collection<Report>>() {}
		, new Property.Accessor<Collection<Report>, Entity>() { private static final long serialVersionUID = 1L;
			@Override
			public Collection<Report> get(Entity model) {
				return model.getReports();
			}
			@Override
			public void set(Entity model, Collection<Report> value) {
				model.getReports().clear();
				model.getReports().addAll(value);
			}
		});

   public static final List<Property<?, ? super Entity>> PROPERTIES;
   static {
      List<Property<?, ? super Entity>> aList = new ArrayList<Property<?, ? super Entity>>();
      aList.add(ID);
      aList.add(NAME);
      aList.add(CLASS_NAME);
      aList.add(ENTITY_PROPERTIES);
      aList.add(REPORTS);
      PROPERTIES = Collections.unmodifiableList(aList);
   };
}
