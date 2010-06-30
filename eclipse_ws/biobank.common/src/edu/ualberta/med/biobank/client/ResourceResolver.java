package edu.ualberta.med.biobank.client;

import java.net.URL;

public interface ResourceResolver {

    public URL resolveURL(URL url) throws Exception;
}
