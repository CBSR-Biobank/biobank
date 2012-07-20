package edu.ualberta.med.biobank.common.peer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import edu.ualberta.med.biobank.common.util.TypeReference;
import edu.ualberta.med.biobank.common.wrappers.Property;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.Center;
import edu.ualberta.med.biobank.model.Comment;
import edu.ualberta.med.biobank.model.ProcessingEvent;
import edu.ualberta.med.biobank.model.Specimen;

public class ProcessingEventPeer {
	public static final Property<Integer, ProcessingEvent> ID = Property.create(
		"id" //$NON-NLS-1$
		, ProcessingEvent.class
		, new TypeReference<Integer>() {}
		, new Property.Accessor<Integer, ProcessingEvent>() { private static final long serialVersionUID = 1L;
			@Override
			public Integer get(ProcessingEvent model) {
				return model.getId();
			}
			@Override
			public void set(ProcessingEvent model, Integer value) {
				model.setId(value);
			}
		});

	public static final Property<Date, ProcessingEvent> CREATED_AT = Property.create(
		"createdAt" //$NON-NLS-1$
		, ProcessingEvent.class
		, new TypeReference<Date>() {}
		, new Property.Accessor<Date, ProcessingEvent>() { private static final long serialVersionUID = 1L;
			@Override
			public Date get(ProcessingEvent model) {
				return model.getCreatedAt();
			}
			@Override
			public void set(ProcessingEvent model, Date value) {
				model.setCreatedAt(value);
			}
		});

	public static final Property<String, ProcessingEvent> WORKSHEET = Property.create(
		"worksheet" //$NON-NLS-1$
		, ProcessingEvent.class
		, new TypeReference<String>() {}
		, new Property.Accessor<String, ProcessingEvent>() { private static final long serialVersionUID = 1L;
			@Override
			public String get(ProcessingEvent model) {
				return model.getWorksheet();
			}
			@Override
			public void set(ProcessingEvent model, String value) {
				model.setWorksheet(value);
			}
		});

	public static final Property<Center, ProcessingEvent> CENTER = Property.create(
		"center" //$NON-NLS-1$
		, ProcessingEvent.class
		, new TypeReference<Center>() {}
		, new Property.Accessor<Center, ProcessingEvent>() { private static final long serialVersionUID = 1L;
			@Override
			public Center get(ProcessingEvent model) {
				return model.getCenter();
			}
			@Override
			public void set(ProcessingEvent model, Center value) {
				model.setCenter(value);
			}
		});

	public static final Property<Collection<Comment>, ProcessingEvent> COMMENTS = Property.create(
		"comments" //$NON-NLS-1$
		, ProcessingEvent.class
		, new TypeReference<Collection<Comment>>() {}
		, new Property.Accessor<Collection<Comment>, ProcessingEvent>() { private static final long serialVersionUID = 1L;
			@Override
			public Collection<Comment> get(ProcessingEvent model) {
				return model.getComments();
			}
			@Override
			public void set(ProcessingEvent model, Collection<Comment> value) {
				model.getComments().clear();
				model.getComments().addAll(value);
			}
		});

	public static final Property<ActivityStatus, ProcessingEvent> ACTIVITY_STATUS = Property.create(
		"activityStatus" //$NON-NLS-1$
		, ProcessingEvent.class
		, new TypeReference<ActivityStatus>() {}
		, new Property.Accessor<ActivityStatus, ProcessingEvent>() { private static final long serialVersionUID = 1L;
			@Override
			public ActivityStatus get(ProcessingEvent model) {
				return model.getActivityStatus();
			}
			@Override
			public void set(ProcessingEvent model, ActivityStatus value) {
				model.setActivityStatus(value);
			}
		});

	public static final Property<Collection<Specimen>, ProcessingEvent> SPECIMENS = Property.create(
		"specimens" //$NON-NLS-1$
		, ProcessingEvent.class
		, new TypeReference<Collection<Specimen>>() {}
		, new Property.Accessor<Collection<Specimen>, ProcessingEvent>() { private static final long serialVersionUID = 1L;
			@Override
			public Collection<Specimen> get(ProcessingEvent model) {
				return model.getSpecimens();
			}
			@Override
			public void set(ProcessingEvent model, Collection<Specimen> value) {
				model.getSpecimens().clear();
				model.getSpecimens().addAll(value);
			}
		});

   public static final List<Property<?, ? super ProcessingEvent>> PROPERTIES;
   static {
      List<Property<?, ? super ProcessingEvent>> aList = new ArrayList<Property<?, ? super ProcessingEvent>>();
      aList.add(ID);
      aList.add(CREATED_AT);
      aList.add(WORKSHEET);
      aList.add(CENTER);
      aList.add(COMMENTS);
      aList.add(ACTIVITY_STATUS);
      aList.add(SPECIMENS);
      PROPERTIES = Collections.unmodifiableList(aList);
   };
}
