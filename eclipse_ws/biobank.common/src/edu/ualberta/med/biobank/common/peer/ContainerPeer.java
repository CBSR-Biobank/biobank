package edu.ualberta.med.biobank.common.peer;

import edu.ualberta.med.biobank.common.util.TypeReference;
import java.util.Collections;
import edu.ualberta.med.biobank.common.wrappers.Property;
import java.util.List;
import java.util.ArrayList;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.ContainerPosition;
import edu.ualberta.med.biobank.model.ContainerType;
import edu.ualberta.med.biobank.model.SpecimenPosition;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.Comment;
import edu.ualberta.med.biobank.model.ActivityStatus;
import java.util.Collection;

public class ContainerPeer {
	public static final Property<Integer, Container> ID = Property.create(
		"id" //$NON-NLS-1$
		, Container.class
		, new TypeReference<Integer>() {}
		, new Property.Accessor<Integer, Container>() { private static final long serialVersionUID = 1L;
			@Override
			public Integer get(Container model) {
				return model.getId();
			}
			@Override
			public void set(Container model, Integer value) {
				model.setId(value);
			}
		});

	public static final Property<String, Container> PRODUCT_BARCODE = Property.create(
		"productBarcode" //$NON-NLS-1$
		, Container.class
		, new TypeReference<String>() {}
		, new Property.Accessor<String, Container>() { private static final long serialVersionUID = 1L;
			@Override
			public String get(Container model) {
				return model.getProductBarcode();
			}
			@Override
			public void set(Container model, String value) {
				model.setProductBarcode(value);
			}
		});

	public static final Property<String, Container> PATH = Property.create(
		"path" //$NON-NLS-1$
		, Container.class
		, new TypeReference<String>() {}
		, new Property.Accessor<String, Container>() { private static final long serialVersionUID = 1L;
			@Override
			public String get(Container model) {
				return model.getPath();
			}
			@Override
			public void set(Container model, String value) {
				model.setPath(value);
			}
		});

	public static final Property<String, Container> LABEL = Property.create(
		"label" //$NON-NLS-1$
		, Container.class
		, new TypeReference<String>() {}
		, new Property.Accessor<String, Container>() { private static final long serialVersionUID = 1L;
			@Override
			public String get(Container model) {
				return model.getLabel();
			}
			@Override
			public void set(Container model, String value) {
				model.setLabel(value);
			}
		});

	public static final Property<Double, Container> TEMPERATURE = Property.create(
		"temperature" //$NON-NLS-1$
		, Container.class
		, new TypeReference<Double>() {}
		, new Property.Accessor<Double, Container>() { private static final long serialVersionUID = 1L;
			@Override
			public Double get(Container model) {
				return model.getTemperature();
			}
			@Override
			public void set(Container model, Double value) {
				model.setTemperature(value);
			}
		});

	public static final Property<Site, Container> SITE = Property.create(
		"site" //$NON-NLS-1$
		, Container.class
		, new TypeReference<Site>() {}
		, new Property.Accessor<Site, Container>() { private static final long serialVersionUID = 1L;
			@Override
			public Site get(Container model) {
				return model.getSite();
			}
			@Override
			public void set(Container model, Site value) {
				model.setSite(value);
			}
		});

	public static final Property<ContainerPosition, Container> POSITION = Property.create(
		"position" //$NON-NLS-1$
		, Container.class
		, new TypeReference<ContainerPosition>() {}
		, new Property.Accessor<ContainerPosition, Container>() { private static final long serialVersionUID = 1L;
			@Override
			public ContainerPosition get(Container model) {
				return model.getPosition();
			}
			@Override
			public void set(Container model, ContainerPosition value) {
				model.setPosition(value);
			}
		});

	public static final Property<ContainerType, Container> CONTAINER_TYPE = Property.create(
		"containerType" //$NON-NLS-1$
		, Container.class
		, new TypeReference<ContainerType>() {}
		, new Property.Accessor<ContainerType, Container>() { private static final long serialVersionUID = 1L;
			@Override
			public ContainerType get(Container model) {
				return model.getContainerType();
			}
			@Override
			public void set(Container model, ContainerType value) {
				model.setContainerType(value);
			}
		});

	public static final Property<Collection<SpecimenPosition>, Container> SPECIMEN_POSITION_COLLECTION = Property.create(
		"specimenPositionCollection" //$NON-NLS-1$
		, Container.class
		, new TypeReference<Collection<SpecimenPosition>>() {}
		, new Property.Accessor<Collection<SpecimenPosition>, Container>() { private static final long serialVersionUID = 1L;
			@Override
			public Collection<SpecimenPosition> get(Container model) {
				return model.getSpecimenPositions();
			}
			@Override
			public void set(Container model, Collection<SpecimenPosition> value) {
				model.getSpecimenPositions().clear();
				model.getSpecimenPositions().addAll(value);
			}
		});

	public static final Property<Collection<ContainerPosition>, Container> CHILD_POSITION_COLLECTION = Property.create(
		"childPositionCollection" //$NON-NLS-1$
		, Container.class
		, new TypeReference<Collection<ContainerPosition>>() {}
		, new Property.Accessor<Collection<ContainerPosition>, Container>() { private static final long serialVersionUID = 1L;
			@Override
			public Collection<ContainerPosition> get(Container model) {
				return model.getChildPositions();
			}
			@Override
			public void set(Container model, Collection<ContainerPosition> value) {
				model.getChildPositions().clear();
				model.getChildPositions().addAll(value);
			}
		});

	public static final Property<Container, Container> TOP_CONTAINER = Property.create(
		"topContainer" //$NON-NLS-1$
		, Container.class
		, new TypeReference<Container>() {}
		, new Property.Accessor<Container, Container>() { private static final long serialVersionUID = 1L;
			@Override
			public Container get(Container model) {
				return model.getTopContainer();
			}
			@Override
			public void set(Container model, Container value) {
				model.setTopContainer(value);
			}
		});

	public static final Property<Collection<Comment>, Container> COMMENT_COLLECTION = Property.create(
		"commentCollection" //$NON-NLS-1$
		, Container.class
		, new TypeReference<Collection<Comment>>() {}
		, new Property.Accessor<Collection<Comment>, Container>() { private static final long serialVersionUID = 1L;
			@Override
			public Collection<Comment> get(Container model) {
				return model.getComments();
			}
			@Override
			public void set(Container model, Collection<Comment> value) {
				model.getComments().clear();
				model.getComments().addAll(value);
			}
		});

	public static final Property<ActivityStatus, Container> ACTIVITY_STATUS = Property.create(
		"activityStatus" //$NON-NLS-1$
		, Container.class
		, new TypeReference<ActivityStatus>() {}
		, new Property.Accessor<ActivityStatus, Container>() { private static final long serialVersionUID = 1L;
			@Override
			public ActivityStatus get(Container model) {
				return model.getActivityStatus();
			}
			@Override
			public void set(Container model, ActivityStatus value) {
				model.setActivityStatus(value);
			}
		});

   public static final List<Property<?, ? super Container>> PROPERTIES;
   static {
      List<Property<?, ? super Container>> aList = new ArrayList<Property<?, ? super Container>>();
      aList.add(ID);
      aList.add(PRODUCT_BARCODE);
      aList.add(PATH);
      aList.add(LABEL);
      aList.add(TEMPERATURE);
      aList.add(SITE);
      aList.add(POSITION);
      aList.add(CONTAINER_TYPE);
      aList.add(SPECIMEN_POSITION_COLLECTION);
      aList.add(CHILD_POSITION_COLLECTION);
      aList.add(TOP_CONTAINER);
      aList.add(COMMENT_COLLECTION);
      aList.add(ACTIVITY_STATUS);
      PROPERTIES = Collections.unmodifiableList(aList);
   };
}
