package edu.ualberta.med.biobank.model;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.common.util.NotAProxy;

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
public enum ActivityStatus implements NotAProxy, Serializable {
    ACTIVE(1, Loader.i18n.tr("Active")),
    CLOSED(2, Loader.i18n.tr("Closed")),
    // TODO: why can't there be a closed and flagged item or an active but
    // flagged item? Especially for users that are mean to be enabled or
    // disabled. When is ActivityStatus.FLAGGED even used in the source code?
    // Can a flagged user log in? What is the point of flagged? Shouldn't it be
    // separate?
    FLAGGED(4, Loader.i18n.tr("Flagged"));

    private static final List<ActivityStatus> VALUES_LIST = Collections
        .unmodifiableList(Arrays.asList(values()));

    public static final String NAME = Loader.i18n.trc(
        "model",
        "Activity Status");

    private final int id;
    private final String name;

    private ActivityStatus(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public static List<ActivityStatus> valuesList() {
        return VALUES_LIST;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public static ActivityStatus fromId(int id) {
        for (ActivityStatus item : values()) {
            if (item.id == id) {
                return item;
            }
        }
        return null;
    }

    public static class Loader {
        private static final I18n i18n = I18nFactory.getI18n(Loader.class);
    }
}
