package edu.ualberta.med.biobank.common.peer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import edu.ualberta.med.biobank.common.util.TypeReference;
import edu.ualberta.med.biobank.common.wrappers.Property;
import edu.ualberta.med.biobank.model.ShipmentInfo;
import edu.ualberta.med.biobank.model.ShippingMethod;

public class ShipmentInfoPeer {
	public static final Property<Integer, ShipmentInfo> ID = Property.create(
		"id" //$NON-NLS-1$
		, ShipmentInfo.class
		, new TypeReference<Integer>() {}
		, new Property.Accessor<Integer, ShipmentInfo>() { private static final long serialVersionUID = 1L;
			@Override
			public Integer get(ShipmentInfo model) {
				return model.getId();
			}
			@Override
			public void set(ShipmentInfo model, Integer value) {
				model.setId(value);
			}
		});

	public static final Property<String, ShipmentInfo> WAYBILL = Property.create(
		"waybill" //$NON-NLS-1$
		, ShipmentInfo.class
		, new TypeReference<String>() {}
		, new Property.Accessor<String, ShipmentInfo>() { private static final long serialVersionUID = 1L;
			@Override
			public String get(ShipmentInfo model) {
				return model.getWaybill();
			}
			@Override
			public void set(ShipmentInfo model, String value) {
				model.setWaybill(value);
			}
		});

	public static final Property<Date, ShipmentInfo> RECEIVED_AT = Property.create(
		"receivedAt" //$NON-NLS-1$
		, ShipmentInfo.class
		, new TypeReference<Date>() {}
		, new Property.Accessor<Date, ShipmentInfo>() { private static final long serialVersionUID = 1L;
			@Override
			public Date get(ShipmentInfo model) {
				return model.getReceivedAt();
			}
			@Override
			public void set(ShipmentInfo model, Date value) {
				model.setReceivedAt(value);
			}
		});

	public static final Property<String, ShipmentInfo> BOX_NUMBER = Property.create(
		"boxNumber" //$NON-NLS-1$
		, ShipmentInfo.class
		, new TypeReference<String>() {}
		, new Property.Accessor<String, ShipmentInfo>() { private static final long serialVersionUID = 1L;
			@Override
			public String get(ShipmentInfo model) {
				return model.getBoxNumber();
			}
			@Override
			public void set(ShipmentInfo model, String value) {
				model.setBoxNumber(value);
			}
		});

	public static final Property<Date, ShipmentInfo> PACKED_AT = Property.create(
		"packedAt" //$NON-NLS-1$
		, ShipmentInfo.class
		, new TypeReference<Date>() {}
		, new Property.Accessor<Date, ShipmentInfo>() { private static final long serialVersionUID = 1L;
			@Override
			public Date get(ShipmentInfo model) {
				return model.getPackedAt();
			}
			@Override
			public void set(ShipmentInfo model, Date value) {
				model.setPackedAt(value);
			}
		});

	public static final Property<ShippingMethod, ShipmentInfo> SHIPPING_METHOD = Property.create(
		"shippingMethod" //$NON-NLS-1$
		, ShipmentInfo.class
		, new TypeReference<ShippingMethod>() {}
		, new Property.Accessor<ShippingMethod, ShipmentInfo>() { private static final long serialVersionUID = 1L;
			@Override
			public ShippingMethod get(ShipmentInfo model) {
				return model.getShippingMethod();
			}
			@Override
			public void set(ShipmentInfo model, ShippingMethod value) {
				model.setShippingMethod(value);
			}
		});

   public static final List<Property<?, ? super ShipmentInfo>> PROPERTIES;
   static {
      List<Property<?, ? super ShipmentInfo>> aList = new ArrayList<Property<?, ? super ShipmentInfo>>();
      aList.add(ID);
      aList.add(WAYBILL);
      aList.add(RECEIVED_AT);
      aList.add(BOX_NUMBER);
      aList.add(PACKED_AT);
      aList.add(SHIPPING_METHOD);
      PROPERTIES = Collections.unmodifiableList(aList);
   };
}
