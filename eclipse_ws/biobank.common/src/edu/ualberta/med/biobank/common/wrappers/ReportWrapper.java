package edu.ualberta.med.biobank.common.wrappers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.model.Report;
import edu.ualberta.med.biobank.model.ReportColumn;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class ReportWrapper extends ModelWrapper<Report> {
    private static final String PROP_KEY_NAME = "name";
    private static final String PROP_KEY_DESCRIPTION = "description";
    private static final String PROP_KEY_IS_COUNT = "isCount";

    public ReportWrapper(WritableApplicationService appService,
        Report wrappedObject) {
        super(appService, wrappedObject);
    }

    public ReportWrapper(WritableApplicationService appService) {
        super(appService);
    }

    public String getName() {
        return wrappedObject.getName();
    }

    public void setName(String name) {
        String oldName = getName();
        wrappedObject.setName(name);
        propertyChangeSupport.firePropertyChange(PROP_KEY_NAME, oldName, name);
    }

    public String getDescription() {
        return wrappedObject.getDescription();
    }

    public void setDescription(String description) {
        String oldDescription = getDescription();
        wrappedObject.setDescription(description);
        propertyChangeSupport.firePropertyChange(PROP_KEY_DESCRIPTION,
            oldDescription, description);
    }

    public Boolean getIsCount() {
        return wrappedObject.getIsCount();
    }

    public void setIsCount(Boolean bool) {
        Boolean oldIsCount = getIsCount();
        wrappedObject.setIsCount(bool);
        propertyChangeSupport.firePropertyChange(PROP_KEY_IS_COUNT, oldIsCount,
            bool);
    }

    public List<ReportColumn> getReportColumns() {
        List<ReportColumn> cols = new ArrayList<ReportColumn>(
            wrappedObject.getReportColumnCollection());

        Collections.sort(cols, new Comparator<ReportColumn>() {
            @Override
            public int compare(ReportColumn lhs, ReportColumn rhs) {
                return lhs.getPosition() - rhs.getPosition();
            }
        });

        return cols;
    }

    @Override
    public Class<Report> getWrappedClass() {
        return Report.class;
    }

    @Override
    protected void persistChecks() throws BiobankCheckException,
        ApplicationException, WrapperException {
    }

    @Override
    protected void deleteChecks() throws Exception {
    }

    @Override
    protected String[] getPropertyChangeNames() {
        return new String[] { PROP_KEY_NAME, PROP_KEY_DESCRIPTION,
            PROP_KEY_IS_COUNT };
    }
}
