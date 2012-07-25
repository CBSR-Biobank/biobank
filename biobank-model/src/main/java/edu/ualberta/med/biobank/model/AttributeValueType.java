package edu.ualberta.med.biobank.model;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.model.util.NotAProxy;

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
public enum AttributeValueType implements NotAProxy, Serializable {
    STRING(0, Loader.i18n.tr("String")),
    NUMBER_INTEGER(1, Loader.i18n.tr("Number (integer)")),
    NUMBER_DECIMAL(2, Loader.i18n.tr("Number (decimal)")),
    BOOLEAN(3, Loader.i18n.tr("Checkbox")),
    SELECT_SINGLE(4, Loader.i18n.tr("Select (single)")),
    SELECT_MULTIPLE(5, Loader.i18n.tr("Select (multiple)"));

    private static final List<AttributeValueType> VALUES_LIST = Collections
        .unmodifiableList(Arrays.asList(values()));

    public static List<AttributeValueType> valuesList() {
        return VALUES_LIST;
    }

    public static AttributeValueType fromId(Integer id) {
        for (AttributeValueType item : values()) {
            if (item.id.equals(id)) return item;
        }
        return null;
    }

    private Integer id;
    private String label;

    private AttributeValueType(Integer id, String label) {
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

    public static class Loader {
        private static final I18n i18n = I18nFactory.getI18n(Loader.class);
    }
}
