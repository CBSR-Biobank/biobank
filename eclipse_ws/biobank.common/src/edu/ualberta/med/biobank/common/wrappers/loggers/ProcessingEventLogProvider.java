package edu.ualberta.med.biobank.common.wrappers.loggers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import edu.ualberta.med.biobank.common.util.StringUtil;
import edu.ualberta.med.biobank.model.Log;
import edu.ualberta.med.biobank.model.ProcessingEvent;
import edu.ualberta.med.biobank.model.Specimen;

public class ProcessingEventLogProvider implements
    WrapperLogProvider<ProcessingEvent> {
    private static final long serialVersionUID = 1L;

    @Override
    public Log getLog(ProcessingEvent processingEvent) {
        Log log = new Log();
        log.setCenter(processingEvent.getCenter().getNameShort());

        List<String> detailsList = new ArrayList<String>();

        detailsList.add(new StringBuilder("Source Specimens: ").append(
            getSpecimenCount(processingEvent)).toString());

        String worksheet = processingEvent.getWorksheet();
        if (worksheet != null) {
            detailsList.add(new StringBuilder("Worksheet: ").append(worksheet)
                .toString());
        }

        log.setDetails(StringUtil.join(detailsList, ", "));

        return log;
    }

    private int getSpecimenCount(ProcessingEvent processingEvent) {
        Collection<Specimen> specimens = processingEvent
            .getSpecimenCollection();
        return specimens == null ? 0 : specimens.size();
    }
}
