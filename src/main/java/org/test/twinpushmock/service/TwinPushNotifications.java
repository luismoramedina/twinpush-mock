package org.test.twinpushmock.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.StringMap;
import org.test.twinpushmock.data.Model;
import org.test.twinpushmock.exception.ExceptionBuilder;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;

@Path("{applicationId}/notifications")
public class TwinPushNotifications {

    ExceptionBuilder exceptionBuilder = new ExceptionBuilder();

    /**
     * @param applicationId path param
     * @param twinPushToken json payload as querystring "X-TwinPush-REST-API-Key-Creator" param
     * @param payload       json payload as querystring "payload" param
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String notificationReceivedGet(
            @PathParam("applicationId") String applicationId,
            @QueryParam("X-TwinPush-REST-API-Key-Creator") String twinPushToken,
            @QueryParam("payload") String payload) {

        return doJob(applicationId, twinPushToken, payload);
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public String notificationReceivedPost(
            @PathParam("applicationId") String applicationId,
            @HeaderParam("X-TwinPush-REST-API-Key-Creator") String twinPushToken,
            byte[] payload) {

        return doJob(applicationId, twinPushToken, new String(payload));
    }

    private String doJob(String applicationId, String twinPushToken, String payload) {


        Gson gson = getGson();

        System.out.format("\n###############################" +
                        "\n%s --> Message received from application '%s' and token '%s'" +
                        "\n%s" +
                        "\n###############################\n",
                new java.util.Date().toString(), applicationId, twinPushToken, payload);

        ArrayList<String> devices_ids;
        try {
            StringMap data = (StringMap) gson.fromJson(payload, Object.class);
            StringMap notification = (StringMap) data.get("notification");

            if (notification == null) {
                notification = data;
            }

            String alert = (String) notification.get("alert");
            devices_ids = (ArrayList<String>) notification.get("devices_ids");
        } catch (Exception e) {
            e.printStackTrace();
            throw exceptionBuilder.getNotificationNotCreated();
        }

        validateRequest(applicationId, twinPushToken, devices_ids);


        return getOkResponse(applicationId);
    }


    private String getOkResponse(String applicationId) {
        String response = "{\n" +
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
        System.out.println("response = " + response);
        return response;
    }

    private void validateRequest(String applicationId, String twinPushToken, ArrayList<String> deviceIds) {

        if (deviceIds != null && deviceIds.size() > 0) {
            for (String deviceId : deviceIds) {
                if (deviceId.startsWith("error:")) {
                    throw exceptionBuilder.getExceptionByType(deviceId.split(":")[1]);
                }
            }
        }

        if (twinPushToken == null || twinPushToken.isEmpty() || !Model.existsToken(twinPushToken)) {
            throw exceptionBuilder.getInvalidTokenException();
        }

        if (!Model.existsApp(applicationId)) {
            throw exceptionBuilder.getAppNotFound();
        }

        if (!Model.existsDevices(deviceIds)) {
            throw exceptionBuilder.getDeviceNotFound();
        }

    }


    public Gson getGson() {
        com.google.gson.GsonBuilder gsonBuilder = new GsonBuilder();
        return gsonBuilder.create();
    }


}
