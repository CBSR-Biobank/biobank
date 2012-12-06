package edu.ualberta.med.biobank;


import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.NCSARequestLog;
import org.eclipse.jetty.server.RequestLog;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.RequestLogHandler;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.util.thread.ThreadPool;
import org.eclipse.jetty.webapp.WebAppContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BiobankServer {
    private static final Logger log = LoggerFactory.getLogger(BiobankServer.class);

    // TODO: You should configure this appropriately for
    // your environment
    private static final String LOG_PATH = "logs/access/yyyy_mm_dd.request.log";

    private static final String WEB_XML = "webapp/WEB-INF/web.xml";

    public static interface WebContext {
        public File getWarPath();
        public String getContextPath();
    }

    public static void main(final String[] arguments) {
        final Server server = new Server(8080);
        server.setThreadPool(createThreadPool());
        server.setHandler(createHandlers());
        server.setStopAtShutdown(true);

        try {
            server.start();
            server.join();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private static ThreadPool createThreadPool() {
        // TODO: You should configure these appropriately
        // for your environment - this is an example only
        QueuedThreadPool _threadPool = new QueuedThreadPool();
        _threadPool.setMinThreads(10);
        _threadPool.setMaxThreads(100);
        return _threadPool;
    }

    private static HandlerCollection createHandlers() {
        WebAppContext context = new WebAppContext();
        context.setContextPath("/");

        context.setWar(getShadedWarUrl());

        List<Handler> handlers = new ArrayList<Handler>();

        handlers.add(context);

        HandlerList contexts = new HandlerList();
        contexts.setHandlers(handlers.toArray(new Handler[0]));

        RequestLogHandler log = new RequestLogHandler();
        log.setRequestLog(createRequestLog());

        HandlerCollection result = new HandlerCollection();
        result.setHandlers(new Handler[] {contexts, log});

        return result;
    }

    private static RequestLog createRequestLog() {
        NCSARequestLog log = new NCSARequestLog();

        File logPath = new File(LOG_PATH);
        logPath.getParentFile().mkdirs();

        log.setFilename(logPath.getPath());
        log.setRetainDays(90);
        log.setExtended(false);
        log.setAppend(true);
        log.setLogTimeZone("GMT");
        log.setLogLatency(true);
        return log;
    }

    private static URL getResource(String aResource) {
        return Thread.currentThread().getContextClassLoader().getResource(aResource);
    }

    private static String getShadedWarUrl() {
        String urlStr = getResource(WEB_XML).toString();
        // Strip off "WEB-INF/web.xml"
        return urlStr.substring(0, urlStr.length() - 15);
    }
}
