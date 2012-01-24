package edu.ualberta.med.biobank.common.peer;

import edu.ualberta.med.biobank.common.util.TypeReference;
import java.util.Collections;
import edu.ualberta.med.biobank.common.wrappers.Property;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import edu.ualberta.med.biobank.model.ResearchGroup;
import edu.ualberta.med.biobank.model.Address;
import edu.ualberta.med.biobank.model.Dispatch;
import edu.ualberta.med.biobank.model.RequestSpecimen;
import java.util.Collection;
import edu.ualberta.med.biobank.model.Request;

public class RequestPeer {
	public static final Property<Integer, Request> ID = Property.create(
		"id" //$NON-NLS-1$
		, Request.class
		, new TypeReference<Integer>() {}
		, new Property.Accessor<Integer, Request>() { private static final long serialVersionUID = 1L;
			@Override
			public Integer get(Request model) {
				return model.getId();
			}
			@Override
			public void set(Request model, Integer value) {
				model.setId(value);
			}
		});

	public static final Property<Date, Request> CREATED = Property.create(
		"created" //$NON-NLS-1$
		, Request.class
		, new TypeReference<Date>() {}
		, new Property.Accessor<Date, Request>() { private static final long serialVersionUID = 1L;
			@Override
			public Date get(Request model) {
				return model.getCreated();
			}
			@Override
			public void set(Request model, Date value) {
				model.setCreated(value);
			}
		});

	public static final Property<Date, Request> SUBMITTED = Property.create(
		"submitted" //$NON-NLS-1$
		, Request.class
		, new TypeReference<Date>() {}
		, new Property.Accessor<Date, Request>() { private static final long serialVersionUID = 1L;
			@Override
			public Date get(Request model) {
				return model.getSubmitted();
			}
			@Override
			public void set(Request model, Date value) {
				model.setSubmitted(value);
			}
		});

	public static final Property<ResearchGroup, Request> RESEARCH_GROUP = Property.create(
		"researchGroup" //$NON-NLS-1$
		, Request.class
		, new TypeReference<ResearchGroup>() {}
		, new Property.Accessor<ResearchGroup, Request>() { private static final long serialVersionUID = 1L;
			@Override
			public ResearchGroup get(Request model) {
				return model.getResearchGroup();
			}
			@Override
			public void set(Request model, ResearchGroup value) {
				model.setResearchGroup(value);
			}
		});

	public static final Property<Address, Request> ADDRESS = Property.create(
		"address" //$NON-NLS-1$
		, Request.class
		, new TypeReference<Address>() {}
		, new Property.Accessor<Address, Request>() { private static final long serialVersionUID = 1L;
			@Override
			public Address get(Request model) {
				return model.getAddress();
			}
			@Override
			public void set(Request model, Address value) {
				model.setAddress(value);
			}
		});

	public static final Property<Collection<Dispatch>, Request> DISPATCH_COLLECTION = Property.create(
		"dispatchCollection" //$NON-NLS-1$
		, Request.class
		, new TypeReference<Collection<Dispatch>>() {}
		, new Property.Accessor<Collection<Dispatch>, Request>() { private static final long serialVersionUID = 1L;
			@Override
			public Collection<Dispatch> get(Request model) {
				return model.getDispatchCollection();
			}
			@Override
			public void set(Request model, Collection<Dispatch> value) {
				model.setDispatchCollection(value);
			}
		});

	public static final Property<Collection<RequestSpecimen>, Request> REQUEST_SPECIMEN_COLLECTION = Property.create(
		"requestSpecimenCollection" //$NON-NLS-1$
		, Request.class
		, new TypeReference<Collection<RequestSpecimen>>() {}
		, new Property.Accessor<Collection<RequestSpecimen>, Request>() { private static final long serialVersionUID = 1L;
			@Override
			public Collection<RequestSpecimen> get(Request model) {
				return model.getRequestSpecimenCollection();
			}
			@Override
			public void set(Request model, Collection<RequestSpecimen> value) {
				model.setRequestSpecimenCollection(value);
			}
		});

   public static final List<Property<?, ? super Request>> PROPERTIES;
   static {
      List<Property<?, ? super Request>> aList = new ArrayList<Property<?, ? super Request>>();
      aList.add(ID);
      aList.add(CREATED);
      aList.add(SUBMITTED);
      aList.add(RESEARCH_GROUP);
      aList.add(ADDRESS);
      aList.add(DISPATCH_COLLECTION);
      aList.add(REQUEST_SPECIMEN_COLLECTION);
      PROPERTIES = Collections.unmodifiableList(aList);
   };
}
