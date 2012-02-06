package edu.ualberta.med.biobank.common.peer;

import edu.ualberta.med.biobank.common.util.TypeReference;
import java.util.Collections;
import edu.ualberta.med.biobank.common.wrappers.Property;
import java.util.List;
import java.util.ArrayList;
import edu.ualberta.med.biobank.model.EventAttrType;
import edu.ualberta.med.biobank.model.GlobalEventAttr;

public class GlobalEventAttrPeer {
	public static final Property<Integer, GlobalEventAttr> ID = Property.create(
		"id" //$NON-NLS-1$
		, GlobalEventAttr.class
		, new TypeReference<Integer>() {}
		, new Property.Accessor<Integer, GlobalEventAttr>() { private static final long serialVersionUID = 1L;
			@Override
			public Integer get(GlobalEventAttr model) {
				return model.getId();
			}
			@Override
			public void set(GlobalEventAttr model, Integer value) {
				model.setId(value);
			}
		});

	public static final Property<String, GlobalEventAttr> LABEL = Property.create(
		"label" //$NON-NLS-1$
		, GlobalEventAttr.class
		, new TypeReference<String>() {}
		, new Property.Accessor<String, GlobalEventAttr>() { private static final long serialVersionUID = 1L;
			@Override
			public String get(GlobalEventAttr model) {
				return model.getLabel();
			}
			@Override
			public void set(GlobalEventAttr model, String value) {
				model.setLabel(value);
			}
		});

	public static final Property<EventAttrType, GlobalEventAttr> EVENT_ATTR_TYPE = Property.create(
		"eventAttrType" //$NON-NLS-1$
		, GlobalEventAttr.class
		, new TypeReference<EventAttrType>() {}
		, new Property.Accessor<EventAttrType, GlobalEventAttr>() { private static final long serialVersionUID = 1L;
			@Override
			public EventAttrType get(GlobalEventAttr model) {
				return model.getEventAttrType();
			}
			@Override
			public void set(GlobalEventAttr model, EventAttrType value) {
				model.setEventAttrType(value);
			}
		});

   public static final List<Property<?, ? super GlobalEventAttr>> PROPERTIES;
   static {
      List<Property<?, ? super GlobalEventAttr>> aList = new ArrayList<Property<?, ? super GlobalEventAttr>>();
      aList.add(ID);
      aList.add(LABEL);
      aList.add(EVENT_ATTR_TYPE);
      PROPERTIES = Collections.unmodifiableList(aList);
   };
}
