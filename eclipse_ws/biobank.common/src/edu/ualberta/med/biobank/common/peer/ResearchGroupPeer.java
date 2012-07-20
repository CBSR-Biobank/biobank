package edu.ualberta.med.biobank.common.peer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import edu.ualberta.med.biobank.common.util.TypeReference;
import edu.ualberta.med.biobank.common.wrappers.Property;
import edu.ualberta.med.biobank.model.Request;
import edu.ualberta.med.biobank.model.ResearchGroup;
import edu.ualberta.med.biobank.model.Study;

public class ResearchGroupPeer  extends CenterPeer {
	public static final Property<Study, ResearchGroup> STUDY = Property.create(
		"study" //$NON-NLS-1$
		, ResearchGroup.class
		, new TypeReference<Study>() {}
		, new Property.Accessor<Study, ResearchGroup>() { private static final long serialVersionUID = 1L;
			@Override
			public Study get(ResearchGroup model) {
				return model.getStudy();
			}
			@Override
			public void set(ResearchGroup model, Study value) {
				model.setStudy(value);
			}
		});

	public static final Property<Collection<Request>, ResearchGroup> REQUESTS = Property.create(
		"requests" //$NON-NLS-1$
		, ResearchGroup.class
		, new TypeReference<Collection<Request>>() {}
		, new Property.Accessor<Collection<Request>, ResearchGroup>() { private static final long serialVersionUID = 1L;
			@Override
			public Collection<Request> get(ResearchGroup model) {
				return model.getRequests();
			}
			@Override
			public void set(ResearchGroup model, Collection<Request> value) {
				model.getRequests().clear();
				model.getRequests().addAll(value);
			}
		});

   public static final List<Property<?, ? super ResearchGroup>> PROPERTIES;
   static {
      List<Property<?, ? super ResearchGroup>> aList = new ArrayList<Property<?, ? super ResearchGroup>>();
      aList.add(STUDY);
      aList.add(REQUESTS);
      PROPERTIES = Collections.unmodifiableList(aList);
   };
}
