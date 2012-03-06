package edu.ualberta.med.biobank.common.peer;

import edu.ualberta.med.biobank.common.util.TypeReference;
import java.util.Collections;
import edu.ualberta.med.biobank.common.wrappers.Property;
import java.util.List;
import java.util.ArrayList;
import edu.ualberta.med.biobank.model.ContainerType;
import edu.ualberta.med.biobank.model.SpecimenType;
import java.util.Collection;

public class SpecimenTypePeer {
	public static final Property<Integer, SpecimenType> ID = Property.create(
		"id" //$NON-NLS-1$
		, SpecimenType.class
		, new TypeReference<Integer>() {}
		, new Property.Accessor<Integer, SpecimenType>() { private static final long serialVersionUID = 1L;
			@Override
			public Integer get(SpecimenType model) {
				return model.getId();
			}
			@Override
			public void set(SpecimenType model, Integer value) {
				model.setId(value);
			}
		});

	public static final Property<String, SpecimenType> NAME = Property.create(
		"name" //$NON-NLS-1$
		, SpecimenType.class
		, new TypeReference<String>() {}
		, new Property.Accessor<String, SpecimenType>() { private static final long serialVersionUID = 1L;
			@Override
			public String get(SpecimenType model) {
				return model.getName();
			}
			@Override
			public void set(SpecimenType model, String value) {
				model.setName(value);
			}
		});

	public static final Property<String, SpecimenType> NAME_SHORT = Property.create(
		"nameShort" //$NON-NLS-1$
		, SpecimenType.class
		, new TypeReference<String>() {}
		, new Property.Accessor<String, SpecimenType>() { private static final long serialVersionUID = 1L;
			@Override
			public String get(SpecimenType model) {
				return model.getNameShort();
			}
			@Override
			public void set(SpecimenType model, String value) {
				model.setNameShort(value);
			}
		});

	public static final Property<Collection<ContainerType>, SpecimenType> CONTAINER_TYPE_COLLECTION = Property.create(
		"containerTypeCollection" //$NON-NLS-1$
		, SpecimenType.class
		, new TypeReference<Collection<ContainerType>>() {}
		, new Property.Accessor<Collection<ContainerType>, SpecimenType>() { private static final long serialVersionUID = 1L;
			@Override
			public Collection<ContainerType> get(SpecimenType model) {
				return model.getContainerTypes();
			}
			@Override
			public void set(SpecimenType model, Collection<ContainerType> value) {
				model.getContainerTypes().clear();
				model.getContainerTypes().addAll(value);
			}
		});

	public static final Property<Collection<SpecimenType>, SpecimenType> PARENT_SPECIMEN_TYPE_COLLECTION = Property.create(
		"parentSpecimenTypeCollection" //$NON-NLS-1$
		, SpecimenType.class
		, new TypeReference<Collection<SpecimenType>>() {}
		, new Property.Accessor<Collection<SpecimenType>, SpecimenType>() { private static final long serialVersionUID = 1L;
			@Override
			public Collection<SpecimenType> get(SpecimenType model) {
				return model.getParentSpecimenTypes();
			}
			@Override
			public void set(SpecimenType model, Collection<SpecimenType> value) {
				model.getParentSpecimenTypes().clear();
				model.getParentSpecimenTypes().addAll(value);
			}
		});

	public static final Property<Collection<SpecimenType>, SpecimenType> CHILD_SPECIMEN_TYPE_COLLECTION = Property.create(
		"childSpecimenTypeCollection" //$NON-NLS-1$
		, SpecimenType.class
		, new TypeReference<Collection<SpecimenType>>() {}
		, new Property.Accessor<Collection<SpecimenType>, SpecimenType>() { private static final long serialVersionUID = 1L;
			@Override
			public Collection<SpecimenType> get(SpecimenType model) {
				return model.getChildSpecimenTypes();
			}
			@Override
			public void set(SpecimenType model, Collection<SpecimenType> value) {
				model.getChildSpecimenTypes().clear();
				model.getChildSpecimenTypes().addAll(value);
			}
		});

   public static final List<Property<?, ? super SpecimenType>> PROPERTIES;
   static {
      List<Property<?, ? super SpecimenType>> aList = new ArrayList<Property<?, ? super SpecimenType>>();
      aList.add(ID);
      aList.add(NAME);
      aList.add(NAME_SHORT);
      aList.add(CONTAINER_TYPE_COLLECTION);
      aList.add(PARENT_SPECIMEN_TYPE_COLLECTION);
      aList.add(CHILD_SPECIMEN_TYPE_COLLECTION);
      PROPERTIES = Collections.unmodifiableList(aList);
   };
}
