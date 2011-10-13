package edu.ualberta.med.biobank.mvp;

import org.ops4j.peaberry.Peaberry;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.SimpleEventBus;

import edu.ualberta.med.biobank.mvp.presenter.impl.SiteEditPresenter;

public class Activator implements BundleActivator {

    // The shared instance
    private static Activator plugin;

    private Injector injector;

    @Override
    public void start(BundleContext context) throws Exception {
        plugin = this;

        System.out.println("MVP Bundle activated");

        // create a Guice module
        Module module = new AbstractModule() {

            @Override
            protected void configure() {
                bind(SiteEditPresenter.Display.class).toProvider(
                    Peaberry.service(SiteEditPresenter.Display.class).single());
                bind(EventBus.class).to(SimpleEventBus.class).in(
                    Singleton.class);
            }

        };

        // inject imported service proxy into the activator
        injector = Guice.createInjector(Peaberry.osgiModule(context), module);

    }

    @Override
    public void stop(BundleContext context) throws Exception {

    }

    /**
     * Returns the shared instance
     * 
     * @return the shared instance
     */
    public static Activator getDefault() {
        return plugin;
    }

    public static Injector getInjector() {
        return getDefault().injector;
    }

}
