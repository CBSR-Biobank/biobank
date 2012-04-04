package edu.ualberta.med.biobank.i18n;

import java.io.Serializable;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

public class TransientI18n implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final I18n i18n = I18nFactory.getI18n(TransientI18n.class);

    private String message;

    public static void tr() {
    }

    public static void trn() {
    }

    public static void trc() {
    }

    public static void trnc() {
    }

    public static void show() {
    }

    public String getMessage() {
        return message;
    }
}
