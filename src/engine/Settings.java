package engine;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;

import org.joml.Vector3f;

import utils.IOUtils;

public class Settings {
	
	public static final int OPENGL_BACKEND = 0x10;
	
	public static final int OPENAL_BACKEND = 0x20;
	
	public final int width;
	
	public final int height;

	public final int renderingBackend;
	
	public final int audioBackend;
	
	public final float mouseSensitivity;

	public final float fov;
	
	public final float aspectRatio;
	
	public final float nearPlane;
	
	public final float farPlane;
	
	public final boolean vSync;
	
	public final boolean fullscreen;
	
	public final boolean nativeResolution;
	
	public final boolean resizable;
	
	public final boolean multisample;
	
	public final String title;
	
	public final Vector3f backgroundColor;
	
	public final String[] windowIcons;
	
	private static final String MOUSE_SENSITIVITY_PROPERTY = "mouseSensitivity";
	private static final String FOV_PROPERTY = "fov";
	private static final String VSYNC_PROPERTY = "vSync";
	private static final String FULLSCREEN_PROPERTY = "fullscreen";
	
	public Settings(InputStream source) {
		width = 1280;
		height = 720;
		renderingBackend = OPENGL_BACKEND;
		audioBackend = OPENAL_BACKEND;
		aspectRatio = (float) width / (float) height;
		nearPlane = 0.1f;
		farPlane = 1000f;
		nativeResolution = true;
		resizable = false;
		multisample = false;
		title = "Application";
		backgroundColor = new Vector3f(135f / 256f, 206f / 256f, 250f / 256f);
		windowIcons = new String[] {
			"firefox_icon.png"
		};
		if (source != null) {
			HashMap<String, String> settings = new HashMap<>();
			IOUtils.readProperties(source, settings);
			mouseSensitivity = Float.parseFloat(settings.getOrDefault(MOUSE_SENSITIVITY_PROPERTY, "0.5"));
			fov = Float.parseFloat(settings.getOrDefault(FOV_PROPERTY, "70"));
			vSync = Boolean.parseBoolean(settings.getOrDefault(VSYNC_PROPERTY, "false"));
			fullscreen = Boolean.parseBoolean(settings.getOrDefault(FULLSCREEN_PROPERTY, "false"));
		} else {
			mouseSensitivity = 0.5f;
			fov = 70;
			vSync = false;
			fullscreen = false;
		}
	}
	
	public void write(OutputStream destination) {
		HashMap<String, String> settings = new HashMap<>();
		settings.put(MOUSE_SENSITIVITY_PROPERTY, String.valueOf(mouseSensitivity));
		settings.put(FOV_PROPERTY, String.valueOf(fov));
		settings.put(VSYNC_PROPERTY, String.valueOf(vSync));
		settings.put(FULLSCREEN_PROPERTY, String.valueOf(fullscreen));
		IOUtils.writeProperties(destination, settings);
	}
	
}
