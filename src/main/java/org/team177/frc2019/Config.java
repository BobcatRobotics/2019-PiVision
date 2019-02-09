package org.team177.frc2019;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Config Parses JSON File
 */
/*
   JSON format:
   {
       "team": <team number>,
       "ntmode": <"client" or "server", "client" if unspecified>
       "cameras": [
           {
               "name": <camera name>
               "path": <path, e.g. "/dev/video0">
               "pixel format": <"MJPEG", "YUYV", etc>   // optional
               "width": <video mode width>              // optional
               "height": <video mode height>            // optional
               "fps": <video mode fps>                  // optional
               "brightness": <percentage brightness>    // optional
               "white balance": <"auto", "hold", value> // optional
               "exposure": <"auto", "hold", value>      // optional
               "properties": [                          // optional
                   {
                       "name": <property name>
                       "value": <property value>
                   }
               ],
               "stream": {                              // optional
                   "properties": [
                       {
                           "name": <stream property name>
                           "value": <stream property value>
                       }
                   ]
               }
           }
       ]
   }
 */
public class Config {
    private String fileName = "/boot/frc.json";
    private AppConfig appConfig = new AppConfig();
    private List<CameraConfig> cameraConfigs = new ArrayList<>();
    private boolean readConfig = false;

    public Config() {
        readConfig = readConfigFile();
    }

    /**
     * Read configuration file.
     */
    protected JsonObject readBaseConfig() {
        // parse file
        JsonObject obj = null;
        JsonElement top;
        try {
            top = new JsonParser().parse(Files.newBufferedReader(Paths.get(fileName)));
        } catch (IOException ex) {
            logError("could not open file. " + ex);
            return obj;
        }

        // top level must be an object
        if (!top.isJsonObject()) {
            logError("JSON file has wrong structure");
            return obj;
        }
        obj = top.getAsJsonObject();
        return obj;
    }

    /**
     * Read configuration file Camera Section
     */
    public boolean readConfigFile() {
        JsonObject obj = readBaseConfig();
        // App Values
        // team number
        JsonElement teamElement = obj.get("team");
        if (teamElement == null) {
            logError("could not read team number");
            return false;
        }
        appConfig.setTeam(teamElement.getAsInt());

        // ntmode (optional)
        if (obj.has("ntmode")) {
            String str = obj.get("ntmode").getAsString();
            if ("client".equalsIgnoreCase(str)) {
                appConfig.setServer(false);
            } else if ("server".equalsIgnoreCase(str)) {
                appConfig.setServer(true);
            } else {
                logError("could not understand ntmode value '" + str + "'");
            }
        }

        // cameras
        JsonElement camerasElement = obj.get("cameras");
        if (camerasElement == null) {
            logError("could not read cameras");
            return false;
        }
        JsonArray cameras = camerasElement.getAsJsonArray();
        for (JsonElement camera : cameras) {
            if (!readCameraConfig(camera.getAsJsonObject())) {
                return false;
            }
        }
        return true;
    }

    /**
     * Read single camera configuration.
     */
    public boolean readCameraConfig(JsonObject config) {
        CameraConfig cam = new CameraConfig();

        // name
        JsonElement nameElement = config.get("name");
        if (nameElement == null) {
            logError("could not read camera name");
            return false;
        }
        cam.setName(nameElement.getAsString());

        // path
        JsonElement pathElement = config.get("path");
        if (pathElement == null) {
            logError("camera '" + cam.getName() + "': could not read path");
            return false;
        }
        cam.setPath(pathElement.getAsString());

        // stream properties
        cam.setStreamConfig(config.get("stream"));
        cam.setConfig(config);
        cameraConfigs.add(cam);
        return true;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
        // read configuration file
        readConfig = readConfigFile();
    }

    public String getFileName() {
        return this.fileName;
    }

    public AppConfig getAppConfig() {
        return this.appConfig;
    }

    public List<CameraConfig> getCameraConfigs() {
        return this.cameraConfigs;
    }

    public boolean isReadConfig() {
        return this.readConfig;
    }

    public boolean getReadConfig() {
        return this.readConfig;
    }

    public static void logError(String str) {
        System.err.println("Error - " + str);
    }

    public static void log(String str) {
        System.out.println("LOG - " + str);
    }

    @Override
    public String toString() {
        return "{" +
            " fileName='" + getFileName() + "'" +
            ", appConfig='" + getAppConfig() + "'" +
            ", cameraConfigs='" + getCameraConfigs() + "'" +
            ", readConfig='" + isReadConfig() + "'" +
            "}";
    }
}