/*
 * This code is automatically generated. Please do not edit.
 */

package edu.ualberta.med.biobank.common.wrappers.base;

import java.util.List;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import edu.ualberta.med.biobank.model.AbstractPosition;
import edu.ualberta.med.biobank.common.wrappers.Property;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import java.util.ArrayList;
import edu.ualberta.med.biobank.common.peer.AbstractPositionPeer;

public abstract class AbstractPositionBaseWrapper<E extends AbstractPosition> extends ModelWrapper<E> {

    public AbstractPositionBaseWrapper(WritableApplicationService appService) {
        super(appService);
    }

    public AbstractPositionBaseWrapper(WritableApplicationService appService,
        E wrappedObject) {
        super(appService, wrappedObject);
    }

    @Override
    public Property<Integer, ? super E> getIdProperty() {
        return AbstractPositionPeer.ID;
    }

    @Override
    protected List<Property<?, ? super E>> getProperties() {
        return new ArrayList<Property<?, ? super E>>(AbstractPositionPeer.PROPERTIES);
    }

    public Integer getCol() {
        return getProperty(AbstractPositionPeer.COL);
    }

    public void setCol(Integer col) {
        setProperty(AbstractPositionPeer.COL, col);
    }

    public String getPositionString() {
        return getProperty(AbstractPositionPeer.POSITION_STRING);
    }

    public void setPositionString(String positionString) {
        String trimmed = positionString == null ? null : positionString.trim();
        setProperty(AbstractPositionPeer.POSITION_STRING, trimmed);
    }

    public Integer getRow() {
        return getProperty(AbstractPositionPeer.ROW);
    }

    public void setRow(Integer row) {
        setProperty(AbstractPositionPeer.ROW, row);
    }

}
