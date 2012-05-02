package edu.ualberta.med.biobank.model.type;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

@SuppressWarnings("nls")
public enum RequestState {
    NEW(0, Loader.i18n.tr("New")),
    SUBMITTED(1, Loader.i18n.tr("Submitted")),
    APPROVED(2, Loader.i18n.tr("Approved")),
    CLOSED(3, Loader.i18n.tr("Closed"));

    private Integer id;
    private String label;

    private RequestState(Integer id, String label) {
        this.id = id;
        this.label = label;
    }

    public static RequestState getState(Integer state) {
        if (state == null)
            return values()[0];
        for (RequestState dss : values()) {
            if (dss.getId().equals(state))
                return dss;
        }
        return null;
    }

    public boolean isEquals(Integer state) {
        return id.equals(state);
    }

    public Integer getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    public static class Loader {
        private static final I18n i18n = I18nFactory.getI18n(Loader.class);
    }
}
