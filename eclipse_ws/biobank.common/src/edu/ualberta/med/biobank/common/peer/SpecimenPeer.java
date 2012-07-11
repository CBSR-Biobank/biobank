package edu.ualberta.med.biobank.common.peer;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import edu.ualberta.med.biobank.common.util.TypeReference;
import edu.ualberta.med.biobank.common.wrappers.Property;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.Center;
import edu.ualberta.med.biobank.model.CollectionEvent;
import edu.ualberta.med.biobank.model.Comment;
import edu.ualberta.med.biobank.model.DispatchSpecimen;
import edu.ualberta.med.biobank.model.OriginInfo;
import edu.ualberta.med.biobank.model.ProcessingEvent;
import edu.ualberta.med.biobank.model.RequestSpecimen;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.model.SpecimenPosition;
import edu.ualberta.med.biobank.model.SpecimenType;

public class SpecimenPeer {
	public static final Property<Integer, Specimen> ID = Property.create(
		"id" //$NON-NLS-1$
		, Specimen.class
		, new TypeReference<Integer>() {}
		, new Property.Accessor<Integer, Specimen>() { private static final long serialVersionUID = 1L;
			@Override
			public Integer get(Specimen model) {
				return model.getId();
			}
			@Override
			public void set(Specimen model, Integer value) {
				model.setId(value);
			}
		});

	public static final Property<Date, Specimen> CREATED_AT = Property.create(
		"createdAt" //$NON-NLS-1$
		, Specimen.class
		, new TypeReference<Date>() {}
		, new Property.Accessor<Date, Specimen>() { private static final long serialVersionUID = 1L;
			@Override
			public Date get(Specimen model) {
				return model.getCreatedAt();
			}
			@Override
			public void set(Specimen model, Date value) {
				model.setCreatedAt(value);
			}
		});

	public static final Property<String, Specimen> INVENTORY_ID = Property.create(
		"inventoryId" //$NON-NLS-1$
		, Specimen.class
		, new TypeReference<String>() {}
		, new Property.Accessor<String, Specimen>() { private static final long serialVersionUID = 1L;
			@Override
			public String get(Specimen model) {
				return model.getInventoryId();
			}
			@Override
			public void set(Specimen model, String value) {
				model.setInventoryId(value);
			}
		});

	public static final Property<BigDecimal, Specimen> QUANTITY = Property.create(
		"quantity" //$NON-NLS-1$
		, Specimen.class
		, new TypeReference<BigDecimal>() {}
		, new Property.Accessor<BigDecimal, Specimen>() { private static final long serialVersionUID = 1L;
			@Override
			public BigDecimal get(Specimen model) {
				return model.getQuantity();
			}
			@Override
			public void set(Specimen model, BigDecimal value) {
				model.setQuantity(value);
			}
		});

	public static final Property<ProcessingEvent, Specimen> PROCESSING_EVENT = Property.create(
		"processingEvent" //$NON-NLS-1$
		, Specimen.class
		, new TypeReference<ProcessingEvent>() {}
		, new Property.Accessor<ProcessingEvent, Specimen>() { private static final long serialVersionUID = 1L;
			@Override
			public ProcessingEvent get(Specimen model) {
				return model.getProcessingEvent();
			}
			@Override
			public void set(Specimen model, ProcessingEvent value) {
				model.setProcessingEvent(value);
			}
		});

	public static final Property<Collection<Comment>, Specimen> COMMENTS = Property.create(
		"comments" //$NON-NLS-1$
		, Specimen.class
		, new TypeReference<Collection<Comment>>() {}
		, new Property.Accessor<Collection<Comment>, Specimen>() { private static final long serialVersionUID = 1L;
			@Override
			public Collection<Comment> get(Specimen model) {
				return model.getComments();
			}
			@Override
			public void set(Specimen model, Collection<Comment> value) {
				model.getComments().clear();
				model.getComments().addAll(value);
			}
		});

	public static final Property<OriginInfo, Specimen> ORIGIN_INFO = Property.create(
		"originInfo" //$NON-NLS-1$
		, Specimen.class
		, new TypeReference<OriginInfo>() {}
		, new Property.Accessor<OriginInfo, Specimen>() { private static final long serialVersionUID = 1L;
			@Override
			public OriginInfo get(Specimen model) {
				return model.getOriginInfo();
			}
			@Override
			public void set(Specimen model, OriginInfo value) {
				model.setOriginInfo(value);
			}
		});

	public static final Property<Specimen, Specimen> TOP_SPECIMEN = Property.create(
		"topSpecimen" //$NON-NLS-1$
		, Specimen.class
		, new TypeReference<Specimen>() {}
		, new Property.Accessor<Specimen, Specimen>() { private static final long serialVersionUID = 1L;
			@Override
			public Specimen get(Specimen model) {
				return model.getTopSpecimen();
			}
			@Override
			public void set(Specimen model, Specimen value) {
				model.setTopSpecimen(value);
			}
		});

	public static final Property<CollectionEvent, Specimen> COLLECTION_EVENT = Property.create(
		"collectionEvent" //$NON-NLS-1$
		, Specimen.class
		, new TypeReference<CollectionEvent>() {}
		, new Property.Accessor<CollectionEvent, Specimen>() { private static final long serialVersionUID = 1L;
			@Override
			public CollectionEvent get(Specimen model) {
				return model.getCollectionEvent();
			}
			@Override
			public void set(Specimen model, CollectionEvent value) {
				model.setCollectionEvent(value);
			}
		});

	public static final Property<CollectionEvent, Specimen> ORIGINAL_COLLECTION_EVENT = Property.create(
		"originalCollectionEvent" //$NON-NLS-1$
		, Specimen.class
		, new TypeReference<CollectionEvent>() {}
		, new Property.Accessor<CollectionEvent, Specimen>() { private static final long serialVersionUID = 1L;
			@Override
			public CollectionEvent get(Specimen model) {
				return model.getOriginalCollectionEvent();
			}
			@Override
			public void set(Specimen model, CollectionEvent value) {
				model.setOriginalCollectionEvent(value);
			}
		});

	public static final Property<Specimen, Specimen> PARENT_SPECIMEN = Property.create(
		"parentSpecimen" //$NON-NLS-1$
		, Specimen.class
		, new TypeReference<Specimen>() {}
		, new Property.Accessor<Specimen, Specimen>() { private static final long serialVersionUID = 1L;
			@Override
			public Specimen get(Specimen model) {
				return model.getParentSpecimen();
			}
			@Override
			public void set(Specimen model, Specimen value) {
				model.setParentSpecimen(value);
			}
		});

	public static final Property<Collection<Specimen>, Specimen> CHILD_SPECIMENS = Property.create(
		"childSpecimens" //$NON-NLS-1$
		, Specimen.class
		, new TypeReference<Collection<Specimen>>() {}
		, new Property.Accessor<Collection<Specimen>, Specimen>() { private static final long serialVersionUID = 1L;
			@Override
			public Collection<Specimen> get(Specimen model) {
				return model.getChildSpecimens();
			}
			@Override
			public void set(Specimen model, Collection<Specimen> value) {
				model.getChildSpecimens().clear();
				model.getChildSpecimens().addAll(value);
			}
		});

	public static final Property<Collection<DispatchSpecimen>, Specimen> DISPATCH_SPECIMENS = Property.create(
		"dispatchSpecimens" //$NON-NLS-1$
		, Specimen.class
		, new TypeReference<Collection<DispatchSpecimen>>() {}
		, new Property.Accessor<Collection<DispatchSpecimen>, Specimen>() { private static final long serialVersionUID = 1L;
			@Override
			public Collection<DispatchSpecimen> get(Specimen model) {
				return model.getDispatchSpecimens();
			}
			@Override
			public void set(Specimen model, Collection<DispatchSpecimen> value) {
				model.getDispatchSpecimens().clear();
				model.getDispatchSpecimens().addAll(value);
			}
		});

	public static final Property<Center, Specimen> CURRENT_CENTER = Property.create(
		"currentCenter" //$NON-NLS-1$
		, Specimen.class
		, new TypeReference<Center>() {}
		, new Property.Accessor<Center, Specimen>() { private static final long serialVersionUID = 1L;
			@Override
			public Center get(Specimen model) {
				return model.getCurrentCenter();
			}
			@Override
			public void set(Specimen model, Center value) {
				model.setCurrentCenter(value);
			}
		});

	public static final Property<SpecimenPosition, Specimen> SPECIMEN_POSITION = Property.create(
		"specimenPosition" //$NON-NLS-1$
		, Specimen.class
		, new TypeReference<SpecimenPosition>() {}
		, new Property.Accessor<SpecimenPosition, Specimen>() { private static final long serialVersionUID = 1L;
			@Override
			public SpecimenPosition get(Specimen model) {
				return model.getSpecimenPosition();
			}
			@Override
			public void set(Specimen model, SpecimenPosition value) {
				model.setSpecimenPosition(value);
			}
		});

	public static final Property<ActivityStatus, Specimen> ACTIVITY_STATUS = Property.create(
		"activityStatus" //$NON-NLS-1$
		, Specimen.class
		, new TypeReference<ActivityStatus>() {}
		, new Property.Accessor<ActivityStatus, Specimen>() { private static final long serialVersionUID = 1L;
			@Override
			public ActivityStatus get(Specimen model) {
				return model.getActivityStatus();
			}
			@Override
			public void set(Specimen model, ActivityStatus value) {
				model.setActivityStatus(value);
			}
		});

	public static final Property<Collection<RequestSpecimen>, Specimen> REQUEST_SPECIMENS = Property.create(
		"requestSpecimens" //$NON-NLS-1$
		, Specimen.class
		, new TypeReference<Collection<RequestSpecimen>>() {}
		, new Property.Accessor<Collection<RequestSpecimen>, Specimen>() { private static final long serialVersionUID = 1L;
			@Override
			public Collection<RequestSpecimen> get(Specimen model) {
				return model.getRequestSpecimens();
			}
			@Override
			public void set(Specimen model, Collection<RequestSpecimen> value) {
				model.getRequestSpecimens().clear();
				model.getRequestSpecimens().addAll(value);
			}
		});

	public static final Property<SpecimenType, Specimen> SPECIMEN_TYPE = Property.create(
		"specimenType" //$NON-NLS-1$
		, Specimen.class
		, new TypeReference<SpecimenType>() {}
		, new Property.Accessor<SpecimenType, Specimen>() { private static final long serialVersionUID = 1L;
			@Override
			public SpecimenType get(Specimen model) {
				return model.getSpecimenType();
			}
			@Override
			public void set(Specimen model, SpecimenType value) {
				model.setSpecimenType(value);
			}
		});

   public static final List<Property<?, ? super Specimen>> PROPERTIES;
   static {
      List<Property<?, ? super Specimen>> aList = new ArrayList<Property<?, ? super Specimen>>();
      aList.add(ID);
      aList.add(CREATED_AT);
      aList.add(INVENTORY_ID);
      aList.add(QUANTITY);
      aList.add(PROCESSING_EVENT);
      aList.add(COMMENTS);
      aList.add(ORIGIN_INFO);
      aList.add(TOP_SPECIMEN);
      aList.add(COLLECTION_EVENT);
      aList.add(ORIGINAL_COLLECTION_EVENT);
      aList.add(PARENT_SPECIMEN);
      aList.add(CHILD_SPECIMENS);
      aList.add(DISPATCH_SPECIMENS);
      aList.add(CURRENT_CENTER);
      aList.add(SPECIMEN_POSITION);
      aList.add(ACTIVITY_STATUS);
      aList.add(REQUEST_SPECIMENS);
      aList.add(SPECIMEN_TYPE);
      PROPERTIES = Collections.unmodifiableList(aList);
   };
}
