package edu.ualberta.med.biobank.common.peer;

import edu.ualberta.med.biobank.common.util.TypeReference;
import java.util.Collections;
import edu.ualberta.med.biobank.common.wrappers.Property;
import java.util.List;
import java.util.ArrayList;
import edu.ualberta.med.biobank.model.StudyEventAttr;
import edu.ualberta.med.biobank.model.CollectionEvent;
import edu.ualberta.med.biobank.model.EventAttr;

public class EventAttrPeer {
	public static final Property<Integer, EventAttr> ID = Property.create(
		"id" //$NON-NLS-1$
		, EventAttr.class
		, new TypeReference<Integer>() {}
		, new Property.Accessor<Integer, EventAttr>() { private static final long serialVersionUID = 1L;
			@Override
			public Integer get(EventAttr model) {
				return model.getId();
			}
			@Override
			public void set(EventAttr model, Integer value) {
				model.setId(value);
			}
		});

	public static final Property<String, EventAttr> VALUE = Property.create(
		"value" //$NON-NLS-1$
		, EventAttr.class
		, new TypeReference<String>() {}
		, new Property.Accessor<String, EventAttr>() { private static final long serialVersionUID = 1L;
			@Override
			public String get(EventAttr model) {
				return model.getValue();
			}
			@Override
			public void set(EventAttr model, String value) {
				model.setValue(value);
			}
		});

	public static final Property<StudyEventAttr, EventAttr> STUDY_EVENT_ATTR = Property.create(
		"studyEventAttr" //$NON-NLS-1$
		, EventAttr.class
		, new TypeReference<StudyEventAttr>() {}
		, new Property.Accessor<StudyEventAttr, EventAttr>() { private static final long serialVersionUID = 1L;
			@Override
			public StudyEventAttr get(EventAttr model) {
				return model.getStudyEventAttr();
			}
			@Override
			public void set(EventAttr model, StudyEventAttr value) {
				model.setStudyEventAttr(value);
			}
		});

	public static final Property<CollectionEvent, EventAttr> COLLECTION_EVENT = Property.create(
		"collectionEvent" //$NON-NLS-1$
		, EventAttr.class
		, new TypeReference<CollectionEvent>() {}
		, new Property.Accessor<CollectionEvent, EventAttr>() { private static final long serialVersionUID = 1L;
			@Override
			public CollectionEvent get(EventAttr model) {
				return model.getCollectionEvent();
			}
			@Override
			public void set(EventAttr model, CollectionEvent value) {
				model.setCollectionEvent(value);
			}
		});

   public static final List<Property<?, ? super EventAttr>> PROPERTIES;
   static {
      List<Property<?, ? super EventAttr>> aList = new ArrayList<Property<?, ? super EventAttr>>();
      aList.add(ID);
      aList.add(VALUE);
      aList.add(STUDY_EVENT_ATTR);
      aList.add(COLLECTION_EVENT);
      PROPERTIES = Collections.unmodifiableList(aList);
   };
}
