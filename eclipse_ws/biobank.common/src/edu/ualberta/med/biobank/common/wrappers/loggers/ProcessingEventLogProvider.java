package edu.ualberta.med.biobank.common.wrappers.loggers;

import java.util.ArrayList;
import java.util.List;

import edu.ualberta.med.biobank.common.util.StringUtil;
import edu.ualberta.med.biobank.model.Log;
import edu.ualberta.med.biobank.model.ProcessingEvent;

public class ProcessingEventLogProvider implements
    WrapperLogProvider<ProcessingEvent> {
    private static final long serialVersionUID = 1L;

    @Override
    public Log getLog(ProcessingEvent processingEvent) {
        Log log = new Log();
        log.setCenter(processingEvent.getCenter().getNameShort());

        List<String> detailsList = new ArrayList<String>();

        String worksheet = processingEvent.getWorksheet();
        if (worksheet != null) {
            detailsList.add(new StringBuilder("Worksheet: ").append(worksheet) //$NON-NLS-1$
                .toString());
        }

        log.setDetails(StringUtil.join(detailsList, ", ")); //$NON-NLS-1$

        return log;
    }

    @Override
    public Log getObjectLog(Object model) {
        return getLog((ProcessingEvent) model);
    }
}
