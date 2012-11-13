package edu.ualberta.med.biobank.model.util;

import java.math.BigDecimal;

public class MathUtil {
    public static boolean equals(BigDecimal a, BigDecimal b) {
        return a.stripTrailingZeros().equals(b.stripTrailingZeros());
    }
}
