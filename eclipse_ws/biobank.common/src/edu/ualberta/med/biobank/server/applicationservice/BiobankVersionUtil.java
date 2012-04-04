package edu.ualberta.med.biobank.server.applicationservice;

import edu.ualberta.med.biobank.server.applicationservice.exceptions.ClientVersionInvalidException;
import edu.ualberta.med.biobank.server.applicationservice.exceptions.ServerVersionInvalidException;
import edu.ualberta.med.biobank.server.applicationservice.exceptions.ServerVersionNewerException;
import edu.ualberta.med.biobank.server.applicationservice.exceptions.ServerVersionOlderException;
import edu.ualberta.med.biobank.server.applicationservice.exceptions.VersionFormatInvalidException;
import gov.nih.nci.system.applicationservice.ApplicationException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;

public class BiobankVersionUtil {
    public static final String SERVER_VERSION_PROP_FILE = "version.properties"; 

    public static final String SERVER_VERSION_PROP_KEY = "server.version"; 

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
            log.error("file " + SERVER_VERSION_PROP_FILE + " not found.", e);  
        } catch (IOException e) {
            log.error("Problem with file " + SERVER_VERSION_PROP_FILE, e); 
        }
    }

    public static void checkVersion(String clientVersionStr)
        throws ApplicationException {
        if (props == null) {
            log.error("server does not have a version"); 
            throw new ServerVersionInvalidException();
        }

        String serverVersionStr = props.getProperty(SERVER_VERSION_PROP_KEY);

        if (serverVersionStr == null) {
            log.error("server does not have a version"); 
            throw new ServerVersionInvalidException();
        }

        try {
            serverVersion = new SwVersion(serverVersionStr);

            if (clientVersionStr == null) {
                log.error("client does not have a version"); 
                throw new ClientVersionInvalidException();
            }

            try {
                SwVersion clientVersion = new SwVersion(clientVersionStr);

                log.info("check version: server_version/" + serverVersionStr 
                    + " client_version/" + clientVersionStr); 

                if (clientVersion.getMajor() < serverVersion.getMajor()) {
                    throw new ServerVersionNewerException(
                        serverVersion.toString(), clientVersion.toString());
                } else if (clientVersion.getMajor() > serverVersion.getMajor()) {
                    throw new ServerVersionOlderException(
                        serverVersion.toString(), clientVersion.toString());
                } else {
                    if (clientVersion.getMinor() < serverVersion.getMinor()) {
                        throw new ServerVersionNewerException(
                            serverVersion.toString(), clientVersion.toString());
                    } else if (clientVersion.getMinor() > serverVersion
                        .getMinor()) {
                        throw new ServerVersionOlderException(
                            serverVersion.toString(), clientVersion.toString());
                    }
                }
            } catch (VersionFormatInvalidException e) {
                throw new ClientVersionInvalidException();
            }
        } catch (VersionFormatInvalidException e) {
            throw new ServerVersionInvalidException();
        }
    }

    public static String getServerVersion() {
        return props.getProperty(SERVER_VERSION_PROP_KEY);
    }
}
