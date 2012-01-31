package edu.ualberta.med.biobank.common.peer;

import edu.ualberta.med.biobank.common.util.TypeReference;
import java.util.Collections;
import edu.ualberta.med.biobank.common.wrappers.Property;
import java.util.List;
import java.util.ArrayList;
import edu.ualberta.med.biobank.model.ShipmentInfo;
import edu.ualberta.med.biobank.model.Comment;
import edu.ualberta.med.biobank.model.Center;
import edu.ualberta.med.biobank.model.DispatchSpecimen;
import java.util.Collection;
import edu.ualberta.med.biobank.model.Dispatch;

public class DispatchPeer {
	public static final Property<Integer, Dispatch> ID = Property.create(
		"id" //$NON-NLS-1$
		, Dispatch.class
		, new TypeReference<Integer>() {}
		, new Property.Accessor<Integer, Dispatch>() { private static final long serialVersionUID = 1L;
			@Override
			public Integer get(Dispatch model) {
				return model.getId();
			}
			@Override
			public void set(Dispatch model, Integer value) {
				model.setId(value);
			}
		});

	public static final Property<Integer, Dispatch> STATE = Property.create(
		"state" //$NON-NLS-1$
		, Dispatch.class
		, new TypeReference<Integer>() {}
		, new Property.Accessor<Integer, Dispatch>() { private static final long serialVersionUID = 1L;
			@Override
			public Integer get(Dispatch model) {
				return model.getState();
			}
			@Override
			public void set(Dispatch model, Integer value) {
				model.setState(value);
			}
		});

	public static final Property<ShipmentInfo, Dispatch> SHIPMENT_INFO = Property.create(
		"shipmentInfo" //$NON-NLS-1$
		, Dispatch.class
		, new TypeReference<ShipmentInfo>() {}
		, new Property.Accessor<ShipmentInfo, Dispatch>() { private static final long serialVersionUID = 1L;
			@Override
			public ShipmentInfo get(Dispatch model) {
				return model.getShipmentInfo();
			}
			@Override
			public void set(Dispatch model, ShipmentInfo value) {
				model.setShipmentInfo(value);
			}
		});

	public static final Property<Collection<Comment>, Dispatch> COMMENT_COLLECTION = Property.create(
		"commentCollection" //$NON-NLS-1$
		, Dispatch.class
		, new TypeReference<Collection<Comment>>() {}
		, new Property.Accessor<Collection<Comment>, Dispatch>() { private static final long serialVersionUID = 1L;
			@Override
			public Collection<Comment> get(Dispatch model) {
				return model.getCommentCollection();
			}
			@Override
			public void set(Dispatch model, Collection<Comment> value) {
				model.getCommentCollection().clear();
				model.getCommentCollection().addAll(value);
			}
		});

	public static final Property<Center, Dispatch> RECEIVER_CENTER = Property.create(
		"receiverCenter" //$NON-NLS-1$
		, Dispatch.class
		, new TypeReference<Center>() {}
		, new Property.Accessor<Center, Dispatch>() { private static final long serialVersionUID = 1L;
			@Override
			public Center get(Dispatch model) {
				return model.getReceiverCenter();
			}
			@Override
			public void set(Dispatch model, Center value) {
				model.setReceiverCenter(value);
			}
		});

	public static final Property<Collection<DispatchSpecimen>, Dispatch> DISPATCH_SPECIMEN_COLLECTION = Property.create(
		"dispatchSpecimenCollection" //$NON-NLS-1$
		, Dispatch.class
		, new TypeReference<Collection<DispatchSpecimen>>() {}
		, new Property.Accessor<Collection<DispatchSpecimen>, Dispatch>() { private static final long serialVersionUID = 1L;
			@Override
			public Collection<DispatchSpecimen> get(Dispatch model) {
				return model.getDispatchSpecimenCollection();
			}
			@Override
			public void set(Dispatch model, Collection<DispatchSpecimen> value) {
				model.getDispatchSpecimenCollection().clear();
				model.getDispatchSpecimenCollection().addAll(value);
			}
		});

	public static final Property<Center, Dispatch> SENDER_CENTER = Property.create(
		"senderCenter" //$NON-NLS-1$
		, Dispatch.class
		, new TypeReference<Center>() {}
		, new Property.Accessor<Center, Dispatch>() { private static final long serialVersionUID = 1L;
			@Override
			public Center get(Dispatch model) {
				return model.getSenderCenter();
			}
			@Override
			public void set(Dispatch model, Center value) {
				model.setSenderCenter(value);
			}
		});

   public static final List<Property<?, ? super Dispatch>> PROPERTIES;
   static {
      List<Property<?, ? super Dispatch>> aList = new ArrayList<Property<?, ? super Dispatch>>();
      aList.add(ID);
      aList.add(STATE);
      aList.add(SHIPMENT_INFO);
      aList.add(COMMENT_COLLECTION);
      aList.add(RECEIVER_CENTER);
      aList.add(DISPATCH_SPECIMEN_COLLECTION);
      aList.add(SENDER_CENTER);
      PROPERTIES = Collections.unmodifiableList(aList);
   };
}
