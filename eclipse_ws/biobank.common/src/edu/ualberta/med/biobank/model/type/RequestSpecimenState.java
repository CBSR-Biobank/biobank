package edu.ualberta.med.biobank.model.type;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * Never remove one of these enum. Use deprecated if it should not be used
 * anymore.
 */
@SuppressWarnings("nls")
public enum RequestSpecimenState implements ItemState {
    AVAILABLE_STATE(0, Loader.i18n.tr("Available")),
    PULLED_STATE(1, Loader.i18n.tr("Pulled")),
    UNAVAILABLE_STATE(2, Loader.i18n.tr("Unavailable")),
    DISPATCHED_STATE(3, Loader.i18n.tr("Dispatched"));

    private Integer id;
    private String label;

    private RequestSpecimenState(Integer id, String label) {
        this.id = id;
        this.label = label;
    }

    public static RequestSpecimenState getState(Integer state) {
        for (RequestSpecimenState das : values()) {
            if (das.isEquals(state)) {
                return das;
            }
        }
        return null;
    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public String toString() {
        return getLabel();
    }

    @Override
    public boolean isEquals(Integer state) {
        return id.equals(state);
    }

    public static class Loader {
        private static final I18n i18n = I18nFactory.getI18n(Loader.class);
    }
}
