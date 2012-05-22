package edu.ualberta.med.biobank.tools.utils;

@SuppressWarnings("nls")
public class HostUrl {

    public static String getHostUrl(String hostname, int port) {

        String hostUrl;
        if (port == 8080) {
            hostUrl = "http://" + hostname + ":8080";
        } else {
            hostUrl = "https://" + hostname + ":" + port;
        }
        hostUrl += "/biobank";
        return hostUrl;
    }

}
