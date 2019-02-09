
package org.team177.frc2019;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class CameraConfig  {
    private String name;
    private String path;
    private JsonObject config;
    private JsonElement streamConfig;

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return this.path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public JsonObject getConfig() {
        return this.config;
    }

    public void setConfig(JsonObject config) {
        this.config = config;
    }

    public JsonElement getStreamConfig() {
        return this.streamConfig;
    }

    public void setStreamConfig(JsonElement streamConfig) {
        this.streamConfig = streamConfig;
    }
 
    @Override
    public String toString() {
        return "{" +
            " name='" + getName() + "'" +
            ", path='" + getPath() + "'" +
            ", config='" + getConfig() + "'" +
            ", streamConfig='" + getStreamConfig() + "'" +
            "}";
    }
}