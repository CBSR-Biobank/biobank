package edu.ualberta.med.biobank.mvp.view.item;

public class LongBox extends TranslatedTextBox<Long> {
    private static final LongAdapter LONG_ADAPTER = new LongAdapter();

    public LongBox() {
        super(LONG_ADAPTER);
    }

    private static class LongAdapter implements Adapter<Long, String> {
        @Override
        public Long adapt(String unadapted) {
            return Long.parseLong(unadapted);
        }

        @Override
        public String unadapt(Long adapted) {
            return adapted.toString();
        }
    };
}
