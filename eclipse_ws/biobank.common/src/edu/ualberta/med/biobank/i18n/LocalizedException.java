package edu.ualberta.med.biobank.i18n;

import java.util.Arrays;

/**
 * 
 * @author Jonathan Ferland
 */
public class LocalizedException extends RuntimeException
    implements HasLocalizedString {
    private static final long serialVersionUID = 1L;

    private final LocalizedString localizedString;

    public LocalizedException(LocalizedString localizedString) {
        this(localizedString, null);
    }

    public LocalizedException(LocalizedString localizedString, Throwable cause) {
        super(qualifiedMessage(localizedString), cause);

        this.localizedString = localizedString;
    }

    @Override
    public LocalizedString getLocalizedString() {
        return localizedString;
    }

    @Override
    public String getLocalizedMessage() {
        return localizedString.toString();
    }

    @SuppressWarnings("nls")
    private static String qualifiedMessage(LocalizedString localizedString) {
        StringBuilder message = new StringBuilder();

        message.append(Arrays.toString(localizedString.getKey().toArray()));
        message.append(" => ");
        message.append(localizedString);

        return message.toString();
    }
}
