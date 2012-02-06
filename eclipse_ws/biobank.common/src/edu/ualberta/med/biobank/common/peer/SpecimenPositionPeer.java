package edu.ualberta.med.biobank.common.peer;

import edu.ualberta.med.biobank.common.util.TypeReference;
import java.util.Collections;
import edu.ualberta.med.biobank.common.wrappers.Property;
import java.util.List;
import java.util.ArrayList;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.model.SpecimenPosition;

public class SpecimenPositionPeer  extends AbstractPositionPeer {
	public static final Property<Container, SpecimenPosition> CONTAINER = Property.create(
		"container" //$NON-NLS-1$
		, SpecimenPosition.class
		, new TypeReference<Container>() {}
		, new Property.Accessor<Container, SpecimenPosition>() { private static final long serialVersionUID = 1L;
			@Override
			public Container get(SpecimenPosition model) {
				return model.getContainer();
			}
			@Override
			public void set(SpecimenPosition model, Container value) {
				model.setContainer(value);
			}
		});

	public static final Property<Specimen, SpecimenPosition> SPECIMEN = Property.create(
		"specimen" //$NON-NLS-1$
		, SpecimenPosition.class
		, new TypeReference<Specimen>() {}
		, new Property.Accessor<Specimen, SpecimenPosition>() { private static final long serialVersionUID = 1L;
			@Override
			public Specimen get(SpecimenPosition model) {
				return model.getSpecimen();
			}
			@Override
			public void set(SpecimenPosition model, Specimen value) {
				model.setSpecimen(value);
			}
		});

   public static final List<Property<?, ? super SpecimenPosition>> PROPERTIES;
   static {
      List<Property<?, ? super SpecimenPosition>> aList = new ArrayList<Property<?, ? super SpecimenPosition>>();
      aList.add(CONTAINER);
      aList.add(SPECIMEN);
      PROPERTIES = Collections.unmodifiableList(aList);
   };
}
