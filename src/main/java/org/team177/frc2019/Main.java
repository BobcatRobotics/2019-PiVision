package org.team177.frc2019;

/*----------------------------------------------------------------------------*/

/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import edu.wpi.cscore.MjpegServer;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.cscore.VideoSource;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.vision.VisionPipeline;
import edu.wpi.first.vision.VisionThread;

import org.opencv.core.Mat;

import java.util.ArrayList;
import java.util.List;

public final class Main {

  private static Config cfg = new Config();

  private Main() {
  }

  /**
   * Start running the camera.
   */
  public static VideoSource startCamera(CameraConfig config) {
    Config.log("Starting camera '" + config.getName() + "' on " + config.getPath());
    CameraServer inst = CameraServer.getInstance();
    UsbCamera camera = new UsbCamera(config.getName(), config.getPath());
    MjpegServer server = inst.startAutomaticCapture(camera);

    Gson gson = new GsonBuilder().create();

    camera.setConfigJson(gson.toJson(config.getConfig()));
    camera.setConnectionStrategy(VideoSource.ConnectionStrategy.kKeepOpen);

    if (config.getStreamConfig() != null) {
      server.setConfigJson(gson.toJson(config.getStreamConfig()));
    }

    return camera;
  }

  /**
   * Example pipeline.
   */
  public static class MyPipeline implements VisionPipeline {
    public int val;

    @Override
    public void process(Mat mat) {
      val += 1;
    }
  }

  /**
   * Main.
   */
  public static void main(String... args) {

    if (args.length > 0) {
      Config.log("Reading configuration file " + args[0]);
      cfg.setFileName(args[0]);
    }

    // read configuration
    if (!cfg.isReadConfig()) {
      Config.logError("There was a problem with configuration file " + args[0]);
      return;
    }

    AppConfig appCfg = cfg.getAppConfig();
    List<CameraConfig> camCfgs = cfg.getCameraConfigs();

    // start NetworkTables
    NetworkTableInstance ntinst = NetworkTableInstance.getDefault();
    if (appCfg.isServer()) {
      Config.log("Setting up NetworkTables server");
      ntinst.startServer();
    } else {
      Config.log("Setting up NetworkTables client for team " + appCfg.getTeam());
      ntinst.startClientTeam(appCfg.getTeam());
    }

    // start cameras
    List<VideoSource> cameras = new ArrayList<>();
    for (CameraConfig cameraConfig : camCfgs) {
      cameras.add(startCamera(cameraConfig));
    }

    // start image processing on camera 0 if present
    Config.log("KEVIN - Starting VisionThread.");
    Config.log("KEVIN - Nbr of cameras is " + cameras.size());
    if (cameras.size() >= 1) {
      Config.log("KEVIN - First camera is " + cameras.get(0).toString());
      VisionThread visionThread = new VisionThread(cameras.get(0), new MyPipeline(), pipeline -> {
        // do something with pipeline results
      });
      /*
       * something like this for GRIP: VisionThread visionThread = new
       * VisionThread(cameras.get(0), new GripPipeline(), pipeline -> { ... });
       */
      visionThread.start();
    }

    // loop forever
    for (;;) {
      try {
        Thread.sleep(10000);
      } catch (InterruptedException ex) {
        return;
      }
    }
  }
}
