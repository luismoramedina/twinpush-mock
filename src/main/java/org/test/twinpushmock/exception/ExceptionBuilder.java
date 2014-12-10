package org.test.twinpushmock.exception;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lmora on 05/12/2014.
 */
public class ExceptionBuilder {

    private static Map exceptionMap = new HashMap();

    public ExceptionBuilder() {
        exceptionMap.put("InvalidToken", new ForbiddenException(getErrorResponse(
                "InvalidToken",
                "API Key Token is not valid or does not match with App ID")));
        exceptionMap.put("InvalidCreatorToken", new ForbiddenException(getErrorResponse(
                "InvalidCreatorToken",
                "Creator api key token is not valid or does not match with the app id")));
        exceptionMap.put("NotificationNotCreated", new UnprocessableEntityException(getErrorResponse(
                "NotificationNotCreated",
                "Some of the parameters given when trying to create a notification is not valid")));
        exceptionMap.put("DeviceNotFound", new NotFoundException(getErrorResponse(
                "DeviceNotFound",
                "Device not found for given device_id")));
        exceptionMap.put("AppNotFound", new NotFoundException(getErrorResponse(
                "AppNotFound",
                "Application not found for given application_id")));
    }

    public WebApplicationException getExceptionByType(String type) {
        return (WebApplicationException) exceptionMap.get(type);
    }

    public ForbiddenException getInvalidTokenException() {
        return (ForbiddenException) exceptionMap.get("InvalidToken");
    }

    public UnprocessableEntityException getNotificationNotCreated() {
        return (UnprocessableEntityException) exceptionMap.get("NotificationNotCreated");
    }


    public NotFoundException getDeviceNotFound() {
        return (NotFoundException) exceptionMap.get("DeviceNotFound");
    }

    public NotFoundException getAppNotFound() {
        return (NotFoundException) exceptionMap.get("AppNotFound");
    }

    private String getErrorResponse(final String errorType, final String detailedErrorMessage) {
        String error = "{\n" +
                "  \"errors\": {\n" +
                "   \"type\": \"" + errorType + "\"\n" +
                "   \"message\": \"" + detailedErrorMessage + "\",\n" +
                " }\n" +
                "}";
        System.out.println("Return ERROR:" + error);
        return error;
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
