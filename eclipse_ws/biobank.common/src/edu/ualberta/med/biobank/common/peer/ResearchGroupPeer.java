package edu.ualberta.med.biobank.common.peer;

import edu.ualberta.med.biobank.common.util.TypeReference;
import java.util.Collections;
import edu.ualberta.med.biobank.common.wrappers.Property;
import java.util.List;
import java.util.ArrayList;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.model.Request;
import java.util.Collection;
import edu.ualberta.med.biobank.model.ResearchGroup;

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

	public static final Property<Collection<Request>, ResearchGroup> REQUEST_COLLECTION = Property.create(
		"requestCollection" //$NON-NLS-1$
		, ResearchGroup.class
		, new TypeReference<Collection<Request>>() {}
		, new Property.Accessor<Collection<Request>, ResearchGroup>() { private static final long serialVersionUID = 1L;
			@Override
			public Collection<Request> get(ResearchGroup model) {
				return model.getRequestCollection();
			}
			@Override
			public void set(ResearchGroup model, Collection<Request> value) {
				model.setRequestCollection(value);
			}
		});

   public static final List<Property<?, ? super ResearchGroup>> PROPERTIES;
   static {
      List<Property<?, ? super ResearchGroup>> aList = new ArrayList<Property<?, ? super ResearchGroup>>();
      aList.add(STUDY);
      aList.add(REQUEST_COLLECTION);
      PROPERTIES = Collections.unmodifiableList(aList);
   };
}
