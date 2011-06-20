package edu.ualberta.med.biobank.common.util;

import java.io.Serializable;
import java.util.List;

import edu.ualberta.med.biobank.model.Report;
import edu.ualberta.med.biobank.server.applicationservice.BiobankApplicationService;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.ApplicationService;

public class ReportListProxy extends AbstractBiobankListProxy<Object> implements
    Serializable {

    private static final long serialVersionUID = 1L;

    protected Report report;

    public ReportListProxy(BiobankApplicationService appService, Report report) {
        super(appService);
        this.report = report;
    }

    @Override
    public List<Object> getChunk(Integer firstRow) throws ApplicationException {
        return ((BiobankApplicationService) appService).runReport(report,
            pageSize, firstRow, 0);
    }

    @Override
    public void setAppService(ApplicationService as) {
        if (!(as instanceof BiobankApplicationService)) {
            throw new IllegalArgumentException(
                "expecting BiobankApplicationService not ApplicationService"); //$NON-NLS-1$
        }

        super.setAppService(as);
    }
}