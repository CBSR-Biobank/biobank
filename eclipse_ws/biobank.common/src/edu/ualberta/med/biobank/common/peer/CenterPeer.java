package edu.ualberta.med.biobank.common.peer;

import edu.ualberta.med.biobank.common.util.TypeReference;
import java.util.Collections;
import edu.ualberta.med.biobank.common.wrappers.Property;
import java.util.List;
import java.util.ArrayList;
import edu.ualberta.med.biobank.model.Dispatch;
import edu.ualberta.med.biobank.model.Comment;
import edu.ualberta.med.biobank.model.Address;
import edu.ualberta.med.biobank.model.ProcessingEvent;
import edu.ualberta.med.biobank.model.Membership;
import edu.ualberta.med.biobank.model.OriginInfo;
import edu.ualberta.med.biobank.model.ActivityStatus;
import java.util.Collection;
import edu.ualberta.med.biobank.model.Center;

public class CenterPeer {
	public static final Property<Integer, Center> ID = Property.create(
		"id" //$NON-NLS-1$
		, Center.class
		, new TypeReference<Integer>() {}
		, new Property.Accessor<Integer, Center>() { private static final long serialVersionUID = 1L;
			@Override
			public Integer get(Center model) {
				return model.getId();
			}
			@Override
			public void set(Center model, Integer value) {
				model.setId(value);
			}
		});

	public static final Property<String, Center> NAME = Property.create(
		"name" //$NON-NLS-1$
		, Center.class
		, new TypeReference<String>() {}
		, new Property.Accessor<String, Center>() { private static final long serialVersionUID = 1L;
			@Override
			public String get(Center model) {
				return model.getName();
			}
			@Override
			public void set(Center model, String value) {
				model.setName(value);
			}
		});

	public static final Property<String, Center> NAME_SHORT = Property.create(
		"nameShort" //$NON-NLS-1$
		, Center.class
		, new TypeReference<String>() {}
		, new Property.Accessor<String, Center>() { private static final long serialVersionUID = 1L;
			@Override
			public String get(Center model) {
				return model.getNameShort();
			}
			@Override
			public void set(Center model, String value) {
				model.setNameShort(value);
			}
		});

	public static final Property<Collection<Dispatch>, Center> DST_DISPATCH_COLLECTION = Property.create(
		"dstDispatchCollection" //$NON-NLS-1$
		, Center.class
		, new TypeReference<Collection<Dispatch>>() {}
		, new Property.Accessor<Collection<Dispatch>, Center>() { private static final long serialVersionUID = 1L;
			@Override
			public Collection<Dispatch> get(Center model) {
				return model.getDstDispatches();
			}
			@Override
			public void set(Center model, Collection<Dispatch> value) {
				model.getDstDispatches().clear();
				model.getDstDispatches().addAll(value);
			}
		});

	public static final Property<Collection<Dispatch>, Center> SRC_DISPATCH_COLLECTION = Property.create(
		"srcDispatchCollection" //$NON-NLS-1$
		, Center.class
		, new TypeReference<Collection<Dispatch>>() {}
		, new Property.Accessor<Collection<Dispatch>, Center>() { private static final long serialVersionUID = 1L;
			@Override
			public Collection<Dispatch> get(Center model) {
				return model.getSrcDispatches();
			}
			@Override
			public void set(Center model, Collection<Dispatch> value) {
				model.getSrcDispatches().clear();
				model.getSrcDispatches().addAll(value);
			}
		});

	public static final Property<Collection<Comment>, Center> COMMENT_COLLECTION = Property.create(
		"commentCollection" //$NON-NLS-1$
		, Center.class
		, new TypeReference<Collection<Comment>>() {}
		, new Property.Accessor<Collection<Comment>, Center>() { private static final long serialVersionUID = 1L;
			@Override
			public Collection<Comment> get(Center model) {
				return model.getComments();
			}
			@Override
			public void set(Center model, Collection<Comment> value) {
				model.getComments().clear();
				model.getComments().addAll(value);
			}
		});

	public static final Property<Address, Center> ADDRESS = Property.create(
		"address" //$NON-NLS-1$
		, Center.class
		, new TypeReference<Address>() {}
		, new Property.Accessor<Address, Center>() { private static final long serialVersionUID = 1L;
			@Override
			public Address get(Center model) {
				return model.getAddress();
			}
			@Override
			public void set(Center model, Address value) {
				model.setAddress(value);
			}
		});

	public static final Property<Collection<ProcessingEvent>, Center> PROCESSING_EVENT_COLLECTION = Property.create(
		"processingEventCollection" //$NON-NLS-1$
		, Center.class
		, new TypeReference<Collection<ProcessingEvent>>() {}
		, new Property.Accessor<Collection<ProcessingEvent>, Center>() { private static final long serialVersionUID = 1L;
			@Override
			public Collection<ProcessingEvent> get(Center model) {
				return model.getProcessingEvents();
			}
			@Override
			public void set(Center model, Collection<ProcessingEvent> value) {
				model.getProcessingEvents().clear();
				model.getProcessingEvents().addAll(value);
			}
		});

	public static final Property<Collection<Membership>, Center> MEMBERSHIP_COLLECTION = Property.create(
		"membershipCollection" //$NON-NLS-1$
		, Center.class
		, new TypeReference<Collection<Membership>>() {}
		, new Property.Accessor<Collection<Membership>, Center>() { private static final long serialVersionUID = 1L;
			@Override
			public Collection<Membership> get(Center model) {
				return model.getMemberships();
			}
			@Override
			public void set(Center model, Collection<Membership> value) {
				model.getMemberships().clear();
				model.getMemberships().addAll(value);
			}
		});

	public static final Property<Collection<OriginInfo>, Center> ORIGIN_INFO_COLLECTION = Property.create(
		"originInfoCollection" //$NON-NLS-1$
		, Center.class
		, new TypeReference<Collection<OriginInfo>>() {}
		, new Property.Accessor<Collection<OriginInfo>, Center>() { private static final long serialVersionUID = 1L;
			@Override
			public Collection<OriginInfo> get(Center model) {
				return model.getOriginInfos();
			}
			@Override
			public void set(Center model, Collection<OriginInfo> value) {
				model.getOriginInfos().clear();
				model.getOriginInfos().addAll(value);
			}
		});

	public static final Property<ActivityStatus, Center> ACTIVITY_STATUS = Property.create(
		"activityStatus" //$NON-NLS-1$
		, Center.class
		, new TypeReference<ActivityStatus>() {}
		, new Property.Accessor<ActivityStatus, Center>() { private static final long serialVersionUID = 1L;
			@Override
			public ActivityStatus get(Center model) {
				return model.getActivityStatus();
			}
			@Override
			public void set(Center model, ActivityStatus value) {
				model.setActivityStatus(value);
			}
		});

   public static final List<Property<?, ? super Center>> PROPERTIES;
   static {
      List<Property<?, ? super Center>> aList = new ArrayList<Property<?, ? super Center>>();
      aList.add(ID);
      aList.add(NAME);
      aList.add(NAME_SHORT);
      aList.add(DST_DISPATCH_COLLECTION);
      aList.add(SRC_DISPATCH_COLLECTION);
      aList.add(COMMENT_COLLECTION);
      aList.add(ADDRESS);
      aList.add(PROCESSING_EVENT_COLLECTION);
      aList.add(MEMBERSHIP_COLLECTION);
      aList.add(ORIGIN_INFO_COLLECTION);
      aList.add(ACTIVITY_STATUS);
      PROPERTIES = Collections.unmodifiableList(aList);
   };
}
