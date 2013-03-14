package edu.ualberta.med.biobank.common.peer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.ualberta.med.biobank.common.util.TypeReference;
import edu.ualberta.med.biobank.common.wrappers.Property;
import edu.ualberta.med.biobank.model.ContainerLabelingScheme;

public class ContainerLabelingSchemePeer {
    public static final Property<Integer, ContainerLabelingScheme> ID = Property.create("id" //$NON-NLS-1$
    , ContainerLabelingScheme.class
        , new TypeReference<Integer>() {
        }
        , new Property.Accessor<Integer, ContainerLabelingScheme>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Integer get(ContainerLabelingScheme model) {
                return model.getId();
            }

            @Override
            public void set(ContainerLabelingScheme model, Integer value) {
                model.setId(value);
            }
        });

    public static final Property<Integer, ContainerLabelingScheme> MAX_CAPACITY = Property.create("maxCapacity" //$NON-NLS-1$
    , ContainerLabelingScheme.class
        , new TypeReference<Integer>() {
        }
        , new Property.Accessor<Integer, ContainerLabelingScheme>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Integer get(ContainerLabelingScheme model) {
                return model.getMaxCapacity();
            }

            @Override
            public void set(ContainerLabelingScheme model, Integer value) {
                model.setMaxCapacity(value);
            }
        });

    public static final Property<Integer, ContainerLabelingScheme> MIN_CHARS = Property.create("minChars" //$NON-NLS-1$
    , ContainerLabelingScheme.class
        , new TypeReference<Integer>() {
        }
        , new Property.Accessor<Integer, ContainerLabelingScheme>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Integer get(ContainerLabelingScheme model) {
                return model.getMinChars();
            }

            @Override
            public void set(ContainerLabelingScheme model, Integer value) {
                model.setMinChars(value);
            }
        });

    public static final Property<Integer, ContainerLabelingScheme> MAX_CHARS = Property.create("maxChars" //$NON-NLS-1$
    , ContainerLabelingScheme.class
        , new TypeReference<Integer>() {
        }
        , new Property.Accessor<Integer, ContainerLabelingScheme>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Integer get(ContainerLabelingScheme model) {
                return model.getMaxChars();
            }

            @Override
            public void set(ContainerLabelingScheme model, Integer value) {
                model.setMaxChars(value);
            }
        });

    public static final Property<String, ContainerLabelingScheme> NAME = Property.create("name" //$NON-NLS-1$
    , ContainerLabelingScheme.class
        , new TypeReference<String>() {
        }
        , new Property.Accessor<String, ContainerLabelingScheme>() {
            private static final long serialVersionUID = 1L;

            @Override
            public String get(ContainerLabelingScheme model) {
                return model.getName();
            }

            @Override
            public void set(ContainerLabelingScheme model, String value) {
                model.setName(value);
            }
        });

    public static final Property<Integer, ContainerLabelingScheme> MAX_ROWS = Property.create("maxRows" //$NON-NLS-1$
    , ContainerLabelingScheme.class
        , new TypeReference<Integer>() {
        }
        , new Property.Accessor<Integer, ContainerLabelingScheme>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Integer get(ContainerLabelingScheme model) {
                return model.getMaxRows();
            }

            @Override
            public void set(ContainerLabelingScheme model, Integer value) {
                model.setMaxRows(value);
            }
        });

    public static final Property<Integer, ContainerLabelingScheme> MAX_COLS = Property.create("maxCols" //$NON-NLS-1$
    , ContainerLabelingScheme.class
        , new TypeReference<Integer>() {
        }
        , new Property.Accessor<Integer, ContainerLabelingScheme>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Integer get(ContainerLabelingScheme model) {
                return model.getMaxCols();
            }

            @Override
            public void set(ContainerLabelingScheme model, Integer value) {
                model.setMaxCols(value);
            }
        });

    public static final Property<Boolean, ContainerLabelingScheme> HAS_MULTIPLE_LAYOUT = Property.create("hasMultipleLayout" //$NON-NLS-1$
    , ContainerLabelingScheme.class
        , new TypeReference<Boolean>() {
        }
        , new Property.Accessor<Boolean, ContainerLabelingScheme>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Boolean get(ContainerLabelingScheme model) {
                return model.getHasMultipleLayout();
            }

            @Override
            public void set(ContainerLabelingScheme model, Boolean value) {
                model.setHasMultipleLayout(value);
            }
        });

    public static final List<Property<?, ? super ContainerLabelingScheme>> PROPERTIES;
    static {
        List<Property<?, ? super ContainerLabelingScheme>> aList = new ArrayList<Property<?, ? super ContainerLabelingScheme>>();
        aList.add(ID);
        aList.add(MAX_CAPACITY);
        aList.add(MIN_CHARS);
        aList.add(MAX_CHARS);
        aList.add(NAME);
        aList.add(MAX_ROWS);
        aList.add(MAX_COLS);
        aList.add(HAS_MULTIPLE_LAYOUT);
        PROPERTIES = Collections.unmodifiableList(aList);
    };
}
