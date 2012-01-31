package edu.ualberta.med.biobank.common.peer;

import edu.ualberta.med.biobank.common.util.TypeReference;
import java.util.Collections;
import edu.ualberta.med.biobank.common.wrappers.Property;
import java.util.List;
import java.util.ArrayList;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.EventAttr;
import edu.ualberta.med.biobank.model.Comment;
import edu.ualberta.med.biobank.model.ActivityStatus;
import java.util.Collection;
import edu.ualberta.med.biobank.model.CollectionEvent;

public class CollectionEventPeer {
	public static final Property<Integer, CollectionEvent> ID = Property.create(
		"id" //$NON-NLS-1$
		, CollectionEvent.class
		, new TypeReference<Integer>() {}
		, new Property.Accessor<Integer, CollectionEvent>() { private static final long serialVersionUID = 1L;
			@Override
			public Integer get(CollectionEvent model) {
				return model.getId();
			}
			@Override
			public void set(CollectionEvent model, Integer value) {
				model.setId(value);
			}
		});

	public static final Property<Integer, CollectionEvent> VISIT_NUMBER = Property.create(
		"visitNumber" //$NON-NLS-1$
		, CollectionEvent.class
		, new TypeReference<Integer>() {}
		, new Property.Accessor<Integer, CollectionEvent>() { private static final long serialVersionUID = 1L;
			@Override
			public Integer get(CollectionEvent model) {
				return model.getVisitNumber();
			}
			@Override
			public void set(CollectionEvent model, Integer value) {
				model.setVisitNumber(value);
			}
		});

	public static final Property<Collection<Specimen>, CollectionEvent> ALL_SPECIMEN_COLLECTION = Property.create(
		"allSpecimenCollection" //$NON-NLS-1$
		, CollectionEvent.class
		, new TypeReference<Collection<Specimen>>() {}
		, new Property.Accessor<Collection<Specimen>, CollectionEvent>() { private static final long serialVersionUID = 1L;
			@Override
			public Collection<Specimen> get(CollectionEvent model) {
				return model.getAllSpecimenCollection();
			}
			@Override
			public void set(CollectionEvent model, Collection<Specimen> value) {
				model.setAllSpecimenCollection(value);
			}
		});

	public static final Property<Patient, CollectionEvent> PATIENT = Property.create(
		"patient" //$NON-NLS-1$
		, CollectionEvent.class
		, new TypeReference<Patient>() {}
		, new Property.Accessor<Patient, CollectionEvent>() { private static final long serialVersionUID = 1L;
			@Override
			public Patient get(CollectionEvent model) {
				return model.getPatient();
			}
			@Override
			public void set(CollectionEvent model, Patient value) {
				model.setPatient(value);
			}
		});

	public static final Property<Collection<EventAttr>, CollectionEvent> EVENT_ATTR_COLLECTION = Property.create(
		"eventAttrCollection" //$NON-NLS-1$
		, CollectionEvent.class
		, new TypeReference<Collection<EventAttr>>() {}
		, new Property.Accessor<Collection<EventAttr>, CollectionEvent>() { private static final long serialVersionUID = 1L;
			@Override
			public Collection<EventAttr> get(CollectionEvent model) {
				return model.getEventAttrCollection();
			}
			@Override
			public void set(CollectionEvent model, Collection<EventAttr> value) {
				model.setEventAttrCollection(value);
			}
		});

	public static final Property<Collection<Comment>, CollectionEvent> COMMENT_COLLECTION = Property.create(
		"commentCollection" //$NON-NLS-1$
		, CollectionEvent.class
		, new TypeReference<Collection<Comment>>() {}
		, new Property.Accessor<Collection<Comment>, CollectionEvent>() { private static final long serialVersionUID = 1L;
			@Override
			public Collection<Comment> get(CollectionEvent model) {
				return model.getCommentCollection();
			}
			@Override
			public void set(CollectionEvent model, Collection<Comment> value) {
				model.getCommentCollection().clear();
				model.getCommentCollection().addAll(value);
			}
		});

	public static final Property<ActivityStatus, CollectionEvent> ACTIVITY_STATUS = Property.create(
		"activityStatus" //$NON-NLS-1$
		, CollectionEvent.class
		, new TypeReference<ActivityStatus>() {}
		, new Property.Accessor<ActivityStatus, CollectionEvent>() { private static final long serialVersionUID = 1L;
			@Override
			public ActivityStatus get(CollectionEvent model) {
				return model.getActivityStatus();
			}
			@Override
			public void set(CollectionEvent model, ActivityStatus value) {
				model.setActivityStatus(value);
			}
		});

	public static final Property<Collection<Specimen>, CollectionEvent> ORIGINAL_SPECIMEN_COLLECTION = Property.create(
		"originalSpecimenCollection" //$NON-NLS-1$
		, CollectionEvent.class
		, new TypeReference<Collection<Specimen>>() {}
		, new Property.Accessor<Collection<Specimen>, CollectionEvent>() { private static final long serialVersionUID = 1L;
			@Override
			public Collection<Specimen> get(CollectionEvent model) {
				return model.getOriginalSpecimenCollection();
			}
			@Override
			public void set(CollectionEvent model, Collection<Specimen> value) {
				model.setOriginalSpecimenCollection(value);
			}
		});

   public static final List<Property<?, ? super CollectionEvent>> PROPERTIES;
   static {
      List<Property<?, ? super CollectionEvent>> aList = new ArrayList<Property<?, ? super CollectionEvent>>();
      aList.add(ID);
      aList.add(VISIT_NUMBER);
      aList.add(ALL_SPECIMEN_COLLECTION);
      aList.add(PATIENT);
      aList.add(EVENT_ATTR_COLLECTION);
      aList.add(COMMENT_COLLECTION);
      aList.add(ACTIVITY_STATUS);
      aList.add(ORIGINAL_SPECIMEN_COLLECTION);
      PROPERTIES = Collections.unmodifiableList(aList);
   };
}
