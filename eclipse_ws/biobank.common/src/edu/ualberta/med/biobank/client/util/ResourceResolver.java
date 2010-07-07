package edu.ualberta.med.biobank.client.util;

import java.net.URL;

public interface ResourceResolver {

    public URL resolveURL(URL url) throws Exception;
}
