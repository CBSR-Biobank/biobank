package edu.ualberta.med.biobank.model.type;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.common.util.NotAProxy;

/**
 * The id of these enumerations are saved in the database. Therefore, DO NOT CHANGE THESE ENUM IDS
 * (unless you are prepared to write an upgrade script). However, order and enum name can be
 * modified freely.
 * <p>
 * Also, these enums should probably never be deleted, unless they are not used in <em>any</em>
 * database. Instead, they should be deprecated and probably always return false when checking
 * allow-ability.
 * 
 * @author Nelson Loyola
 */
@SuppressWarnings("nls")
public enum LabelingLayout implements Serializable, NotAProxy {
    VERTICAL(0, Loader.i18n.tr("Vertical")),
    HORIZONTAL(0, Loader.i18n.tr("Horizontal"));

    private static final List<LabelingLayout> VALUES_LIST = Collections
        .unmodifiableList(Arrays.asList(values()));

    public static List<LabelingLayout> valuesList() {
        return VALUES_LIST;
    }

    public static LabelingLayout fromId(Integer id) {
        if (id == null) return VERTICAL;
        for (LabelingLayout item : values()) {
            if (item.id.equals(id)) return item;
        }
        return null;
    }

    private LabelingLayout(Integer id, String label) {
        this.id = id;
        this.label = label;
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

    private Integer id;
    private String label;

    public static class Loader {
        private static final I18n i18n = I18nFactory.getI18n(Loader.class);
    }
}
