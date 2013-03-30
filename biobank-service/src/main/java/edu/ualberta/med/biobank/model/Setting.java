package edu.ualberta.med.biobank.model;

import java.io.Serializable;
import java.text.MessageFormat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.hibernate.envers.Audited;
import org.hibernate.validator.constraints.Length;

import edu.ualberta.med.biobank.model.util.CustomEnumType;

/**
 * Represents configurable database-wide or server-wide options.
 * 
 * @author Jonathan Ferland
 */
@Audited
@Entity
@Table(name = "SETTING")
public class Setting implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final int MAX_VALUE_LENGTH = 1000;

    /**
     * Change these enum's names all you want, but be careful about changing their {@link #id}, as
     * the {@link #id} is persisted and could be referenced by key name elsewhere, such as, through
     * scripts. The {@link #id} should remain <em>constant</em>.
     * 
     * @author Jonathan Ferland
     */
    public enum SettingKey {
        DATA_VERSION("DATA_VERSION"),
        MAX_ELEMENTS_IN_BATCH("MAX_ELEMENTS_IN_BATCH"),
        MAX_UPLOAD_SIZE_IN_BYTES("MAX_UPLOAD_SIZE_IN_BYTES");

        static {
            for (SettingKey key : SettingKey.values()) {
                if (key.getId().length() > SettingKey.MAX_KEY_LENGTH) {
                    throw new RuntimeException(MessageFormat.format(
                        "{0}.{1} exceeds max key length of {2}, so it cannot" +
                            " fit inside the database column. Please choose a" +
                            " smaller key (identifier).",
                        SettingKey.class.getName(),
                        key.name(),
                        SettingKey.MAX_KEY_LENGTH));
                }
            }
        }

        public static final int MAX_KEY_LENGTH = 127;

        private final String id;

        private SettingKey(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }
    }

    private SettingKey key;
    private String value;

    @NotNull(message = "{Setting.key.NotNull}")
    @Id
    @Type(type = "edu.ualberta.med.biobank.model.util.CustomEnumType",
        parameters = {
            @Parameter(
                name = CustomEnumType.ENUM_CLASS_NAME_PARAM,
                value = "edu.ualberta.med.biobank.model.Setting$SettingKey"
            )
        })
    @Column(name = "`KEY`", nullable = false, length = SettingKey.MAX_KEY_LENGTH)
    public SettingKey getKey() {
        return key;
    }

    public void setKey(SettingKey key) {
        this.key = key;
    }

    @NotNull(message = "{Setting.value.NotNull}")
    @Length(max = Setting.MAX_VALUE_LENGTH, message = "{Setting.value.Length}")
    @Column(name = "VALUE", nullable = false, length = Setting.MAX_VALUE_LENGTH)
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
