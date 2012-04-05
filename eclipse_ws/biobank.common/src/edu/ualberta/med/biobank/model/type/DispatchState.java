package edu.ualberta.med.biobank.model.type;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

@SuppressWarnings("nls")
public enum DispatchState {
    CREATION(0, Loader.i18n.tr("Creation")),
    IN_TRANSIT(1, Loader.i18n.tr("In Transit")),
    RECEIVED(2, Loader.i18n.tr("Received")),
    CLOSED(3, Loader.i18n.tr("Closed")),
    LOST(4, Loader.i18n.tr("Lost"));

    private Integer id;
    private String label;

    private DispatchState(Integer id, String label) {
        this.id = id;
        this.label = label;
    }

    public static DispatchState getState(Integer state) {
        if (state == null)
            return CREATION;
        for (DispatchState dss : values()) {
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
