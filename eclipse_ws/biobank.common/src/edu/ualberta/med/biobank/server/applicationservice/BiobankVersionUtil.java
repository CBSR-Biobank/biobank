package edu.ualberta.med.biobank.server.applicationservice;

import edu.ualberta.med.biobank.server.applicationservice.exceptions.ClientVersionInvalidException;
import edu.ualberta.med.biobank.server.applicationservice.exceptions.ServerVersionInvalidException;
import edu.ualberta.med.biobank.server.applicationservice.exceptions.ServerVersionNewerException;
import edu.ualberta.med.biobank.server.applicationservice.exceptions.ServerVersionOlderException;
import edu.ualberta.med.biobank.server.applicationservice.exceptions.VersionInvalidException;
import gov.nih.nci.system.applicationservice.ApplicationException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;

public class BiobankVersionUtil {
    public static final String SERVER_VERSION_PROP_FILE = "version.properties"; //$NON-NLS-1$

    public static final String SERVER_VERSION_PROP_KEY = "server.version"; //$NON-NLS-1$

    public static SwVersion serverVersion = null;

    private static Properties props = null;

    private static Logger log = Logger.getLogger(BiobankVersionUtil.class
        .getName());

    static {
        props = new Properties();
        try {
            props.load(BiobankApplicationServiceImpl.class
                .getResourceAsStream(SERVER_VERSION_PROP_FILE));
        } catch (FileNotFoundException e) {
            log.error("file " + SERVER_VERSION_PROP_FILE + " not found.", e); //$NON-NLS-1$ //$NON-NLS-2$
        } catch (IOException e) {
            log.error("Problem with file " + SERVER_VERSION_PROP_FILE, e); //$NON-NLS-1$
        }
    }

    public static void checkVersion(String clientVersionStr)
        throws ApplicationException {
        if (props == null) {
            log.error("server does not have a version"); //$NON-NLS-1$
            throw new ServerVersionInvalidException(
                Messages
                    .getString("BiobankVersionUtil.server_version_error_msg")); //$NON-NLS-1$
        }

        String serverVersionStr = props.getProperty(SERVER_VERSION_PROP_KEY);

        if (serverVersionStr == null) {
            log.error("server does not have a version"); //$NON-NLS-1$
            throw new ServerVersionInvalidException(
                Messages
                    .getString("BiobankVersionUtil.server_version_error_msg")); //$NON-NLS-1$
        }

        try {
            serverVersion = new SwVersion(serverVersionStr);

            if (clientVersionStr == null) {
                log.error("client does not have a version"); //$NON-NLS-1$
                throw new ClientVersionInvalidException(
                    Messages.getString("BiobankVersionUtil.compatibility_error_msg")); //$NON-NLS-1$
            }

            try {
                SwVersion clientVersion = new SwVersion(clientVersionStr);

                log.info("check version: server_version/" + serverVersionStr //$NON-NLS-1$
                    + " client_version/" + clientVersionStr); //$NON-NLS-1$

                if (clientVersion.getMajor() < serverVersion.getMajor()) {
                    throw new ServerVersionNewerException(
                        Messages.getString("BiobankVersionUtil.tooold_error_msg")); //$NON-NLS-1$
                } else if (clientVersion.getMajor() > serverVersion.getMajor()) {
                    throw new ServerVersionOlderException(
                        Messages.getString("BiobankVersionUtil.toonew_error_msg")); //$NON-NLS-1$
                } else {
                    if (clientVersion.getMinor() < serverVersion.getMinor()) {
                        throw new ServerVersionNewerException(
                            Messages.getString("BiobankVersionUtil.tooold_error_msg")); //$NON-NLS-1$
                    } else if (clientVersion.getMinor() > serverVersion
                        .getMinor()) {
                        throw new ServerVersionOlderException(
                            Messages.getString("BiobankVersionUtil.toonew_error_msg")); //$NON-NLS-1$
                    }
                }
            } catch (VersionInvalidException e) {
                throw new ClientVersionInvalidException(
                    Messages.getString("BiobankVersionUtil.compatibility_error_msg")); //$NON-NLS-1$
            }
        } catch (VersionInvalidException e) {
            throw new ServerVersionInvalidException(
                Messages
                    .getString("BiobankVersionUtil.server_version_error_msg")); //$NON-NLS-1$
        }
    }

    public static String getServerVersion() {
        return props.getProperty(SERVER_VERSION_PROP_KEY);
    }
}
