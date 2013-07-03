package edu.ualberta.med.biobank.common.action.eventattr;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.ualberta.med.biobank.CommonBundle;
import edu.ualberta.med.biobank.common.formatters.DateFormatter;
import edu.ualberta.med.biobank.i18n.Bundle;
import edu.ualberta.med.biobank.i18n.LocalizedException;
import edu.ualberta.med.biobank.i18n.Tr;

public class EventAttrUtil {

    private static final Bundle bundle = new CommonBundle();

    @SuppressWarnings("nls")
    public static final Tr INVALID_STUDY_EVENT_ATTR_SINGLE_VALUE_ERRMSG =
        bundle.tr("Value \"{0}\" is invalid for label \"{2}\".");

    @SuppressWarnings("nls")
    public static final Tr INVALID_STUDY_EVENT_ATTR_MULTIPLE_VALUE_ERRMSG =
        bundle.tr("Value \"{0}\" (\"{1}\") is invalid for label \"{2}\".");

    @SuppressWarnings("nls")
    public static final Tr CANNOT_PARSE_DATE_ERRMSG =
        bundle.tr("Cannot parse date \"{0}\".");

    @SuppressWarnings("nls")
    public static final Tr UNKNOWN_EVENT_ATTR_TYPE_ERRMSG =
        bundle.tr("Unknown Event Attribute Type \"{0}\".");

    /**
     * Validates an event attribute value.
     * 
     * @param type
     * @param label
     * @param permissibleValues
     * @param value
     * 
     * @throws LocalizedException
     */
    @SuppressWarnings("nls")
    public static void validateValue(EventAttrTypeEnum type, String label,
        String permissibleValues, String value)
        throws LocalizedException {
        List<String> permValuesSplit = new ArrayList<String>(0);

        if (type.isSelectType()) {
            if (permissibleValues != null) {
                permValuesSplit = Arrays.asList(permissibleValues.split(";"));
            }
        }

        if (type == EventAttrTypeEnum.SELECT_SINGLE) {
            if (!permValuesSplit.contains(value)) {
                throw new LocalizedException(
                    INVALID_STUDY_EVENT_ATTR_SINGLE_VALUE_ERRMSG.format(value, label));
            }
        } else if (type == EventAttrTypeEnum.SELECT_MULTIPLE) {
            for (String singleVal : value.split(";")) {
                if (!permValuesSplit.contains(singleVal)) {
                    throw new LocalizedException(
                        INVALID_STUDY_EVENT_ATTR_MULTIPLE_VALUE_ERRMSG.format(
                            singleVal, value, label));
                }
            }
        } else if (type == EventAttrTypeEnum.NUMBER) {
            Double.parseDouble(value);
        } else if (type == EventAttrTypeEnum.DATE_TIME) {
            try {
                DateFormatter.dateFormatter.parse(value);
            } catch (ParseException e) {
                throw new LocalizedException(
                    CANNOT_PARSE_DATE_ERRMSG.format(value));
            }
        } else if (type == EventAttrTypeEnum.TEXT) {
            // do nothing
        } else {
            throw new LocalizedException(
                UNKNOWN_EVENT_ATTR_TYPE_ERRMSG.format(type.getName()));
        }

    }

}
