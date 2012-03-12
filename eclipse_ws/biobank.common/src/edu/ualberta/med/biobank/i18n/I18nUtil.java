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

        @Override
        public int hashCode() {
            final int prime = 31;
            int i = 1;
            i = prime * i + ((collection == null) ? 0 : collection.hashCode());
            i = prime * i + ((delimiter == null) ? 0 : delimiter.hashCode());
            return i;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            JoinedCollection other = (JoinedCollection) obj;
            if (collection == null) {
                if (other.collection != null) return false;
            } else if (!collection.equals(other.collection)) return false;
            if (delimiter == null) {
                if (other.delimiter != null) return false;
            } else if (!delimiter.equals(other.delimiter)) return false;
            return true;
        }
    }
}
