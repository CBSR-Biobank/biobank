package edu.ualberta.med.biobank.i18n;

import java.util.Collection;

import edu.ualberta.med.biobank.common.util.StringUtil;

public class I18nUtil {
    public static <T> L10nedMessage join(final Collection<T> collection) {
        return new JoinedCollection(collection);
    }

    static class JoinedCollection extends AbstractLazyL10nedMessage {
        private static final long serialVersionUID = 1L;

        private final Collection<?> collection;
        private final L10nedMessage delimiter;

        public JoinedCollection(Collection<?> c, L10nedMessage delimiter) {
            this.collection = c;
            this.delimiter = delimiter;
        }

        public JoinedCollection(Collection<?> c) {
            this(c, CommonMessages.LIST_ITEM_DELIMITER);
        }

        @Override
        protected String loadMessage() {
            return StringUtil.join(collection, delimiter.getMessage());
        }
    }
}
