package edu.ualberta.med.biobank.common.wrappers.loggers;

import java.util.ArrayList;
import java.util.List;

import edu.ualberta.med.biobank.common.util.StringUtil;
import edu.ualberta.med.biobank.model.Log;
import edu.ualberta.med.biobank.model.Request;

public class RequestLogProvider implements WrapperLogProvider<Request> {
    private static final long serialVersionUID = 1L;

    @Override
    public Log getLog(Request request) {
        Log log = new Log();

        log.setCenter(request.getResearchGroup().getNameShort());

        List<String> detailsList = new ArrayList<String>();
        detailsList.add("dateSubmitted:" + request.getSubmitted()); //$NON-NLS-1$

        log.setDetails(StringUtil.join(detailsList, ", ")); //$NON-NLS-1$

        return log;
    }

    @Override
    public Log getObjectLog(Object model) {
        return getLog((Request) model);
    }
}
