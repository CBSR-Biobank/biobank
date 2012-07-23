package edu.ualberta.med.biobank.test.action.csvimport;

import org.slf4j.Logger;

import edu.ualberta.med.biobank.common.action.exception.CsvImportException;
import edu.ualberta.med.biobank.common.action.exception.CsvImportException.ImportError;

public class CsvUtil {

    public static void showErrorsInLog(Logger log, CsvImportException e) {
        for (ImportError ie : e.getErrors()) {
            log.error("ERROR: line no {}: {}", ie.getLineNumber(),
                ie.getMessage());
        }

    }

}
