package edu.ualberta.med.biobank.server.applicationservice;

import edu.ualberta.med.biobank.server.applicationservice.exceptions.ClientVersionInvalidException;
import edu.ualberta.med.biobank.server.applicationservice.exceptions.ServerVersionInvalidException;
import edu.ualberta.med.biobank.server.applicationservice.exceptions.ServerVersionNewerException;
import edu.ualberta.med.biobank.server.applicationservice.exceptions.ServerVersionOlderException;
import gov.nih.nci.system.applicationservice.ApplicationException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;

public class BiobankVersionUtil {
    public static final String SERVER_VERSION_PROP_FILE = "version.properties";

    public static final String SERVER_VERSION_PROP_KEY = "server.version";

    public static int[] serverVersionArr = null;

    private static Properties props = null;

    private static Logger log = Logger.getLogger(BiobankVersionUtil.class
        .getName());

    static {
        props = new Properties();
        try {
            props.load(BiobankApplicationServiceImpl.class
                .getResourceAsStream(SERVER_VERSION_PROP_FILE));
        } catch (FileNotFoundException e) {
            log.error("file " + SERVER_VERSION_PROP_FILE + " not found.", e);
        } catch (IOException e) {
            log.error("Problem with file " + SERVER_VERSION_PROP_FILE, e);
        }
    }

    private static void serverVersionStrToIntArray(String version) {
        if (serverVersionArr != null)
            return;
        serverVersionArr = versionStrToIntArray(version);
    }

    public static void checkVersion(String clientVersion)
        throws ApplicationException {
        if (props == null) {
            log.error("server does not have a version");
            throw new ServerVersionInvalidException(
                "The server version could not be determined.");
        }

        String serverVersion = props.getProperty(SERVER_VERSION_PROP_KEY);

        if (serverVersion == null) {
            log.error("server does not have a version");
            throw new ServerVersionInvalidException(
                "The server version could not be determined.");
        }

        serverVersionStrToIntArray(serverVersion);
        if (serverVersionArr == null) {
            throw new ServerVersionInvalidException(
                "The server version could not be determined.");
        }

        if (clientVersion == null) {
            log.error("client does not have a version");
            throw new ClientVersionInvalidException(
                "Client authentication failed. "
                    + "The Java Client version is not compatible with the server and must be upgraded.");
        }

        int[] clientVersionArr = versionStrToIntArray(clientVersion);
        if (clientVersionArr == null) {
            throw new ClientVersionInvalidException(
                "The Java Client version is not valid.");
        }

        log.info("check version: server_version/" + serverVersion
            + " client_version/" + clientVersion);

        if (clientVersionArr[0] < serverVersionArr[0]) {
            throw new ServerVersionNewerException(
                "Client authentication failed. "
                    + "The Java Client version is too old to connect to this server.");
        } else if (clientVersionArr[0] > serverVersionArr[0]) {
            throw new ServerVersionOlderException(
                "Client authentication failed. "
                    + "The Java Client version is too new to connect to this server.");
        } else {
            if (clientVersionArr[1] < serverVersionArr[1]) {
                throw new ServerVersionNewerException(
                    "Client authentication failed. "
                        + "The Java Client version is too old to connect to this server.");
            } else if (clientVersionArr[1] > serverVersionArr[1]) {
                throw new ServerVersionOlderException(
                    "Client authentication failed. "
                        + "The Java Client version is too new to connect to this server.");
            }
        }
    }

    public static String getServerVersion() {
        return props.getProperty(SERVER_VERSION_PROP_KEY);
    }

    private static int[] versionStrToIntArray(String version) {
        String[] versionSplit = version.split("\\.");

        if ((versionSplit.length != 3) && (versionSplit.length != 4)) {
            // split length is invalid
            return null;
        }

        int[] result = new int[versionSplit.length];

        for (int i = 0; i < versionSplit.length; i++) {
            if (i < 3) {
                try {
                    result[i] = Integer.parseInt(versionSplit[i]);
                } catch (NumberFormatException e) {
                    return null;
                }
            } else if ((i == 3) && !versionSplit[3].equals("pre")) {
                return null;
            }
        }
        return result;
    }

}
