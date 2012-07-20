package edu.ualberta.med.biobank.common.peer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.ualberta.med.biobank.common.util.TypeReference;
import edu.ualberta.med.biobank.common.wrappers.Property;
import edu.ualberta.med.biobank.model.SourceSpecimen;
import edu.ualberta.med.biobank.model.SpecimenType;
import edu.ualberta.med.biobank.model.Study;

public class SourceSpecimenPeer {
	public static final Property<Integer, SourceSpecimen> ID = Property.create(
		"id" //$NON-NLS-1$
		, SourceSpecimen.class
		, new TypeReference<Integer>() {}
		, new Property.Accessor<Integer, SourceSpecimen>() { private static final long serialVersionUID = 1L;
			@Override
			public Integer get(SourceSpecimen model) {
				return model.getId();
			}
			@Override
			public void set(SourceSpecimen model, Integer value) {
				model.setId(value);
			}
		});

	public static final Property<Boolean, SourceSpecimen> NEED_ORIGINAL_VOLUME = Property.create(
		"needOriginalVolume" //$NON-NLS-1$
		, SourceSpecimen.class
		, new TypeReference<Boolean>() {}
		, new Property.Accessor<Boolean, SourceSpecimen>() { private static final long serialVersionUID = 1L;
			@Override
			public Boolean get(SourceSpecimen model) {
				return model.getNeedOriginalVolume();
			}
			@Override
			public void set(SourceSpecimen model, Boolean value) {
				model.setNeedOriginalVolume(value);
			}
		});

	public static final Property<Study, SourceSpecimen> STUDY = Property.create(
		"study" //$NON-NLS-1$
		, SourceSpecimen.class
		, new TypeReference<Study>() {}
		, new Property.Accessor<Study, SourceSpecimen>() { private static final long serialVersionUID = 1L;
			@Override
			public Study get(SourceSpecimen model) {
				return model.getStudy();
			}
			@Override
			public void set(SourceSpecimen model, Study value) {
				model.setStudy(value);
			}
		});

	public static final Property<SpecimenType, SourceSpecimen> SPECIMEN_TYPE = Property.create(
		"specimenType" //$NON-NLS-1$
		, SourceSpecimen.class
		, new TypeReference<SpecimenType>() {}
		, new Property.Accessor<SpecimenType, SourceSpecimen>() { private static final long serialVersionUID = 1L;
			@Override
			public SpecimenType get(SourceSpecimen model) {
				return model.getSpecimenType();
			}
			@Override
			public void set(SourceSpecimen model, SpecimenType value) {
				model.setSpecimenType(value);
			}
		});

   public static final List<Property<?, ? super SourceSpecimen>> PROPERTIES;
   static {
      List<Property<?, ? super SourceSpecimen>> aList = new ArrayList<Property<?, ? super SourceSpecimen>>();
      aList.add(ID);
      aList.add(NEED_ORIGINAL_VOLUME);
      aList.add(STUDY);
      aList.add(SPECIMEN_TYPE);
      PROPERTIES = Collections.unmodifiableList(aList);
   };
}
