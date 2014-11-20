package org.test.twinpushmock.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.StringMap;
import org.test.twinpushmock.data.Model;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;

@Path("{applicationId}/notifications")
public class TwinPushNotifications {


    /**
     * @param applicationId path param
     * @param twinPushToken json payload as querystring "X-TwinPush-REST-API-Token" param
     * @param payload json payload as querystring "payload" param
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String notificationReceivedGet(
            @PathParam("applicationId") String applicationId,
            @QueryParam("X-TwinPush-REST-API-Token") String twinPushToken,
            @QueryParam("payload") String payload) {

        return doJob(applicationId, twinPushToken, payload);
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public String notificationReceivedPost(
            @PathParam("applicationId") String applicationId,
            @HeaderParam("X-TwinPush-REST-API-Token") String twinPushToken,
            byte[] payload) {

        return doJob(applicationId, twinPushToken, new String(payload));
    }

    private String doJob(String applicationId, String twinPushToken, String payload) {


        Gson gson = getGson();

        System.out.format("\nMessage received from application %s... \n %s", applicationId, payload);

        ArrayList<String> devices_ids;
        try {
            StringMap data = (StringMap) gson.fromJson(payload, Object.class);
            StringMap notification = (StringMap) data.get("notification");
            String alert = (String) notification.get("alert");
            devices_ids = (ArrayList<String>) notification.get("devices_ids");
        } catch (Exception e) {
            throw new UnprocessableEntityException(getErrorResponse(
                    "NotificationNotCreated",
                    "Some of the parameters given when trying to create a notification is not valid"));
        }

        validateRequest(applicationId, twinPushToken, devices_ids);


        return getOkResponse(applicationId);
    }

    private String getErrorResponse(final String errorType, final String detailedErrorMessage) {
        return "{\n" +
                "  \"errors\": {\n" +
                "   \"type\": \"" + errorType + "\"\n" +
                "   \"message\": \"" + detailedErrorMessage + "\",\n" +
                " }\n" +
                "}";
    }

    private String getOkResponse(String applicationId) {
        return "{\n" +
                "  \"objects\": [\n" +
                "   {\n" +
                "     \"id\": \"" + applicationId + "\",\n" +
                "     \"sound\": \"default\",\n" +
                "     \"alert\": \"Test!\",\n" +
                "     \"badge\": \"4\",\n" +
                "     \"send_since\": \"2013-10-03 15:14:33 +0000\",\n" +
                "     \"created_at\": \"2013-10-03 15:14:33 +0000\",\n" +
                "     \"updated_at\": \"2013-10-03 15:14:33 +0000\",\n" +
                "     \"type\": \"Notification\"\n" +
                "   }\n" +
                " ],\n" +
                " \"references\": []\n" +
                "}";
    }

    private void validateRequest(String applicationId, String twinPushToken, ArrayList<String> deviceIds) {

        if (twinPushToken == null || twinPushToken.isEmpty()) {
            throw new ForbiddenException(getErrorResponse(
                    "InvalidToken",
                    "API Key Token is not valid or does not match with App ID"));
        }

        if (!Model.existsApp(applicationId)) {
            throw new NotFoundException(getErrorResponse(
                    "AppNotFound",
                    "Application not found for given application_id"));
        }

        if (!Model.existsDevices(deviceIds)) {
            throw new NotFoundException(getErrorResponse(
                    "DeviceNotFound",
                    "Device not found for given device_id"));
        }

    }

    public Gson getGson() {
        com.google.gson.GsonBuilder gsonBuilder = new GsonBuilder();
        return gsonBuilder.create();
    }

    public class ForbiddenException extends WebApplicationException {
        public ForbiddenException(String message) {
            super(Response.status(Response.Status.FORBIDDEN).entity(message).build());
        }
    }

    public class NotFoundException extends WebApplicationException {
        public NotFoundException(String message) {
            super(Response.status(Response.Status.NOT_FOUND).entity(message).build());
        }
    }

    public class UnprocessableEntityException extends WebApplicationException {
        public UnprocessableEntityException(String message) {
            super(Response.status(422).entity(message).build());
        }
    }

}
