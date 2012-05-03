package edu.ualberta.med.biobank.model.type;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * The id of these enumerations are saved in the database. Therefore, DO NOT
 * CHANGE THESE ENUM IDS (unless you are prepared to write an upgrade script).
 * However, order and enum name can be modified freely.
 * <p>
 * Also, these enums should probably never be deleted, unless they are not used
 * in <em>any</em> database. Instead, they should be deprecated and probably
 * always return false when checking allow-ability.
 * 
 * @author Jonathan Ferland
 */
@SuppressWarnings("nls")
public enum DispatchSpecimenState implements ItemState {
    NONE(0, Loader.i18n.tr("None")),
    RECEIVED(1, Loader.i18n.tr("Received")),
    MISSING(2, Loader.i18n.tr("Missing")),
    EXTRA(3, Loader.i18n.tr("Extra"));

    private static final List<DispatchSpecimenState> VALUES_LIST = Collections
        .unmodifiableList(Arrays.asList(values()));

    public static List<DispatchSpecimenState> valuesList() {
        return VALUES_LIST;
    }

    public static DispatchSpecimenState fromId(Integer id) {
        for (DispatchSpecimenState item : values()) {
            if (item.id == id) return item;
        }
        return null;
    }

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
    public String toString() {
        return getLabel();
    }

    public static class Loader {
        private static final I18n i18n = I18nFactory.getI18n(Loader.class);
    }
}
