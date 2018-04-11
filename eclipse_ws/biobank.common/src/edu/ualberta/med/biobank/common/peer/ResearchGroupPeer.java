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

/**
 *
 * Code Changes -
 * 		1> Add a new property to hold a collection of Studies for a Research Group
 * 		2> Remove old property STUDY
 *
 * @author OHSDEV
 *
 */
public class ResearchGroupPeer  extends CenterPeer {
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

	//OHSDEV
	public static final Property<Collection<Study>, ResearchGroup> STUDIES = Property.create(
			"studies" //$NON-NLS-1$
			, ResearchGroup.class
			, new TypeReference<Collection<Study>>() {}
			, new Property.Accessor<Collection<Study>, ResearchGroup>() { private static final long serialVersionUID = 1L;
				@Override
				public Collection<Study> get(ResearchGroup model) {
					return model.getStudies();
				}
				@Override
				public void set(ResearchGroup model, Collection<Study> value) {
					model.getStudies().clear();
					model.getStudies().addAll(value);
				}
			});



   public static final List<Property<?, ? super ResearchGroup>> PROPERTIES;
   static {
      List<Property<?, ? super ResearchGroup>> aList = new ArrayList<Property<?, ? super ResearchGroup>>();
      aList.add(STUDIES);	//OHSDEV
      aList.add(REQUESTS);
      PROPERTIES = Collections.unmodifiableList(aList);
   };
}