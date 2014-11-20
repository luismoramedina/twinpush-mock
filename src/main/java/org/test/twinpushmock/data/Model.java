package org.test.twinpushmock.data;

import java.util.ArrayList;

public class Model {

    static ArrayList<String> apps = new ArrayList<>();
    static ArrayList<String> devices = new ArrayList<>();

    static {
        apps.add("app1");
        apps.add("app2");

        devices.add("device1");
        devices.add("device2");
        devices.add("2b3c4d5f6g");
    }


    public static String getApp(String applicationId) {
        return existsApp(applicationId) ? applicationId : null;
    }

    public static boolean existsApp(String applicationId) {
        return apps.contains(applicationId);
    }

    public static boolean existsDevice(String deviceId) {
        return devices.contains(deviceId);
    }

    public static boolean existsDevices(ArrayList<String> deviceIds) {
        for (String thisDevice : deviceIds) {
            if (!existsDevice(thisDevice)) {
                return false;
            }
        }
        return true;
    }

    public static String getDevice(String deviceId) {
        return existsDevice(deviceId) ? deviceId : null;
    }


}
