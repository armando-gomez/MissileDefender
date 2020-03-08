package com.armandogomez.missledefender;

import android.animation.AnimatorSet;

import java.util.ArrayList;

public class MissileMaker implements Runnable {
	private static final String TAG = "MissileMaker";
	private MainActivity mainActivity;
	private int screenWidth, screenHeight;

	private ArrayList<Missile> activeMissiles = new ArrayList<>();

	private boolean isRunning;
	private int missileCount;
	private int delayBetweenMissiles = 3000;
	private int missilesPerLevel = 10;
	private int currentLevel;

	MissileMaker(MainActivity mainActivity, int screenWidth, int screenHeight) {
		this.mainActivity = mainActivity;
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;
	}

	void setRunning(boolean running) {
		isRunning = running;
		ArrayList<Missile> temp = new ArrayList<>(activeMissiles);
		for(Missile m: temp) {
			m.stop();
		}
	}

	@Override
	public void run() {
		setRunning(true);
		missileCount = 0;
		currentLevel = 1;
		try {
			Thread.sleep((long) (delayBetweenMissiles * 0.5));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		while(isRunning) {
			makeMissile(delayBetweenMissiles);
			missileCount++;
			if(missileCount > missilesPerLevel) {
				increaseLevel();
				missileCount = 0;
			}
		}

		int sleepTime = getSleepTime();

		try {
			Thread.sleep(sleepTime);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	int getSleepTime() {
		double random = Math.random();
		if(random < 0.1) {
			return 1000;
		} else if(random < 0.2) {
			return (int) (0.5 * delayBetweenMissiles);
		} else {
			return delayBetweenMissiles;
		}
	}

	void increaseLevel() {
		currentLevel++;
		delayBetweenMissiles -= 500;
		if(delayBetweenMissiles <= 0) {
			delayBetweenMissiles = 1000;
		}

		mainActivity.setLevel(currentLevel);
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	void makeMissile(long delay) {
		Missile missile = new Missile(screenWidth, screenHeight, delayBetweenMissiles, mainActivity);
		activeMissiles.add(missile);
		mainActivity.addMissile(missile);

		final AnimatorSet set = missile.getSet();
		mainActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				SoundPlayer.getInstance().start("launch_missile");
				set.start();
			}
		});
	}

	public void removeMissile(Missile m) {
		activeMissiles.remove(m);
	}
}
