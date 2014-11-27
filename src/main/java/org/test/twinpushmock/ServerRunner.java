package org.test.twinpushmock;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.ssl.SSLContextConfigurator;
import org.glassfish.grizzly.ssl.SSLEngineConfigurator;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.net.URI;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * ServerRunner class.
 */
public class ServerRunner {
    // Base URI the Grizzly HTTP server will listen on

    //https://app.twinpush.com/api/v2/apps/%APPID%/notifications
    public static final String BASE_URI = "http://0.0.0.0:8081/";
    public static final String APPS_URI = BASE_URI + "api/v2/apps/";

    private static class TrustAllCerts implements X509TrustManager {
        @Override
        public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }

    private final static TrustManager[] trustAllCerts = new TrustManager[]{new TrustAllCerts()};

    public static SSLEngineConfigurator getSslEngineConfig() throws KeyManagementException, NoSuchAlgorithmException {
        SSLEngineConfigurator sslEngineConfigurator;
//        SSLContextConfigurator sc = null;
//        sc = getSSLContext();
        SSLContext sc = SSLContext.getInstance("TLSv1.2");
        sc.init(null, trustAllCerts, new SecureRandom());
        sslEngineConfigurator = new SSLEngineConfigurator(sc, false, true, false);

        return sslEngineConfigurator;
    }

    /**
     * Starts Grizzly HTTP server exposing JAX-RS resources defined in this application.
     * @return Grizzly HTTP server.
     */
    public static HttpServer startServer(boolean secure) throws NoSuchAlgorithmException, KeyManagementException {
        // create a resource config that scans for JAX-RS resources and providers
        // in com.example package
        final ResourceConfig rc = new ResourceConfig().packages("org.test.twinpushmock.service");

        // create and start a new instance of grizzly http server
        // exposing the Jersey application at BASE_URI
        HttpServer httpServer;
        if (!secure) {
            httpServer = GrizzlyHttpServerFactory.createHttpServer(URI.create(APPS_URI), rc);
        } else {
            httpServer = GrizzlyHttpServerFactory.createHttpServer(URI.create(APPS_URI), rc, true, getSslEngineConfig());
        }
        return httpServer;
    }

    private static SSLContextConfigurator getSSLContext() {
        SSLContextConfigurator sslContext = new SSLContextConfigurator();

        // set up security context
        sslContext.setKeyStoreFile("c:\\data\\temp\\twinpush-mock.jks"); // contains server keypair
        sslContext.setKeyStorePass("123123123");
        sslContext.setTrustStoreFile("c:\\data\\temp\\twinpush-mock.jks"); // contains client certificate
        sslContext.setTrustStorePass("123123123");
        return sslContext;
    }

    public static void main(String[] args) throws Exception {

        final HttpServer server = startServer(false);
        System.out.println(String.format("Jersey app started with WADL available at "
                + "%sapplication.wadl\nHit enter to stop it...", APPS_URI));
        System.in.read();
        server.shutdown();
    }
}

