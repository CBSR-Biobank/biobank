package edu.ualberta.med.biobank.test.action.csvimport;

import org.slf4j.Logger;

import edu.ualberta.med.biobank.common.action.exception.BatchOpErrorsException;
import edu.ualberta.med.biobank.common.action.exception.BatchOpErrorsException.ImportError;

/**
 * 
 * @author loyola
 * 
 */
public class CsvUtil {

    public static void showErrorsInLog(Logger log, BatchOpErrorsException e) {
        for (ImportError ie : e.getErrors()) {
            log.error("ERROR: line no {}: {}", ie.getLineNumber(),
                ie.getMessage());
        }

    }

}
