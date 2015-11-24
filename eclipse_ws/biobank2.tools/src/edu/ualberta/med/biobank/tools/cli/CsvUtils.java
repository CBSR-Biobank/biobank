package edu.ualberta.med.biobank.tools.cli;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.ualberta.med.biobank.common.action.exception.BatchOpErrorsException;
import edu.ualberta.med.biobank.common.action.exception.BatchOpException;
import edu.ualberta.med.biobank.i18n.LString;

public class CsvUtils {

    private static final Logger LOG = LoggerFactory.getLogger(CsvUtils.class.getName());

    public static void showErrorsInLog(BatchOpErrorsException e) {
        for (BatchOpException<LString> ie : e.getErrors()) {
            LOG.error("ERROR: line no {}: {}", ie.getLineNumber(),
                ie.getMessage());
        }
    }
}
