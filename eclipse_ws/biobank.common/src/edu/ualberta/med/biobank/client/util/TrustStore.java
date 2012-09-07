package edu.ualberta.med.biobank.client.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

/**
 * Creates and maintains a custom trust store (via javax.net.ssl.trustStore)
 * that is alive only as long as the JVM is and can trust otherwise rejected
 * certificates.
 * 
 * @author Jonathan Ferland
 */
@SuppressWarnings("nls")
public final class TrustStore {
    private static final HostnameVerifier HOSTNAME_VERIFIER =
        HttpsURLConnection.getDefaultHostnameVerifier();
    private static final String DEFAULT_KEYSTORE_PATH =
        new File(System.getProperty("java.home"), "lib/security/cacerts")
            .getAbsolutePath();
    private static final String DEFAULT_KEYSTORE_PW = "changeit";
    private static final String TRUST_STORE_PROPERTY_NAME =
        "javax.net.ssl.trustStore";
    private static final String TRUST_STORE_PW_PROPERTY_NAME =
        "javax.net.ssl.trustStorePassword";
    private static final TrustStore instance = new TrustStore();

    private final SecureRandom random = new SecureRandom();
    private KeyStore ks;

    public static TrustStore getInstance() {
        return instance;
    }

    public class Cert {
        private final URL url;
        private final X509Certificate cert;

        private Cert(URL url, X509Certificate cert) {
            this.url = url;
            this.cert = cert;
        }

        public URL getUrl() {
            return url;
        }

        public X509Certificate getCertificate() {
            return cert;
        }

        public void trust() throws KeyStoreException, NoSuchAlgorithmException,
            CertificateException, IOException {
            String alias = getAlias(url);

            ks.setCertificateEntry(alias, cert);
            flush();
        }

        private String getAlias(URL url) throws KeyStoreException {
            String alias = url.getHost();
            for (int i = 1; ks.containsAlias(alias); i++) {
                alias = alias + "-" + i;
                if (i > 100) throw new RuntimeException("too many aliases");
            }
            return alias;
        }
    }

    public List<Cert> getUntrustedCerts(String urlString)
        throws KeyManagementException, NoSuchAlgorithmException,
        KeyStoreException, UnknownHostException, IOException {
        URL url = new URL(urlString);
        ArrayList<Cert> untrustedCerts = new ArrayList<Cert>();

        if ("https".equals(url.getProtocol())) {
            SSLContext context = SSLContext.getInstance("TLS");

            String alg = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(alg);
            tmf.init(ks);

            X509TrustManager tm = (X509TrustManager) tmf.getTrustManagers()[0];
            CustomTrustManager ctm = new CustomTrustManager(tm);
            context.init(null, new TrustManager[] { ctm }, null);
            SSLSocketFactory factory = context.getSocketFactory();

            String host = url.getHost();
            int port = (url.getPort() != -1) ? url.getPort() : 443;

            SSLSocket socket = (SSLSocket) factory.createSocket(host, port);
            socket.setSoTimeout(5000);

            try {
                verifyHost(socket, host);
            } catch (SSLException e) {
            } finally {
                socket.close();
            }

            List<X509Certificate> rejected = ctm.getRejectedIssuers();
            for (X509Certificate cert : rejected) {
                untrustedCerts.add(new Cert(url, cert));
            }
        }

        return untrustedCerts;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException("singleton");
    }

    private TrustStore() {
        try {
            this.ks = KeyStore.getInstance(KeyStore.getDefaultType());
            initKeyStore(ks);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String getPassword() {
        return new BigInteger(130, random).toString(32);
    }

    private void initKeyStore(KeyStore ks)
        throws IOException, NoSuchAlgorithmException, CertificateException,
        KeyStoreException {
        File tmp = File.createTempFile("biobank", ".keystore");
        tmp.deleteOnExit();

        InputStream in = new FileInputStream(DEFAULT_KEYSTORE_PATH);
        ks.load(in, DEFAULT_KEYSTORE_PW.toCharArray());
        in.close();

        System.setProperty(TRUST_STORE_PROPERTY_NAME, tmp.getAbsolutePath());

        flush();
    }

    private void flush()
        throws KeyStoreException, NoSuchAlgorithmException,
        CertificateException, IOException {
        String file = System.getProperty(TRUST_STORE_PROPERTY_NAME);

        String randPw = getPassword();
        System.setProperty(TRUST_STORE_PW_PROPERTY_NAME, randPw);

        OutputStream out = new FileOutputStream(file);
        out = new BufferedOutputStream(out);

        ks.store(out, randPw.toCharArray());
        out.close();
    }

    private void verifyHost(SSLSocket socket, String host)
        throws IOException {
        socket.startHandshake();

        SSLSession session = socket.getSession();
        if (session == null) {
            throw new SSLException("Cannot verify SSL socket without session");
        }

        if (!HOSTNAME_VERIFIER.verify(host, session)) {
            throw new SSLPeerUnverifiedException(
                "Unable to verify host: " + host);
        }
    }

    private static class CustomTrustManager
        implements X509TrustManager {
        private final X509TrustManager delegate;
        private X509Certificate[] chain;

        private CustomTrustManager(X509TrustManager delegate) {
            this.delegate = delegate;
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return delegate.getAcceptedIssuers();
        }

        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType)
            throws CertificateException {
            delegate.checkClientTrusted(chain, authType);
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType)
            throws CertificateException {
            this.chain = chain;
            delegate.checkServerTrusted(chain, authType);
        }

        public List<X509Certificate> getRejectedIssuers() {
            List<X509Certificate> list = new ArrayList<X509Certificate>(Arrays.asList(chain));
            list.removeAll(Arrays.asList(getAcceptedIssuers()));
            return list;
        }
    }
}
