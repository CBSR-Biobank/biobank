package edu.ualberta.med.biobank.auditor;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.persistence.DiscriminatorValue;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.model.Activity;
import edu.ualberta.med.biobank.model.Activity.Arg;

@SuppressWarnings("nls")
public enum ActivityTemplate {
    DID_SOMETHING(1,
        Loader.i18n.tr("Did something"),
        Loader.i18n.tr("Something was done on {0} and {1}."),
        // new DidSomething
        new Param());

    @DiscriminatorValue(ActivityTemplate.getId().toString())
    public static class Test {

    }

    public interface IActivity {

    }

    public static class DidSomething implements IActivity {

    }

    private final Integer id;
    private final String name;
    private final String template;
    private final List<Param> params;

    public Integer getId() {
        return id;
    }

    private ActivityTemplate(Integer id, String name, String template,
        Param... params) {
        this.id = id;
        this.name = name;
        this.template = template;
        this.params = Collections.unmodifiableList(Arrays.asList(params));
    }

    public static class Param {

    }

    public Activity create() {
        return null;
    }

    public String format(Activity activity) {
        return "";
    }

    public interface ArgFormatter {
        public String format(Activity activity, Arg arg);
    }

    public static class Loader {
        private static final I18n i18n = I18nFactory.getI18n(Loader.class);
    }
}
