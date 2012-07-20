package edu.ualberta.med.biobank.common.peer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.ualberta.med.biobank.common.util.TypeReference;
import edu.ualberta.med.biobank.common.wrappers.Property;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.GlobalEventAttr;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.model.StudyEventAttr;

public class StudyEventAttrPeer {
	public static final Property<Integer, StudyEventAttr> ID = Property.create(
		"id" //$NON-NLS-1$
		, StudyEventAttr.class
		, new TypeReference<Integer>() {}
		, new Property.Accessor<Integer, StudyEventAttr>() { private static final long serialVersionUID = 1L;
			@Override
			public Integer get(StudyEventAttr model) {
				return model.getId();
			}
			@Override
			public void set(StudyEventAttr model, Integer value) {
				model.setId(value);
			}
		});

	public static final Property<String, StudyEventAttr> PERMISSIBLE = Property.create(
		"permissible" //$NON-NLS-1$
		, StudyEventAttr.class
		, new TypeReference<String>() {}
		, new Property.Accessor<String, StudyEventAttr>() { private static final long serialVersionUID = 1L;
			@Override
			public String get(StudyEventAttr model) {
				return model.getPermissible();
			}
			@Override
			public void set(StudyEventAttr model, String value) {
				model.setPermissible(value);
			}
		});

	public static final Property<Boolean, StudyEventAttr> REQUIRED = Property.create(
		"required" //$NON-NLS-1$
		, StudyEventAttr.class
		, new TypeReference<Boolean>() {}
		, new Property.Accessor<Boolean, StudyEventAttr>() { private static final long serialVersionUID = 1L;
			@Override
			public Boolean get(StudyEventAttr model) {
				return model.getRequired();
			}
			@Override
			public void set(StudyEventAttr model, Boolean value) {
				model.setRequired(value);
			}
		});

	public static final Property<GlobalEventAttr, StudyEventAttr> GLOBAL_EVENT_ATTR = Property.create(
		"globalEventAttr" //$NON-NLS-1$
		, StudyEventAttr.class
		, new TypeReference<GlobalEventAttr>() {}
		, new Property.Accessor<GlobalEventAttr, StudyEventAttr>() { private static final long serialVersionUID = 1L;
			@Override
			public GlobalEventAttr get(StudyEventAttr model) {
				return model.getGlobalEventAttr();
			}
			@Override
			public void set(StudyEventAttr model, GlobalEventAttr value) {
				model.setGlobalEventAttr(value);
			}
		});

	public static final Property<ActivityStatus, StudyEventAttr> ACTIVITY_STATUS = Property.create(
		"activityStatus" //$NON-NLS-1$
		, StudyEventAttr.class
		, new TypeReference<ActivityStatus>() {}
		, new Property.Accessor<ActivityStatus, StudyEventAttr>() { private static final long serialVersionUID = 1L;
			@Override
			public ActivityStatus get(StudyEventAttr model) {
				return model.getActivityStatus();
			}
			@Override
			public void set(StudyEventAttr model, ActivityStatus value) {
				model.setActivityStatus(value);
			}
		});

	public static final Property<Study, StudyEventAttr> STUDY = Property.create(
		"study" //$NON-NLS-1$
		, StudyEventAttr.class
		, new TypeReference<Study>() {}
		, new Property.Accessor<Study, StudyEventAttr>() { private static final long serialVersionUID = 1L;
			@Override
			public Study get(StudyEventAttr model) {
				return model.getStudy();
			}
			@Override
			public void set(StudyEventAttr model, Study value) {
				model.setStudy(value);
			}
		});

   public static final List<Property<?, ? super StudyEventAttr>> PROPERTIES;
   static {
      List<Property<?, ? super StudyEventAttr>> aList = new ArrayList<Property<?, ? super StudyEventAttr>>();
      aList.add(ID);
      aList.add(PERMISSIBLE);
      aList.add(REQUIRED);
      aList.add(GLOBAL_EVENT_ATTR);
      aList.add(ACTIVITY_STATUS);
      aList.add(STUDY);
      PROPERTIES = Collections.unmodifiableList(aList);
   };
}
