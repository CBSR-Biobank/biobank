package edu.ualberta.med.biobank.common.peer;

import edu.ualberta.med.biobank.common.util.TypeReference;

import java.math.BigDecimal;
import java.util.Collections;
import edu.ualberta.med.biobank.common.wrappers.Property;
import java.util.List;
import java.util.ArrayList;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.model.SpecimenType;
import edu.ualberta.med.biobank.model.AliquotedSpecimen;

public class AliquotedSpecimenPeer {
	public static final Property<Integer, AliquotedSpecimen> ID = Property.create(
		"id" //$NON-NLS-1$
		, AliquotedSpecimen.class
		, new TypeReference<Integer>() {}
		, new Property.Accessor<Integer, AliquotedSpecimen>() { private static final long serialVersionUID = 1L;
			@Override
			public Integer get(AliquotedSpecimen model) {
				return model.getId();
			}
			@Override
			public void set(AliquotedSpecimen model, Integer value) {
				model.setId(value);
			}
		});

	public static final Property<BigDecimal, AliquotedSpecimen> VOLUME = Property.create(
		"volume" //$NON-NLS-1$
		, AliquotedSpecimen.class
		, new TypeReference<BigDecimal>() {}
		, new Property.Accessor<BigDecimal, AliquotedSpecimen>() { private static final long serialVersionUID = 1L;
			@Override
			public BigDecimal get(AliquotedSpecimen model) {
				return model.getVolume();
			}
			@Override
			public void set(AliquotedSpecimen model, BigDecimal value) {
				model.setVolume(value);
			}
		});

	public static final Property<Integer, AliquotedSpecimen> QUANTITY = Property.create(
		"quantity" //$NON-NLS-1$
		, AliquotedSpecimen.class
		, new TypeReference<Integer>() {}
		, new Property.Accessor<Integer, AliquotedSpecimen>() { private static final long serialVersionUID = 1L;
			@Override
			public Integer get(AliquotedSpecimen model) {
				return model.getQuantity();
			}
			@Override
			public void set(AliquotedSpecimen model, Integer value) {
				model.setQuantity(value);
			}
		});

	public static final Property<ActivityStatus, AliquotedSpecimen> ACTIVITY_STATUS = Property.create(
		"activityStatus" //$NON-NLS-1$
		, AliquotedSpecimen.class
		, new TypeReference<ActivityStatus>() {}
		, new Property.Accessor<ActivityStatus, AliquotedSpecimen>() { private static final long serialVersionUID = 1L;
			@Override
			public ActivityStatus get(AliquotedSpecimen model) {
				return model.getActivityStatus();
			}
			@Override
			public void set(AliquotedSpecimen model, ActivityStatus value) {
				model.setActivityStatus(value);
			}
		});

	public static final Property<Study, AliquotedSpecimen> STUDY = Property.create(
		"study" //$NON-NLS-1$
		, AliquotedSpecimen.class
		, new TypeReference<Study>() {}
		, new Property.Accessor<Study, AliquotedSpecimen>() { private static final long serialVersionUID = 1L;
			@Override
			public Study get(AliquotedSpecimen model) {
				return model.getStudy();
			}
			@Override
			public void set(AliquotedSpecimen model, Study value) {
				model.setStudy(value);
			}
		});

	public static final Property<SpecimenType, AliquotedSpecimen> SPECIMEN_TYPE = Property.create(
		"specimenType" //$NON-NLS-1$
		, AliquotedSpecimen.class
		, new TypeReference<SpecimenType>() {}
		, new Property.Accessor<SpecimenType, AliquotedSpecimen>() { private static final long serialVersionUID = 1L;
			@Override
			public SpecimenType get(AliquotedSpecimen model) {
				return model.getSpecimenType();
			}
			@Override
			public void set(AliquotedSpecimen model, SpecimenType value) {
				model.setSpecimenType(value);
			}
		});

   public static final List<Property<?, ? super AliquotedSpecimen>> PROPERTIES;
   static {
      List<Property<?, ? super AliquotedSpecimen>> aList = new ArrayList<Property<?, ? super AliquotedSpecimen>>();
      aList.add(ID);
      aList.add(VOLUME);
      aList.add(QUANTITY);
      aList.add(ACTIVITY_STATUS);
      aList.add(STUDY);
      aList.add(SPECIMEN_TYPE);
      PROPERTIES = Collections.unmodifiableList(aList);
   };
}
