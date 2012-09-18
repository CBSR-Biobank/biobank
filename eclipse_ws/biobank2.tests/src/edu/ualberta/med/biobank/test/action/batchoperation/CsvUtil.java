package edu.ualberta.med.biobank.test.action.batchoperation;

import org.slf4j.Logger;

import edu.ualberta.med.biobank.common.action.exception.BatchOpErrorsException;
import edu.ualberta.med.biobank.common.action.exception.BatchOpException;
import edu.ualberta.med.biobank.i18n.LString;

/**
 * 
 * @author Nelson Loyola
 * 
 */
public class CsvUtil {

    public static void showErrorsInLog(Logger log, BatchOpErrorsException e) {
        for (BatchOpException<LString> ie : e.getErrors()) {
            log.error("ERROR: line no {}: {}", ie.getLineNumber(),
                ie.getMessage());
        }

    }

}
