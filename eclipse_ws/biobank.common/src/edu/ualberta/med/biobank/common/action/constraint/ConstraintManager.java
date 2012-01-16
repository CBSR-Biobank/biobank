package edu.ualberta.med.biobank.common.action.constraint;

import java.util.Arrays;

import org.hibernate.cfg.Configuration;
import org.hibernate.event.PreUpdateEvent;
import org.hibernate.event.PreUpdateEventListener;

public class ConstraintManager {
    public ConstraintManager(Configuration configuration) {
        System.out.println(Arrays.toString(configuration.getEventListeners()
            .getPreUpdateEventListeners()));
        configuration.getEventListeners().setPreUpdateEventListeners(
            new PreUpdateEventListener[] { new PreUpdateHandler() });

        // configuration.setProperty("hibernate.show_sql", "true");
        // configuration.setProperty("hibernate.format_sql", "true");
        // configuration.setProperty("hibernate.use_sql_comments", "true");
        // configuration.setProperty("javax.persistence.validation.mode",
        // "none");

        configuration.setProperty("hibernate.check_nullability", "false");
    }

    static class PreUpdateHandler implements PreUpdateEventListener {
        private static final long serialVersionUID = 1L;

        @Override
        public boolean onPreUpdate(PreUpdateEvent event) {
            return false;
        }
    }
}
