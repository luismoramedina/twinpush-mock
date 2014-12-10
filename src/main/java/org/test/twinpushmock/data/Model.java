package org.test.twinpushmock.data;

import java.util.ArrayList;

public class Model {

    static ArrayList<String> apps = new ArrayList<>();
    static ArrayList<String> devices = new ArrayList<>();
    static ArrayList<String> tokens = new ArrayList<>();

    static {
        apps.add("app1");
        apps.add("app2");
        apps.add("pruebaAppId");
        apps.add("ebec1bf4a328564d");

        devices.add("dev1");
        devices.add("device1");
        devices.add("device2");
        devices.add("2b3c4d5f6g");
        devices.add("be0cbc4b90a805c0");

        tokens.add("token1");
        tokens.add("token2");
        tokens.add("token3");
        tokens.add("token4");
        tokens.add("asdfg");
        tokens.add("asdf");
        tokens.add("12345");
        tokens.add("TOKEN");
        tokens.add("39ee8cd9dc3070a2bfe900e18bce58ac");
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

    public static boolean existsToken(String token) {
        return tokens.contains(token);
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
