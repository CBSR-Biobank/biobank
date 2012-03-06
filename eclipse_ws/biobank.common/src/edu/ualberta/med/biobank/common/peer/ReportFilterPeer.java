package edu.ualberta.med.biobank.common.peer;

import edu.ualberta.med.biobank.common.util.TypeReference;
import java.util.Collections;
import edu.ualberta.med.biobank.common.wrappers.Property;
import java.util.List;
import java.util.ArrayList;
import edu.ualberta.med.biobank.model.ReportFilterValue;
import edu.ualberta.med.biobank.model.EntityFilter;
import java.util.Collection;
import edu.ualberta.med.biobank.model.ReportFilter;

public class ReportFilterPeer {
	public static final Property<Integer, ReportFilter> POSITION = Property.create(
		"position" //$NON-NLS-1$
		, ReportFilter.class
		, new TypeReference<Integer>() {}
		, new Property.Accessor<Integer, ReportFilter>() { private static final long serialVersionUID = 1L;
			@Override
			public Integer get(ReportFilter model) {
				return model.getPosition();
			}
			@Override
			public void set(ReportFilter model, Integer value) {
				model.setPosition(value);
			}
		});

	public static final Property<Integer, ReportFilter> ID = Property.create(
		"id" //$NON-NLS-1$
		, ReportFilter.class
		, new TypeReference<Integer>() {}
		, new Property.Accessor<Integer, ReportFilter>() { private static final long serialVersionUID = 1L;
			@Override
			public Integer get(ReportFilter model) {
				return model.getId();
			}
			@Override
			public void set(ReportFilter model, Integer value) {
				model.setId(value);
			}
		});

	public static final Property<Integer, ReportFilter> OPERATOR = Property.create(
		"operator" //$NON-NLS-1$
		, ReportFilter.class
		, new TypeReference<Integer>() {}
		, new Property.Accessor<Integer, ReportFilter>() { private static final long serialVersionUID = 1L;
			@Override
			public Integer get(ReportFilter model) {
				return model.getOperator();
			}
			@Override
			public void set(ReportFilter model, Integer value) {
				model.setOperator(value);
			}
		});

	public static final Property<Collection<ReportFilterValue>, ReportFilter> REPORT_FILTER_VALUES = Property.create(
		"reportFilterValues" //$NON-NLS-1$
		, ReportFilter.class
		, new TypeReference<Collection<ReportFilterValue>>() {}
		, new Property.Accessor<Collection<ReportFilterValue>, ReportFilter>() { private static final long serialVersionUID = 1L;
			@Override
			public Collection<ReportFilterValue> get(ReportFilter model) {
				return model.getReportFilterValues();
			}
			@Override
			public void set(ReportFilter model, Collection<ReportFilterValue> value) {
				model.getReportFilterValues().clear();
				model.getReportFilterValues().addAll(value);
			}
		});

	public static final Property<EntityFilter, ReportFilter> ENTITY_FILTER = Property.create(
		"entityFilter" //$NON-NLS-1$
		, ReportFilter.class
		, new TypeReference<EntityFilter>() {}
		, new Property.Accessor<EntityFilter, ReportFilter>() { private static final long serialVersionUID = 1L;
			@Override
			public EntityFilter get(ReportFilter model) {
				return model.getEntityFilter();
			}
			@Override
			public void set(ReportFilter model, EntityFilter value) {
				model.setEntityFilter(value);
			}
		});

   public static final List<Property<?, ? super ReportFilter>> PROPERTIES;
   static {
      List<Property<?, ? super ReportFilter>> aList = new ArrayList<Property<?, ? super ReportFilter>>();
      aList.add(POSITION);
      aList.add(ID);
      aList.add(OPERATOR);
      aList.add(REPORT_FILTER_VALUES);
      aList.add(ENTITY_FILTER);
      PROPERTIES = Collections.unmodifiableList(aList);
   };
}
