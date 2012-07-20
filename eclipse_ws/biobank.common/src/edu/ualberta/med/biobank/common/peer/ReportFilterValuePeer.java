package edu.ualberta.med.biobank.common.peer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.ualberta.med.biobank.common.util.TypeReference;
import edu.ualberta.med.biobank.common.wrappers.Property;
import edu.ualberta.med.biobank.model.ReportFilterValue;

public class ReportFilterValuePeer {
	public static final Property<Integer, ReportFilterValue> POSITION = Property.create(
		"position" //$NON-NLS-1$
		, ReportFilterValue.class
		, new TypeReference<Integer>() {}
		, new Property.Accessor<Integer, ReportFilterValue>() { private static final long serialVersionUID = 1L;
			@Override
			public Integer get(ReportFilterValue model) {
				return model.getPosition();
			}
			@Override
			public void set(ReportFilterValue model, Integer value) {
				model.setPosition(value);
			}
		});

	public static final Property<Integer, ReportFilterValue> ID = Property.create(
		"id" //$NON-NLS-1$
		, ReportFilterValue.class
		, new TypeReference<Integer>() {}
		, new Property.Accessor<Integer, ReportFilterValue>() { private static final long serialVersionUID = 1L;
			@Override
			public Integer get(ReportFilterValue model) {
				return model.getId();
			}
			@Override
			public void set(ReportFilterValue model, Integer value) {
				model.setId(value);
			}
		});

	public static final Property<String, ReportFilterValue> VALUE = Property.create(
		"value" //$NON-NLS-1$
		, ReportFilterValue.class
		, new TypeReference<String>() {}
		, new Property.Accessor<String, ReportFilterValue>() { private static final long serialVersionUID = 1L;
			@Override
			public String get(ReportFilterValue model) {
				return model.getValue();
			}
			@Override
			public void set(ReportFilterValue model, String value) {
				model.setValue(value);
			}
		});

	public static final Property<String, ReportFilterValue> SECOND_VALUE = Property.create(
		"secondValue" //$NON-NLS-1$
		, ReportFilterValue.class
		, new TypeReference<String>() {}
		, new Property.Accessor<String, ReportFilterValue>() { private static final long serialVersionUID = 1L;
			@Override
			public String get(ReportFilterValue model) {
				return model.getSecondValue();
			}
			@Override
			public void set(ReportFilterValue model, String value) {
				model.setSecondValue(value);
			}
		});

   public static final List<Property<?, ? super ReportFilterValue>> PROPERTIES;
   static {
      List<Property<?, ? super ReportFilterValue>> aList = new ArrayList<Property<?, ? super ReportFilterValue>>();
      aList.add(POSITION);
      aList.add(ID);
      aList.add(VALUE);
      aList.add(SECOND_VALUE);
      PROPERTIES = Collections.unmodifiableList(aList);
   };
}
