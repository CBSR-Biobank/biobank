package edu.ualberta.med.biobank.common.peer;

import edu.ualberta.med.biobank.common.util.TypeReference;
import java.util.Collections;
import edu.ualberta.med.biobank.common.wrappers.Property;
import java.util.List;
import java.util.ArrayList;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.model.Request;
import edu.ualberta.med.biobank.model.RequestSpecimen;
import edu.ualberta.med.biobank.model.type.RequestSpecimenState;

public class RequestSpecimenPeer {
	public static final Property<Integer, RequestSpecimen> ID = Property.create(
		"id" //$NON-NLS-1$
		, RequestSpecimen.class
		, new TypeReference<Integer>() {}
		, new Property.Accessor<Integer, RequestSpecimen>() { private static final long serialVersionUID = 1L;
			@Override
			public Integer get(RequestSpecimen model) {
				return model.getId();
			}
			@Override
			public void set(RequestSpecimen model, Integer value) {
				model.setId(value);
			}
		});

	public static final Property<RequestSpecimenState, RequestSpecimen> STATE = Property.create(
		"state" //$NON-NLS-1$
		, RequestSpecimen.class
		, new TypeReference<RequestSpecimenState>() {}
		, new Property.Accessor<RequestSpecimenState, RequestSpecimen>() { private static final long serialVersionUID = 1L;
			@Override
			public RequestSpecimenState get(RequestSpecimen model) {
				return model.getState();
			}
			@Override
			public void set(RequestSpecimen model, RequestSpecimenState value) {
				model.setState(value);
			}
		});

	public static final Property<String, RequestSpecimen> CLAIMED_BY = Property.create(
		"claimedBy" //$NON-NLS-1$
		, RequestSpecimen.class
		, new TypeReference<String>() {}
		, new Property.Accessor<String, RequestSpecimen>() { private static final long serialVersionUID = 1L;
			@Override
			public String get(RequestSpecimen model) {
				return model.getClaimedBy();
			}
			@Override
			public void set(RequestSpecimen model, String value) {
				model.setClaimedBy(value);
			}
		});

	public static final Property<Specimen, RequestSpecimen> SPECIMEN = Property.create(
		"specimen" //$NON-NLS-1$
		, RequestSpecimen.class
		, new TypeReference<Specimen>() {}
		, new Property.Accessor<Specimen, RequestSpecimen>() { private static final long serialVersionUID = 1L;
			@Override
			public Specimen get(RequestSpecimen model) {
				return model.getSpecimen();
			}
			@Override
			public void set(RequestSpecimen model, Specimen value) {
				model.setSpecimen(value);
			}
		});

	public static final Property<Request, RequestSpecimen> REQUEST = Property.create(
		"request" //$NON-NLS-1$
		, RequestSpecimen.class
		, new TypeReference<Request>() {}
		, new Property.Accessor<Request, RequestSpecimen>() { private static final long serialVersionUID = 1L;
			@Override
			public Request get(RequestSpecimen model) {
				return model.getRequest();
			}
			@Override
			public void set(RequestSpecimen model, Request value) {
				model.setRequest(value);
			}
		});

   public static final List<Property<?, ? super RequestSpecimen>> PROPERTIES;
   static {
      List<Property<?, ? super RequestSpecimen>> aList = new ArrayList<Property<?, ? super RequestSpecimen>>();
      aList.add(ID);
      aList.add(STATE);
      aList.add(CLAIMED_BY);
      aList.add(SPECIMEN);
      aList.add(REQUEST);
      PROPERTIES = Collections.unmodifiableList(aList);
   };
}
