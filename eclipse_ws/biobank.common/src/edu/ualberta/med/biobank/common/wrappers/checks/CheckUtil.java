package edu.ualberta.med.biobank.common.wrappers.checks;

import java.util.List;

class CheckUtil {
    static Long getCountFromResult(List<?> results) {
        Long count = null;
        if (results != null && results.size() == 1
            && (results.get(0) instanceof Number)) {
            count = new Long(((Number) results.get(0)).longValue());
        }
        return count;
    }
}
