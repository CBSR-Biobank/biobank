package edu.ualberta.med.biobank.common.peer;

import edu.ualberta.med.biobank.common.util.TypeReference;
import java.util.Collections;
import edu.ualberta.med.biobank.common.wrappers.Property;
import java.util.List;
import java.util.ArrayList;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.ContainerPosition;

public class ContainerPositionPeer  extends AbstractPositionPeer {
	public static final Property<Container, ContainerPosition> CONTAINER = Property.create(
		"container" //$NON-NLS-1$
		, ContainerPosition.class
		, new TypeReference<Container>() {}
		, new Property.Accessor<Container, ContainerPosition>() { private static final long serialVersionUID = 1L;
			@Override
			public Container get(ContainerPosition model) {
				return model.getContainer();
			}
			@Override
			public void set(ContainerPosition model, Container value) {
				model.setContainer(value);
			}
		});

	public static final Property<Container, ContainerPosition> PARENT_CONTAINER = Property.create(
		"parentContainer" //$NON-NLS-1$
		, ContainerPosition.class
		, new TypeReference<Container>() {}
		, new Property.Accessor<Container, ContainerPosition>() { private static final long serialVersionUID = 1L;
			@Override
			public Container get(ContainerPosition model) {
				return model.getParentContainer();
			}
			@Override
			public void set(ContainerPosition model, Container value) {
				model.setParentContainer(value);
			}
		});

   public static final List<Property<?, ? super ContainerPosition>> PROPERTIES;
   static {
      List<Property<?, ? super ContainerPosition>> aList = new ArrayList<Property<?, ? super ContainerPosition>>();
      aList.add(CONTAINER);
      aList.add(PARENT_CONTAINER);
      PROPERTIES = Collections.unmodifiableList(aList);
   };
}
