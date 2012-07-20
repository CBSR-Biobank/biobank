/*
 * This code is automatically generated. Please do not edit.
 */

package edu.ualberta.med.biobank.common.wrappers.base;

import java.util.List;

import edu.ualberta.med.biobank.common.peer.ContainerLabelingSchemePeer;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.Property;
import edu.ualberta.med.biobank.model.ContainerLabelingScheme;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class ContainerLabelingSchemeBaseWrapper extends ModelWrapper<ContainerLabelingScheme> {

    public ContainerLabelingSchemeBaseWrapper(WritableApplicationService appService) {
        super(appService);
    }

    public ContainerLabelingSchemeBaseWrapper(WritableApplicationService appService,
        ContainerLabelingScheme wrappedObject) {
        super(appService, wrappedObject);
    }

    @Override
    public final Class<ContainerLabelingScheme> getWrappedClass() {
        return ContainerLabelingScheme.class;
    }

    @Override
    public Property<Integer, ? super ContainerLabelingScheme> getIdProperty() {
        return ContainerLabelingSchemePeer.ID;
    }

    @Override
    protected List<Property<?, ? super ContainerLabelingScheme>> getProperties() {
        return ContainerLabelingSchemePeer.PROPERTIES;
    }

    public Integer getMaxCapacity() {
        return getProperty(ContainerLabelingSchemePeer.MAX_CAPACITY);
    }

    public void setMaxCapacity(Integer maxCapacity) {
        setProperty(ContainerLabelingSchemePeer.MAX_CAPACITY, maxCapacity);
    }

    public Integer getMinChars() {
        return getProperty(ContainerLabelingSchemePeer.MIN_CHARS);
    }

    public void setMinChars(Integer minChars) {
        setProperty(ContainerLabelingSchemePeer.MIN_CHARS, minChars);
    }

    public Integer getMaxChars() {
        return getProperty(ContainerLabelingSchemePeer.MAX_CHARS);
    }

    public void setMaxChars(Integer maxChars) {
        setProperty(ContainerLabelingSchemePeer.MAX_CHARS, maxChars);
    }

    public String getName() {
        return getProperty(ContainerLabelingSchemePeer.NAME);
    }

    public void setName(String name) {
        String trimmed = name == null ? null : name.trim();
        setProperty(ContainerLabelingSchemePeer.NAME, trimmed);
    }

    public Integer getMaxRows() {
        return getProperty(ContainerLabelingSchemePeer.MAX_ROWS);
    }

    public void setMaxRows(Integer maxRows) {
        setProperty(ContainerLabelingSchemePeer.MAX_ROWS, maxRows);
    }

    public Integer getMaxCols() {
        return getProperty(ContainerLabelingSchemePeer.MAX_COLS);
    }

    public void setMaxCols(Integer maxCols) {
        setProperty(ContainerLabelingSchemePeer.MAX_COLS, maxCols);
    }

}
