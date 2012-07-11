package edu.ualberta.med.biobank.common.peer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import edu.ualberta.med.biobank.common.util.TypeReference;
import edu.ualberta.med.biobank.common.wrappers.Property;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.ContainerType;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.Study;

public class SitePeer  extends CenterPeer {
	public static final Property<Collection<Container>, Site> CONTAINERS = Property.create(
		"containers" //$NON-NLS-1$
		, Site.class
		, new TypeReference<Collection<Container>>() {}
		, new Property.Accessor<Collection<Container>, Site>() { private static final long serialVersionUID = 1L;
			@Override
			public Collection<Container> get(Site model) {
				return model.getContainers();
			}
			@Override
			public void set(Site model, Collection<Container> value) {
				model.getContainers().clear();
				model.getContainers().addAll(value);
			}
		});

	public static final Property<Collection<ContainerType>, Site> CONTAINER_TYPES = Property.create(
		"containerTypes" //$NON-NLS-1$
		, Site.class
		, new TypeReference<Collection<ContainerType>>() {}
		, new Property.Accessor<Collection<ContainerType>, Site>() { private static final long serialVersionUID = 1L;
			@Override
			public Collection<ContainerType> get(Site model) {
				return model.getContainerTypes();
			}
			@Override
			public void set(Site model, Collection<ContainerType> value) {
				model.getContainerTypes().clear();
				model.getContainerTypes().addAll(value);
			}
		});

	public static final Property<Collection<Study>, Site> STUDIES = Property.create(
		"studies" //$NON-NLS-1$
		, Site.class
		, new TypeReference<Collection<Study>>() {}
		, new Property.Accessor<Collection<Study>, Site>() { private static final long serialVersionUID = 1L;
			@Override
			public Collection<Study> get(Site model) {
				return model.getStudies();
			}
			@Override
			public void set(Site model, Collection<Study> value) {
				model.getStudies().clear();
				model.getStudies().addAll(value);
			}
		});

   public static final List<Property<?, ? super Site>> PROPERTIES;
   static {
      List<Property<?, ? super Site>> aList = new ArrayList<Property<?, ? super Site>>();
      aList.add(CONTAINERS);
      aList.add(CONTAINER_TYPES);
      aList.add(STUDIES);
      PROPERTIES = Collections.unmodifiableList(aList);
   };
}
