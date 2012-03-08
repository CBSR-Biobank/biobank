package edu.ualberta.med.biobank.i18n;

public class OgnlI18dMessage extends AbstractI18dMessage {
    private static final long serialVersionUID = 1L;

    private final I18dMessage delegate;
    private final Object root;

    public OgnlI18dMessage(I18dMessage delegate, Object root) {
        this.delegate = delegate;
        this.root = root;
    }

    @Override
    protected String loadMessage() {
        String message = delegate.getI18dMessage();
        String evaluated = OgnlMessageFormatter.format(message, root);
        return evaluated;
    }
}
