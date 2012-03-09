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
        String message = delegate.getL10nedMessage();
        String evaluated = OgnlMessageFormatter.format(message, root);
        return evaluated;
    }
}
