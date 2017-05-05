package engine.audio;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.esotericsoftware.minlog.Log;

import engine.Engine;
import engine.RawAsset;

public class Soundtrack {
	
	private ArrayList<Music> tracks = new ArrayList<>();
	
	private int trackPointer = 0;
	
	public Soundtrack(InputStream stream, Engine engine, AudioFormat format) {
		try {
			ZipInputStream zipFile = new ZipInputStream(stream);
			RawAsset rawFile = new RawAsset(zipFile);
			ZipEntry zipEntry = zipFile.getNextEntry();
			while (zipEntry != null) {
				tracks.add(engine.getAudioBackend().loadMusic(rawFile, format));
				zipFile.closeEntry();
				zipEntry = zipFile.getNextEntry();
			}
		} catch (IOException e) {
			Log.error("Error while loading soundtrack.", e);
		}
	}
	
	public void update(Engine engine) {
		if (trackPointer >= tracks.size()) {
			trackPointer = 0;
		}
		if (engine.getAudioBackend().isBackgroundMusicDonePlaying()) {
			engine.getAudioBackend().setBackgroundMusic(tracks.get(trackPointer));
			trackPointer++;
		}
	}

}
