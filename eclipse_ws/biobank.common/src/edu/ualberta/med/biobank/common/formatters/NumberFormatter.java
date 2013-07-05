package edu.ualberta.med.biobank.common.formatters;

import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Locale;

public class NumberFormatter {
    public static final NumberFormat defaultNberFormatter = NumberFormat
        .getNumberInstance(Locale.getDefault());
    {
        defaultNberFormatter.setRoundingMode(RoundingMode.UNNECESSARY);
    }

    public static final NumberFormat currencyFormatter = NumberFormat
        .getCurrencyInstance(Locale.getDefault());

    public static final NumberFormat perCentFormatter = NumberFormat
        .getPercentInstance(Locale.getDefault());

    static {
        perCentFormatter.setMinimumFractionDigits(2);
    }

    public static String format(Number nb) {
        if (nb == null) {
            return ""; //$NON-NLS-1$
        }
        return defaultNberFormatter.format(nb);
    }

    public static String formatCurrency(Number nb) {
        if (nb == null) {
            return ""; //$NON-NLS-1$
        }
        return currencyFormatter.format(nb);
    }

    public static String formatPerCent(Number nb) {
        if (nb == null) {
            return ""; //$NON-NLS-1$
        }
        return perCentFormatter.format(nb);
    }
}
