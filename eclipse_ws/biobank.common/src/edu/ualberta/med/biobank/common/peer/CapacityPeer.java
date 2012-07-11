package edu.ualberta.med.biobank.common.peer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.ualberta.med.biobank.common.util.TypeReference;
import edu.ualberta.med.biobank.common.wrappers.Property;
import edu.ualberta.med.biobank.model.Capacity;

public class CapacityPeer {
	public static final Property<Integer, Capacity> COL_CAPACITY = Property.create(
		"colCapacity" //$NON-NLS-1$
		, Capacity.class
		, new TypeReference<Integer>() {}
		, new Property.Accessor<Integer, Capacity>() { private static final long serialVersionUID = 1L;
			@Override
			public Integer get(Capacity model) {
				return model.getColCapacity();
			}
			@Override
			public void set(Capacity model, Integer value) {
				model.setColCapacity(value);
			}
		});

	public static final Property<Integer, Capacity> ROW_CAPACITY = Property.create(
		"rowCapacity" //$NON-NLS-1$
		, Capacity.class
		, new TypeReference<Integer>() {}
		, new Property.Accessor<Integer, Capacity>() { private static final long serialVersionUID = 1L;
			@Override
			public Integer get(Capacity model) {
				return model.getRowCapacity();
			}
			@Override
			public void set(Capacity model, Integer value) {
				model.setRowCapacity(value);
			}
		});

   public static final List<Property<?, ? super Capacity>> PROPERTIES;
   static {
      List<Property<?, ? super Capacity>> aList = new ArrayList<Property<?, ? super Capacity>>();
      aList.add(COL_CAPACITY);
      aList.add(ROW_CAPACITY);
      PROPERTIES = Collections.unmodifiableList(aList);
   };
}
