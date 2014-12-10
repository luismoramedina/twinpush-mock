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

    String requestForceNotificationNotCreatedOldSchool = "{ 'notification': {'alert': 'Nunca se enviara','devices_ids': ['error:NotificationNotCreated']}}";
    String requestForceNotificationAppNotFoundOldSchool = "{ 'notification': {'alert': 'Nunca se enviara','devices_ids': ['error:AppNotFound']}}";
    String requestOkOldSchool = "{ 'notification': {'alert': 'Mensaje a enviar.','devices_ids': ['2b3c4d5f6g']}}";
    String requestDeviceNotFoundOldSchool = "{ 'notification': {'alert': 'Mensaje que no se envia.','devices_ids': ['el4']}}";

    String requestForceInvalidCreatorToken = "{'alert': 'Nunca se enviara','devices_ids': ['error:InvalidCreatorToken']}";
    String requestForceNotificationNotCreated = "{'alert': 'Nunca se enviara','devices_ids': ['error:NotificationNotCreated']}";
    String requestForceNotificationAppNotFound = "{'alert': 'Nunca se enviara','devices_ids': ['error:AppNotFound']}";
    String requestOkDelia = "{\"alert\":\"Hola Chris soy Delia. Estoy probando...\",\"devices_ids\":[\"be0cbc4b90a805c0\"]}";

    String requestOk = "{'alert': 'Mensaje a enviar.','devices_ids': ['2b3c4d5f6g']}";
    String requestDeviceNotFound = "{'alert': 'Mensaje que no se envia.','devices_ids': ['el4']}";

    private HttpServer server;
    private WebTarget client;

    @Before
    public void setUp() throws Exception {
        // start the server
        int port = 9624;
        server = ServerRunner.startServer(false, port);
        // create the client
        Client c = ClientBuilder.newClient();

        // uncomment the following line if you want to enable
        // support for JSON in the client (you also have to uncomment
        // dependency on jersey-media-json module in pom.xml and ServerRunner.startServer())
        // --
        // c.configuration().enable(new org.glassfish.jersey.media.json.JsonJaxbFeature());

        client = c.target(ServerRunner.buildUri(port));
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
    public void testPostError403InvalidTokenNotNull() throws IOException {
    MultivaluedMap<String, Object> map = new MultivaluedHashMap<>();
        map.putSingle("X-TwinPush-REST-API-Token", "INVALID TOKEN");
        Response post = client.path("app1/notifications").request().headers(map).post(Entity.entity(requestOk, MediaType.APPLICATION_JSON_TYPE));
        String data = readResponse(post);
        assertEquals(403, post.getStatus());
        assertTrue(data.contains("InvalidToken"));
    }

    @Test
    public void testPostError404AppNotFound() throws IOException {
        MultivaluedMap<String, Object> map = new MultivaluedHashMap<>();
        map.putSingle("X-TwinPush-REST-API-Token", "TOKEN");
        Response post = client.path("appNOOOOOOOOOO/notifications").request().headers(map).post(Entity.entity(requestOk, MediaType.APPLICATION_JSON_TYPE));
        String data = readResponse(post);
        assertEquals(404, post.getStatus());
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
    public void testPostOkOldSchool() throws IOException {
        MultivaluedMap<String, Object> map = new MultivaluedHashMap<>();
        map.putSingle("X-TwinPush-REST-API-Token", "TOKEN");
        Response post = client.path("app1/notifications").request().headers(map).post(Entity.entity(requestOkOldSchool, MediaType.APPLICATION_JSON_TYPE));
        String data = readResponse(post);
        assertTrue(data.contains("\"id\": \"app1\""));
        assertTrue(data.contains("\"alert\": \"Test!\""));
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

    @Test
    public void testPostOkDelia() throws IOException {
        MultivaluedMap<String, Object> map = new MultivaluedHashMap<>();
        map.putSingle("X-TwinPush-REST-API-Token", "39ee8cd9dc3070a2bfe900e18bce58ac");
        Response post = client.path("app1/notifications").request().headers(map).post(Entity.entity(requestOkDelia, MediaType.APPLICATION_JSON_TYPE));
        String data = readResponse(post);
        assertTrue(data.contains("\"id\": \"app1\""));
        assertTrue(data.contains("\"alert\": \"Test!\""));
    }

    @Test
    public void testForceNotificationNotCreatedError() throws IOException {
        Response post = client.path("app1/notifications").request().post(Entity.entity(requestForceNotificationNotCreated, MediaType.APPLICATION_JSON_TYPE));
        String data = readResponse(post);
        assertEquals(422, post.getStatus());
        assertTrue(data.contains("NotificationNotCreated"));
    }

    @Test
    public void testForceNotificationForceInvalidCreatorToken() throws IOException {
        Response post = client.path("app1/notifications").request().post(Entity.entity(requestForceInvalidCreatorToken, MediaType.APPLICATION_JSON_TYPE));
        String data = readResponse(post);
        assertEquals(403, post.getStatus());
        assertTrue(data.contains("InvalidCreatorToken"));
    }

    @Test
    public void testForceAppNotFoundError() throws IOException {
        Response post = client.path("app1/notifications").request().post(Entity.entity(requestForceNotificationAppNotFound, MediaType.APPLICATION_JSON_TYPE));
        String data = readResponse(post);
        assertEquals(404, post.getStatus());
        assertTrue(data.contains("AppNotFound"));
    }

    private String readResponse(Response post) throws IOException {
        InputStream entity = (InputStream) post.getEntity();
        byte[] b = new byte[entity.available()];
        entity.read(b);
        String data = new String(b);
        System.out.println("Data read from client:" + data);
        return data;
    }
}
