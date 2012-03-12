package edu.ualberta.med.biobank.i18n;

public class OgnlL10nedMessage extends AbstractLazyL10nedMessage {
    private static final long serialVersionUID = 1L;

    private final L10nedMessage delegate;
    private final Object root;

    public OgnlL10nedMessage(L10nedMessage delegate, Object root) {
        this.delegate = delegate;
        this.root = root;
    }

    @Override
    protected String loadMessage() {
        String message = delegate.getMessage();
        String evaluated = OgnlMessageFormatter.format(message, root);
        return evaluated;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int i = 1;
        i = prime * i + ((delegate == null) ? 0 : delegate.hashCode());
        i = prime * i + ((root == null) ? 0 : root.hashCode());
        return i;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        OgnlL10nedMessage other = (OgnlL10nedMessage) obj;
        if (delegate == null) {
            if (other.delegate != null) return false;
        } else if (!delegate.equals(other.delegate)) return false;
        if (root == null) {
            if (other.root != null) return false;
        } else if (!root.equals(other.root)) return false;
        return true;
    }
}
