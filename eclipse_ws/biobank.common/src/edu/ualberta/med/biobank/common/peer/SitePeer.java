package edu.ualberta.med.biobank.common.peer;

import edu.ualberta.med.biobank.common.util.TypeReference;
import java.util.Collections;
import edu.ualberta.med.biobank.common.wrappers.Property;
import java.util.List;
import java.util.ArrayList;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.ContainerType;
import edu.ualberta.med.biobank.model.Study;
import java.util.Collection;
import edu.ualberta.med.biobank.model.Site;

public class SitePeer  extends CenterPeer {
	public static final Property<Collection<Container>, Site> CONTAINER_COLLECTION = Property.create(
		"containerCollection" //$NON-NLS-1$
		, Site.class
		, new TypeReference<Collection<Container>>() {}
		, new Property.Accessor<Collection<Container>, Site>() { private static final long serialVersionUID = 1L;
			@Override
			public Collection<Container> get(Site model) {
				return model.getContainerCollection();
			}
			@Override
			public void set(Site model, Collection<Container> value) {
				model.setContainerCollection(value);
			}
		});

	public static final Property<Collection<ContainerType>, Site> CONTAINER_TYPE_COLLECTION = Property.create(
		"containerTypeCollection" //$NON-NLS-1$
		, Site.class
		, new TypeReference<Collection<ContainerType>>() {}
		, new Property.Accessor<Collection<ContainerType>, Site>() { private static final long serialVersionUID = 1L;
			@Override
			public Collection<ContainerType> get(Site model) {
				return model.getContainerTypeCollection();
			}
			@Override
			public void set(Site model, Collection<ContainerType> value) {
				model.setContainerTypeCollection(value);
			}
		});

	public static final Property<Collection<Study>, Site> STUDY_COLLECTION = Property.create(
		"studyCollection" //$NON-NLS-1$
		, Site.class
		, new TypeReference<Collection<Study>>() {}
		, new Property.Accessor<Collection<Study>, Site>() { private static final long serialVersionUID = 1L;
			@Override
			public Collection<Study> get(Site model) {
				return model.getStudyCollection();
			}
			@Override
			public void set(Site model, Collection<Study> value) {
				model.setStudyCollection(value);
			}
		});

   public static final List<Property<?, ? super Site>> PROPERTIES;
   static {
      List<Property<?, ? super Site>> aList = new ArrayList<Property<?, ? super Site>>();
      aList.add(CONTAINER_COLLECTION);
      aList.add(CONTAINER_TYPE_COLLECTION);
      aList.add(STUDY_COLLECTION);
      PROPERTIES = Collections.unmodifiableList(aList);
   };
}
