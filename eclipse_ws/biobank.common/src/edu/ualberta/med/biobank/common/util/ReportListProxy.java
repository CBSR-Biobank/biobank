package edu.ualberta.med.biobank.common.util;

import java.io.Serializable;
import java.util.List;

import edu.ualberta.med.biobank.common.action.reports.ReportInput;
import edu.ualberta.med.biobank.server.applicationservice.BiobankApplicationService;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.ApplicationService;

public class ReportListProxy
    extends AbstractBiobankListProxy<Object>
    implements Serializable {

    private static final long serialVersionUID = 1L;

    protected ReportInput reportInput;

    public ReportListProxy(BiobankApplicationService appService, ReportInput reportInput) {
        super(appService);
        this.reportInput = reportInput;
    }

    @Override
    public List<Object> getChunk(Integer firstRow) throws ApplicationException {
        return ((BiobankApplicationService) appService).runReport(reportInput,
            pageSize, firstRow, 0);
    }

    @Override
    public void setAppService(ApplicationService as) {
        if (!(as instanceof BiobankApplicationService)) {
            throw new IllegalArgumentException("expecting BiobankApplicationService not ApplicationService"); //$NON-NLS-1$
        }

        super.setAppService(as);
    }
}