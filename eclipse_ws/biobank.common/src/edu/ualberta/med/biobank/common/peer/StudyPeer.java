package edu.ualberta.med.biobank.common.peer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import edu.ualberta.med.biobank.common.util.TypeReference;
import edu.ualberta.med.biobank.common.wrappers.Property;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.AliquotedSpecimen;
import edu.ualberta.med.biobank.model.Comment;
import edu.ualberta.med.biobank.model.Contact;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.ResearchGroup;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.SourceSpecimen;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.model.StudyEventAttr;

/**
 *
 * Code Changes -
 * 		1> Add a new property to hold a collection of Research Groups associated with a Study
 * 		2> Remove old property RESEARCH_GROUP
 *
 * @author OHSDEV
 *
 */
public class StudyPeer {
    public static final Property<Integer, Study> ID = Property.create("id" //$NON-NLS-1$
        , Study.class
        , new TypeReference<Integer>() {
        }
        , new Property.Accessor<Integer, Study>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Integer get(Study model) {
                return model.getId();
            }

            @Override
            public void set(Study model, Integer value) {
                model.setId(value);
            }
        });

    public static final Property<String, Study> NAME = Property.create("name" //$NON-NLS-1$
        , Study.class
        , new TypeReference<String>() {
        }
        , new Property.Accessor<String, Study>() {
            private static final long serialVersionUID = 1L;

            @Override
            public String get(Study model) {
                return model.getName();
            }

            @Override
            public void set(Study model, String value) {
                model.setName(value);
            }
        });

    public static final Property<String, Study> NAME_SHORT = Property.create(
        "nameShort" //$NON-NLS-1$
        , Study.class
        , new TypeReference<String>() {
        }
        , new Property.Accessor<String, Study>() {
            private static final long serialVersionUID = 1L;

            @Override
            public String get(Study model) {
                return model.getNameShort();
            }

            @Override
            public void set(Study model, String value) {
                model.setNameShort(value);
            }
        });

    //OHSDEV
    public static final Property<Collection<ResearchGroup>, Study> RESEARCH_GROUPS =
	Property.create("researchGroups" //$NON-NLS-1$
            , Study.class
		, new TypeReference<Collection<ResearchGroup>>() {
            }
		, new Property.Accessor<Collection<ResearchGroup>, Study>() {
                private static final long serialVersionUID = 1L;

                @Override
			public Collection<ResearchGroup> get(Study model) {
                        return model.getResearchGroups();
                }

                @Override
                public void set(Study model, Collection<ResearchGroup> value) {
                        model.getResearchGroups().clear();
                        model.getResearchGroups().addAll(value);
                }
            });

    public static final Property<Collection<Contact>, Study> CONTACTS =
        Property.create("contacts" //$NON-NLS-1$
            , Study.class
            , new TypeReference<Collection<Contact>>() {
            }
            , new Property.Accessor<Collection<Contact>, Study>() {
                private static final long serialVersionUID = 1L;

                @Override
                public Collection<Contact> get(Study model) {
                    return model.getContacts();
                }

                @Override
                public void set(Study model, Collection<Contact> value) {
                    model.getContacts().clear();
                    model.getContacts().addAll(value);
                }
            });

    public static final Property<Collection<Patient>, Study> PATIENTS =
        Property.create("patients" //$NON-NLS-1$
            , Study.class
            , new TypeReference<Collection<Patient>>() {
            }
            , new Property.Accessor<Collection<Patient>, Study>() {
                private static final long serialVersionUID = 1L;

                @Override
                public Collection<Patient> get(Study model) {
                    return model.getPatients();
                }

                @Override
                public void set(Study model, Collection<Patient> value) {
                    model.getPatients().clear();
                    model.getPatients().addAll(value);
                }
            });

    public static final Property<Collection<Comment>, Study> COMMENTS =
        Property.create("comments" //$NON-NLS-1$
            , Study.class
            , new TypeReference<Collection<Comment>>() {
            }
            , new Property.Accessor<Collection<Comment>, Study>() {
                private static final long serialVersionUID = 1L;

                @Override
                public Collection<Comment> get(Study model) {
                    return model.getComments();
                }

                @Override
                public void set(Study model, Collection<Comment> value) {
                    model.getComments().clear();
                    model.getComments().addAll(value);
                }
            });

    public static final Property<Collection<Site>, Study> SITES = Property
        .create("sites" //$NON-NLS-1$
            , Study.class
            , new TypeReference<Collection<Site>>() {
            }
            , new Property.Accessor<Collection<Site>, Study>() {
                private static final long serialVersionUID = 1L;

                @Override
                public Collection<Site> get(Study model) {
                    return model.getSites();
                }

                @Override
                public void set(Study model, Collection<Site> value) {
                    model.getSites().clear();
                    model.getSites().addAll(value);
                }
            });

    public static final Property<Collection<StudyEventAttr>, Study> STUDY_EVENT_ATTRS =
        Property.create("studyEventAttrs" //$NON-NLS-1$
            , Study.class
            , new TypeReference<Collection<StudyEventAttr>>() {
            }
            , new Property.Accessor<Collection<StudyEventAttr>, Study>() {
                private static final long serialVersionUID = 1L;

                @Override
                public Collection<StudyEventAttr> get(Study model) {
                    return model.getStudyEventAttrs();
                }

                @Override
                public void set(Study model, Collection<StudyEventAttr> value) {
                    model.getStudyEventAttrs().clear();
                    model.getStudyEventAttrs().addAll(value);
                }
            });

    public static final Property<ActivityStatus, Study> ACTIVITY_STATUS =
        Property.create("activityStatus" //$NON-NLS-1$
            , Study.class
            , new TypeReference<ActivityStatus>() {
            }
            , new Property.Accessor<ActivityStatus, Study>() {
                private static final long serialVersionUID = 1L;

                @Override
                public ActivityStatus get(Study model) {
                    return model.getActivityStatus();
                }

                @Override
                public void set(Study model, ActivityStatus value) {
                    model.setActivityStatus(value);
                }
            });

    public static final Property<Collection<AliquotedSpecimen>, Study> ALIQUOTED_SPECIMENS =
        Property.create("aliquotedSpecimens" //$NON-NLS-1$
            , Study.class
            , new TypeReference<Collection<AliquotedSpecimen>>() {
            }
            , new Property.Accessor<Collection<AliquotedSpecimen>, Study>() {
                private static final long serialVersionUID = 1L;

                @Override
                public Collection<AliquotedSpecimen> get(Study model) {
                    return model.getAliquotedSpecimens();
                }

                @Override
                public void set(Study model, Collection<AliquotedSpecimen> value) {
                    model.getAliquotedSpecimens().clear();
                    model.getAliquotedSpecimens().addAll(value);
                }
            });

    public static final Property<Collection<SourceSpecimen>, Study> SOURCE_SPECIMENS =
        Property.create("sourceSpecimens" //$NON-NLS-1$
            , Study.class
            , new TypeReference<Collection<SourceSpecimen>>() {
            }
            , new Property.Accessor<Collection<SourceSpecimen>, Study>() {
                private static final long serialVersionUID = 1L;

                @Override
                public Collection<SourceSpecimen> get(Study model) {
                    return model.getSourceSpecimens();
                }

                @Override
                public void set(Study model, Collection<SourceSpecimen> value) {
                    model.getSourceSpecimens().clear();
                    model.getSourceSpecimens().addAll(value);
                }
            });

    public static final List<Property<?, ? super Study>> PROPERTIES;
    static {
        List<Property<?, ? super Study>> aList =
            new ArrayList<Property<?, ? super Study>>();
        aList.add(ID);
        aList.add(NAME);
        aList.add(NAME_SHORT);
        aList.add(RESEARCH_GROUPS);		//OHSDEV
        aList.add(CONTACTS);
        aList.add(PATIENTS);
        aList.add(COMMENTS);
        aList.add(SITES);
        aList.add(STUDY_EVENT_ATTRS);
        aList.add(ACTIVITY_STATUS);
        aList.add(ALIQUOTED_SPECIMENS);
        aList.add(SOURCE_SPECIMENS);
        PROPERTIES = Collections.unmodifiableList(aList);
    };
}
