package edu.ualberta.med.biobank.common.peer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import edu.ualberta.med.biobank.common.util.TypeReference;
import edu.ualberta.med.biobank.common.wrappers.Property;
import edu.ualberta.med.biobank.model.Center;
import edu.ualberta.med.biobank.model.Comment;
import edu.ualberta.med.biobank.model.OriginInfo;
import edu.ualberta.med.biobank.model.ShipmentInfo;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.Specimen;

public class OriginInfoPeer {
	public static final Property<Integer, OriginInfo> ID = Property.create(
		"id" //$NON-NLS-1$
		, OriginInfo.class
		, new TypeReference<Integer>() {}
		, new Property.Accessor<Integer, OriginInfo>() { private static final long serialVersionUID = 1L;
			@Override
			public Integer get(OriginInfo model) {
				return model.getId();
			}
			@Override
			public void set(OriginInfo model, Integer value) {
				model.setId(value);
			}
		});

	public static final Property<Center, OriginInfo> CENTER = Property.create(
		"center" //$NON-NLS-1$
		, OriginInfo.class
		, new TypeReference<Center>() {}
		, new Property.Accessor<Center, OriginInfo>() { private static final long serialVersionUID = 1L;
			@Override
			public Center get(OriginInfo model) {
				return model.getCenter();
			}
			@Override
			public void set(OriginInfo model, Center value) {
				model.setCenter(value);
			}
		});

	public static final Property<ShipmentInfo, OriginInfo> SHIPMENT_INFO = Property.create(
		"shipmentInfo" //$NON-NLS-1$
		, OriginInfo.class
		, new TypeReference<ShipmentInfo>() {}
		, new Property.Accessor<ShipmentInfo, OriginInfo>() { private static final long serialVersionUID = 1L;
			@Override
			public ShipmentInfo get(OriginInfo model) {
				return model.getShipmentInfo();
			}
			@Override
			public void set(OriginInfo model, ShipmentInfo value) {
				model.setShipmentInfo(value);
			}
		});

	public static final Property<Collection<Comment>, OriginInfo> COMMENTS = Property.create(
		"comments" //$NON-NLS-1$
		, OriginInfo.class
		, new TypeReference<Collection<Comment>>() {}
		, new Property.Accessor<Collection<Comment>, OriginInfo>() { private static final long serialVersionUID = 1L;
			@Override
			public Collection<Comment> get(OriginInfo model) {
				return model.getComments();
			}
			@Override
			public void set(OriginInfo model, Collection<Comment> value) {
				model.getComments().clear();
				model.getComments().addAll(value);
			}
		});

	public static final Property<Site, OriginInfo> RECEIVER_SITE = Property.create(
		"receiverSite" //$NON-NLS-1$
		, OriginInfo.class
		, new TypeReference<Site>() {}
		, new Property.Accessor<Site, OriginInfo>() { private static final long serialVersionUID = 1L;
			@Override
			public Site get(OriginInfo model) {
				return model.getReceiverSite();
			}
			@Override
			public void set(OriginInfo model, Site value) {
				model.setReceiverSite(value);
			}
		});

	public static final Property<Collection<Specimen>, OriginInfo> SPECIMENS = Property.create(
		"specimens" //$NON-NLS-1$
		, OriginInfo.class
		, new TypeReference<Collection<Specimen>>() {}
		, new Property.Accessor<Collection<Specimen>, OriginInfo>() { private static final long serialVersionUID = 1L;
			@Override
			public Collection<Specimen> get(OriginInfo model) {
				return model.getSpecimens();
			}
			@Override
			public void set(OriginInfo model, Collection<Specimen> value) {
				model.getSpecimens().clear();
				model.getSpecimens().addAll(value);
			}
		});

   public static final List<Property<?, ? super OriginInfo>> PROPERTIES;
   static {
      List<Property<?, ? super OriginInfo>> aList = new ArrayList<Property<?, ? super OriginInfo>>();
      aList.add(ID);
      aList.add(CENTER);
      aList.add(SHIPMENT_INFO);
      aList.add(COMMENTS);
      aList.add(RECEIVER_SITE);
      aList.add(SPECIMENS);
      PROPERTIES = Collections.unmodifiableList(aList);
   };
}
