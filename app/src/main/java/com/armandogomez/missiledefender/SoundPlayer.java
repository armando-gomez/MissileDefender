package com.armandogomez.missiledefender;

import android.content.Context;
import android.media.SoundPool;
import android.util.Log;

import java.util.HashMap;
import java.util.HashSet;

public class SoundPlayer {
	private static final String TAG = "SoundPlayer";
	private static SoundPlayer instance;
	private SoundPool soundPool;
	private static final int MAX_STREAMS = 10;

	private HashSet<Integer> loaded = new HashSet<>();
	private HashSet<String> loopSet = new HashSet<>();

	private HashMap<String, Integer> soundNameToResource = new HashMap<>();

	private SoundPlayer() {
		SoundPool.Builder builder = new SoundPool.Builder();
		builder.setMaxStreams(MAX_STREAMS);

		this.soundPool = builder.build();

		this.soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
			@Override
			public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
				Log.d(TAG, "onLoadComplete: #" + sampleId + " " + status);
				instance.loaded.add(sampleId);
				if(soundNameToResource.get("background") == sampleId) {
					start("background");
				}
			}
		});
	}

	static SoundPlayer getInstance() {
		if(instance == null) {
			instance = new SoundPlayer();
		}
		return instance;
	}

	void setupSound(Context context, String id, int resource, boolean loop) {
		int soundId = soundPool.load(context, resource, 1);
		soundNameToResource.put(id, soundId);
		if(loop) {
			loopSet.add(id);
		}

		Log.d(TAG, "setupSound: " + id + ": #" + soundId);
		try {
			Thread.sleep(300);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	void start(final String id) {
		if (!checkSoundLoad(id)) {
			Log.d(TAG, "start: SOUND NOT LOADED: " + id);
			return;
		}

		int loop = 0;
		if(loopSet.contains(id)) {
			loop = -1;
		}

		Integer resId = soundNameToResource.get(id);
		if (resId == null)
			return;
		soundPool.play(resId, 1f, 1f, 1, loop, 1f);
	}

	boolean checkSoundLoad(final String id) {
		int loadId = soundNameToResource.get(id);
		if (!loaded.contains(loadId)) {
			return false;
		}
		return true;
	}
}
