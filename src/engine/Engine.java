package engine;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import com.esotericsoftware.minlog.Log;

import backends.AudioBackend;
import backends.RenderingBackend;
import backends.openal.ALAudioBackend;
import backends.opengl.GLRenderingBackend;
import engine.input.Keyboard;
import engine.input.Mouse;

public class Engine {
	
	private Settings settings;
	
	private RenderingBackend renderingBackend;
	
	private AudioBackend audioBackend;
	
	private EngineFunction init, update;
	
	private long lastSecond = 0;
	
	private int frames;
	
//	private float totalFrames = 0;
	
//	private int cycles = 0;
	
	private int lastFPS = 0;
	
	private Keyboard keyboard = new Keyboard();
	
	private Mouse mouse = new Mouse();
	
	private FileDropCallback fileDropCallback = (files) -> {
		// Ignore the files
	};
	
	public Engine(Settings settings) {
		this.settings = settings;
		switch (settings.renderingBackend) {
			case Settings.OPENGL_BACKEND:
				renderingBackend = new GLRenderingBackend();
				break;
		}
		switch (settings.audioBackend) {
			case Settings.OPENAL_BACKEND:
				audioBackend = new ALAudioBackend();
				break;
		}
	}
	
	public void setFileDropCallback(FileDropCallback fileDropCallback) {
		this.fileDropCallback = fileDropCallback;
	}
	
	public FileDropCallback getFileDropCallback() {
		return fileDropCallback;
	}
	
	public Keyboard getKeyboard() {
		return keyboard;
	}
	
	public Mouse getMouse() {
		return mouse;
	}
	
	public InputStream getResource(String path) {
		InputStream classResource = getClass().getClassLoader().getResourceAsStream(path);
		if (classResource != null)
			return classResource;
		String filePath = "res/" + path;
		try {
			FileInputStream fileResource = new FileInputStream(filePath);
			return fileResource;
		} catch (FileNotFoundException e) {
			// File not found
		}
		Log.warn("Cannot locate resource: " + path);
		return null;
	}
	
	public RenderingBackend getRenderingBackend() {
		return renderingBackend;
	}
	
	public AudioBackend getAudioBackend() {
		return audioBackend;
	}
	
	public void setInit(EngineFunction function) {
		init = function;
	}
	
	public void setUpdate(EngineFunction function) {
		update = function;
	}
	
	public float getFPS() {
		return lastFPS;
	}
	
	public void run() {
		renderingBackend.createDisplay(settings, keyboard, mouse, this);
		audioBackend.createContext();
		init.invoke(this);
		lastSecond = System.currentTimeMillis();
		renderingBackend.showDisplay();
		while (renderingBackend.isOpen()) {
			renderingBackend.prepareContext();
			update.invoke(this);
			renderingBackend.updateContext();
			audioBackend.updateContext();
			if (System.currentTimeMillis() - lastSecond > 1000) {
				lastSecond = System.currentTimeMillis();
//				totalFrames += frames;
//				cycles++;
				lastFPS = frames;
				frames = 0;
			}
			frames++;
		}
		renderingBackend.hideDisplay();
		audioBackend.destroyContext();
		renderingBackend.destroyDisplay();
	}

	public Settings getSettings() {
		return settings;
	}

}
