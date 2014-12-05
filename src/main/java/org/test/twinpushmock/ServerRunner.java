package org.test.twinpushmock;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.ssl.SSLContextConfigurator;
import org.glassfish.grizzly.ssl.SSLEngineConfigurator;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
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
    public static final String LISTEN_IP = "0.0.0.0";
    public static final String APPS_URI = "/api/v2/apps/";

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
    public static HttpServer startServer(boolean secure, int port) throws NoSuchAlgorithmException, KeyManagementException {
        // create a resource config that scans for JAX-RS resources and providers
        // in com.example package
        final ResourceConfig rc = new ResourceConfig().packages("org.test.twinpushmock.service");

        // create and start a new instance of grizzly http server
        // exposing the Jersey application at LISTEN_IP
        HttpServer httpServer;
        String appsUri = buildUri(port);
        if (!secure) {
            httpServer = GrizzlyHttpServerFactory.createHttpServer(URI.create(appsUri), rc);
        } else {
            httpServer = GrizzlyHttpServerFactory.createHttpServer(URI.create(appsUri), rc, true, getSslEngineConfig());
        }
        return httpServer;
    }

    public static String buildUri(int port) {
        return "http://" + LISTEN_IP + ":" + port + APPS_URI;
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
        int port = 8082;
        final HttpServer server = startServer(false, port);
        System.out.println(String.format("Jersey app started with WADL available at "
                + "%sapplication.wadl\nHit enter to stop it...", buildUri(port)));
        System.in.read();
        server.shutdown();
    }
}

