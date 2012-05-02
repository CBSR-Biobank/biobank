package edu.ualberta.med.biobank.model.type;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * Never remove one of these enum. Use deprecated if it should not be used
 * anymore.
 */
@SuppressWarnings("nls")
public enum DispatchSpecimenState implements ItemState {
    NONE(0, Loader.i18n.tr("None")),
    RECEIVED(1, Loader.i18n.tr("Received")),
    MISSING(2, Loader.i18n.tr("Missing")),
    EXTRA(3, Loader.i18n.tr("Extra"));

    private Integer id;
    private String label;

    private DispatchSpecimenState(Integer id, String label) {
        this.id = id;
        this.label = label;
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
    public boolean isEquals(Integer state) {
        return id.equals(state);
    }

    @Override
    public String toString() {
        return getLabel();
    }

    public static DispatchSpecimenState getState(Integer state) {
        for (DispatchSpecimenState das : values()) {
            if (das.isEquals(state)) {
                return das;
            }
        }
        return null;
    }

    public static class Loader {
        private static final I18n i18n = I18nFactory.getI18n(Loader.class);
    }
}
