package edu.ualberta.med.biobank.common;

import java.net.URL;

public interface ResourceResolver {

    public URL resolveURL(URL url) throws Exception;
}
