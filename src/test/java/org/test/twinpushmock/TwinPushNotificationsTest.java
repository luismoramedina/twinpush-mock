package org.test.twinpushmock;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.glassfish.grizzly.http.server.HttpServer;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TwinPushNotificationsTest {

    String requestOk = "{ 'notification': {'alert': 'Mensaje a enviar.','devices_ids': ['2b3c4d5f6g']}}";
    String requestDeviceNotFound = "{ 'notification': {'alert': 'Mensaje que no se envia.','devices_ids': ['el4']}}";

    private HttpServer server;
    private WebTarget client;

    @Before
    public void setUp() throws Exception {
        // start the server
        server = ServerRunner.startServer(false);
        // create the client
        Client c = ClientBuilder.newClient();

        // uncomment the following line if you want to enable
        // support for JSON in the client (you also have to uncomment
        // dependency on jersey-media-json module in pom.xml and ServerRunner.startServer())
        // --
        // c.configuration().enable(new org.glassfish.jersey.media.json.JsonJaxbFeature());

        client = c.target(ServerRunner.APPS_URI);
    }

    @After
    public void tearDown() throws Exception {
        server.shutdown();
    }

    @Test
    public void testPostError403InvalidToken() throws IOException {
        Response post = client.path("app1/notifications").request().post(Entity.entity(requestOk, MediaType.APPLICATION_JSON_TYPE));
        assertEquals(403, post.getStatus());
        String data = readResponse(post);
        assertTrue(data.contains("InvalidToken"));
    }

    @Test
    public void testPostError404AppNotFound() throws IOException {
        MultivaluedMap<String, Object> map = new MultivaluedHashMap<>();
        map.putSingle("X-TwinPush-REST-API-Token", "TOKEN");
        Response post = client.path("appNOOOOOOOOOO/notifications").request().headers(map).post(Entity.entity(requestOk, MediaType.APPLICATION_JSON_TYPE));
        assertEquals(404, post.getStatus());
        String data = readResponse(post);
        assertTrue(data.contains("AppNotFound"));
    }

    @Test
    public void testPostError404DeviceNotFound() throws IOException {
        MultivaluedMap<String, Object> map = new MultivaluedHashMap<>();
        map.putSingle("X-TwinPush-REST-API-Token", "TOKEN");
        Response post = client.path("app1/notifications").request().headers(map).post(Entity.entity(requestDeviceNotFound, MediaType.APPLICATION_JSON_TYPE));
        assertEquals(404, post.getStatus());
        String data = readResponse(post);
        assertTrue(data.contains("DeviceNotFound"));
    }

    @Test
    public void testPostError422NotificationNotCreated() throws IOException {
        MultivaluedMap<String, Object> map = new MultivaluedHashMap<>();
        map.putSingle("X-TwinPush-REST-API-Token", "TOKEN");
        Response post = client.path("app1/notifications").request().headers(map).post(Entity.entity("", MediaType.APPLICATION_JSON_TYPE));
        assertEquals(422, post.getStatus());
        String data = readResponse(post);
        assertTrue(data.contains("NotificationNotCreated"));
    }

    @Test
    public void testPostOk() throws IOException {
        MultivaluedMap<String, Object> map = new MultivaluedHashMap<>();
        map.putSingle("X-TwinPush-REST-API-Token", "TOKEN");
        Response post = client.path("app1/notifications").request().headers(map).post(Entity.entity(requestOk, MediaType.APPLICATION_JSON_TYPE));
        String data = readResponse(post);
        assertTrue(data.contains("\"id\": \"app1\""));
        assertTrue(data.contains("\"alert\": \"Test!\""));
    }

    private String readResponse(Response post) throws IOException {
        InputStream entity = (InputStream) post.getEntity();
        byte[] b = new byte[entity.available()];
        entity.read(b);
        String data = new String(b);
        System.out.println("data:" + data);
        return data;
    }
}